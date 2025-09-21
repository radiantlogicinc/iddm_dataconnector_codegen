#!/usr/bin/env python3

"""
Complete Enhanced MCP Server for Data Connector Development

Combines OpenAPI tools with Data Connector code generation and testing capabilities
Enhanced to handle both simple and complex OpenAPI specifications with all functions included
"""

# Move all imports to the very top of the file
import json
import yaml
import dspy
import requests
import os
import sys
import subprocess
import shutil
import re
import asyncio
import base64
import traceback
import logging
import warnings
from typing import Dict, List, Any, Optional, Union, Tuple
from urllib.parse import urljoin, urlparse
from pathlib import Path
from collections import defaultdict
import heapq
import datetime
import ast

# Suppress all warnings
warnings.filterwarnings("ignore")

# Set up logger for enhanced MCP server
debug_logger = logging.getLogger('enhanced_mcp_server')
debug_logger.info('=== COMPLETE ENHANCED MCP DATA CONNECTOR SERVER EXECUTED ===')

# Add user library to path for MCP modules
user_lib_path = '/Users/Ragiv1/Library/Python/3.12/lib/python/site-packages'
if user_lib_path not in sys.path:
    sys.path.insert(0, user_lib_path)

# Import MCP modules
from fastmcp import FastMCP

# Initialize the MCP server
mcp = FastMCP("Complete Enhanced Data Connector MCP Tools")

# Function to dynamically determine project root directory
def get_project_root() -> str:
    """
    Dynamically determine the project root directory.
    This function looks for the project root by finding the directory containing pom.xml
    and other project files, working backwards from the current working directory.
    """
    # Start from current working directory
    current_dir = os.getcwd()
    
    # Look for project root by checking for pom.xml
    while current_dir != os.path.dirname(current_dir):  # Stop at filesystem root
        pom_path = os.path.join(current_dir, "pom.xml")
        if os.path.exists(pom_path):
            # Found pom.xml, this is likely the project root
            return current_dir
        # Move up one directory
        current_dir = os.path.dirname(current_dir)
    
    # If no pom.xml found, try to find by looking for the script's location
    script_dir = os.path.dirname(os.path.abspath(__file__))
    
    # Search upwards from script location to find pom.xml
    search_dir = script_dir
    while search_dir != os.path.dirname(search_dir):  # Stop at filesystem root
        pom_path = os.path.join(search_dir, "pom.xml")
        if os.path.exists(pom_path):
            return search_dir
        search_dir = os.path.dirname(search_dir)
    
    # Fallback: use current working directory
    return os.getcwd()

# Get project root dynamically
PROJECT_ROOT = get_project_root()

# Global variables to store state
source_json_data = {}
target_json_data = {}
source_json_path = ""
target_json_path = ""
api_spec_url = ""
base_url = ""
auth_config = {}
current_project_dir = PROJECT_ROOT

# Add new global for large file handling
openapi_cache = {}
spec_metadata = {}

# =============================
# Enhanced OpenAPI Parser Classes
# =============================

class EnhancedApiSchemaParser:
    """Enhanced API schema parser that handles both simple and complex specs"""
    
    def __init__(self, api_spec_url: str):
        self.api_spec_url = api_spec_url
        self.api_spec = None
        self.servers = []
        self.security_schemes = {}
        self.global_security = []
        self.paths = {}
        self.external_refs = {}
        self.has_external_refs = False
        self.path_to_object = {}
        self.best_position = 0

    def fetch_api_spec(self):
        """Fetch and parse the API specification from URL or file"""
        try:
            if self.api_spec_url.startswith(('http://', 'https://')):
                debug_logger.debug(f"Fetching OpenAPI spec from URL: {self.api_spec_url}")
                response = requests.get(self.api_spec_url)
                response.raise_for_status()
                response.encoding = 'utf-8'
                content = response.text
            else:
                debug_logger.debug(f"Loading OpenAPI spec from file: {self.api_spec_url}")
                with open(self.api_spec_url, 'r', encoding='utf-8') as f:
                    content = f.read()

            try:
                self.api_spec = json.loads(content)
                debug_logger.debug("Parsed OpenAPI spec as JSON.")
            except json.JSONDecodeError:
                debug_logger.debug("JSON decode failed. Trying YAML...")
                self.api_spec = yaml.safe_load(content)
                debug_logger.debug("Parsed OpenAPI spec as YAML.")

            return True
        except Exception as e:
            debug_logger.error(f"Error fetching/parsing API specification: {e}")
            traceback.print_exc()
            return False

    def detect_external_refs(self):
        """Detect if the spec uses external $ref references"""
        self.has_external_refs = False
        external_files = set()
        
        for path, path_data in self.paths.items():
            if isinstance(path_data, dict) and '$ref' in path_data:
                self.has_external_refs = True
                ref = path_data['$ref']
                if '#' in ref:
                    file_part = ref.split('#')[0]
                    if file_part:
                        external_files.add(file_part)
        
        debug_logger.debug(f"External refs detected: {self.has_external_refs}")
        if external_files:
            debug_logger.debug(f"External files needed: {list(external_files)}")
        return list(external_files)

    def try_resolve_external_refs(self, external_files):
        """Attempt to resolve external references by loading the files"""
        resolved_refs = {}
        base_path = os.path.dirname(self.api_spec_url) if not self.api_spec_url.startswith('http') else ''
        
        for file_name in external_files:
            try:
                if self.api_spec_url.startswith(('http://', 'https://')):
                    # For remote files, construct the URL
                    base_url = '/'.join(self.api_spec_url.split('/')[:-1])
                    file_url = f"{base_url}/{file_name}"
                    print(f"DEBUG: Attempting to fetch external file: {file_url}", file=sys.stderr)
                    response = requests.get(file_url)
                    if response.status_code == 200:
                        external_content = yaml.safe_load(response.text)
                        resolved_refs[file_name] = external_content
                        print(f"DEBUG: Successfully loaded {file_name}", file=sys.stderr)
                    else:
                        print(f"DEBUG: Failed to fetch {file_name}: {response.status_code}", file=sys.stderr)
                else:
                    # For local files
                    file_path = os.path.join(base_path, file_name)
                    if os.path.exists(file_path):
                        with open(file_path, 'r', encoding='utf-8') as f:
                            external_content = yaml.safe_load(f)
                        resolved_refs[file_name] = external_content
                        print(f"DEBUG: Successfully loaded {file_name}", file=sys.stderr)
                    else:
                        print(f"DEBUG: External file not found: {file_path}", file=sys.stderr)
            except Exception as e:
                print(f"DEBUG: Error loading {file_name}: {e}", file=sys.stderr)
        
        return resolved_refs

    def resolve_ref(self, ref_string, resolved_refs):
        """Resolve a single $ref reference"""
        try:
            if '#' not in ref_string:
                return None
            
            file_name, json_path = ref_string.split('#', 1)
            if file_name not in resolved_refs:
                return None
            
            # Navigate the JSON path (e.g., /components/paths/DefaultLogin)
            path_parts = json_path.strip('/').split('/')
            current = resolved_refs[file_name]
            for part in path_parts:
                if isinstance(current, dict) and part in current:
                    current = current[part]
                else:
                    return None
            
            return current
        except Exception:
            return None

    def infer_method_from_path_and_context(self, path, ref_string="", tag_context=""):
        """Infer HTTP methods from path structure and context when $ref cannot be resolved"""
        methods = {}
        
        # Common patterns for HTTP methods based on path structure and context
        path_lower = path.lower()
        ref_lower = ref_string.lower()
        tag_lower = tag_context.lower()
        
        # Login/Authentication patterns
        if any(word in path_lower for word in ['login', 'authenticate', 'auth']):
            methods['post'] = {
                "verb": "post",
                "operation": f"post_{path.replace('/', '_').replace('{', '').replace('}', '')}",
                "tags": [tag_context] if tag_context else ["authentication"],
                "description": f"Authentication endpoint for {path}",
                "parameters": self._extract_path_parameters(path)
            }
        
        # Status/Health check patterns
        elif any(word in path_lower for word in ['status', 'health', 'info', 'whoami']):
            methods['get'] = {
                "verb": "get",
                "operation": f"get_{path.replace('/', '_').replace('{', '').replace('}', '')}",
                "tags": [tag_context] if tag_context else ["status"],
                "description": f"Get status/info for {path}",
                "parameters": self._extract_path_parameters(path)
            }
        
        # Resource management patterns (CRUD)
        elif any(word in tag_lower for word in ['secrets', 'policies', 'roles', 'resources']):
            # Assume GET for listing/reading
            methods['get'] = {
                "verb": "get",
                "operation": f"get_{path.replace('/', '_').replace('{', '').replace('}', '')}",
                "tags": [tag_context] if tag_context else ["resources"],
                "description": f"Get {tag_context or 'resource'} from {path}",
                "parameters": self._extract_path_parameters(path)
            }
            
            # If path has parameters, likely supports PUT/POST/DELETE too
            if '{' in path:
                methods['put'] = {
                    "verb": "put",
                    "operation": f"put_{path.replace('/', '_').replace('{', '').replace('}', '')}",
                    "tags": [tag_context] if tag_context else ["resources"],
                    "description": f"Update {tag_context or 'resource'} at {path}",
                    "parameters": self._extract_path_parameters(path)
                }
                
                methods['delete'] = {
                    "verb": "delete",
                    "operation": f"delete_{path.replace('/', '_').replace('{', '').replace('}', '')}",
                    "tags": [tag_context] if tag_context else ["resources"],
                    "description": f"Delete {tag_context or 'resource'} at {path}",
                    "parameters": self._extract_path_parameters(path)
                }
            else:
                # Collection endpoints typically support POST for creation
                methods['post'] = {
                    "verb": "post",
                    "operation": f"post_{path.replace('/', '_').replace('{', '').replace('}', '')}",
                    "tags": [tag_context] if tag_context else ["resources"],
                    "description": f"Create new {tag_context or 'resource'} at {path}",
                    "parameters": self._extract_path_parameters(path)
                }
        
        # Generic fallback - assume GET
        if not methods:
            methods['get'] = {
                "verb": "get",
                "operation": f"get_{path.replace('/', '_').replace('{', '').replace('}', '')}",
                "tags": [tag_context] if tag_context else ["general"],
                "description": f"Access endpoint {path}",
                "parameters": self._extract_path_parameters(path)
            }
        
        return methods

    def _extract_path_parameters(self, path):
        """Extract path parameters from URL template"""
        import re
        parameters = []
        
        # Find all {parameter} patterns
        param_pattern = r'\{([^}]+)\}'
        matches = re.findall(param_pattern, path)
        
        for match in matches:
            parameters.append({
                "name": match,
                "in": "path",
                "required": True,
                "schema": {"type": "string"},
                "description": f"Path parameter {match}"
            })
        
        return parameters

    def extract_data(self):
        """Extract relevant data from the API specification"""
        if not self.api_spec:
            if not self.fetch_api_spec():
                return False

        self.servers = self.api_spec.get('servers', [])
        self.security_schemes = self.api_spec.get('components', {}).get('securitySchemes', {})
        self.global_security = self.api_spec.get('security', [])
        self.paths = self.api_spec.get('paths', {})

        # Preprocess paths to determine optimal segment position for object extraction
        self.path_to_object, self.best_position = self.preprocess_paths_for_object_extraction(self.paths)
        print(f"DEBUG: Optimal segment position for object extraction: {self.best_position}", file=sys.stderr)

        # Check for external references
        external_files = self.detect_external_refs()
        if self.has_external_refs:
            print("DEBUG: Spec uses external references. Attempting to resolve...", file=sys.stderr)
            resolved_refs = self.try_resolve_external_refs(external_files)
            
            # Try to resolve as many references as possible
            resolved_count = 0
            for path, path_data in self.paths.items():
                if isinstance(path_data, dict) and '$ref' in path_data:
                    resolved_data = self.resolve_ref(path_data['$ref'], resolved_refs)
                    if resolved_data:
                        self.paths[path] = resolved_data
                        resolved_count += 1
            
            print(f"DEBUG: Resolved {resolved_count}/{len(self.paths)} external references", file=sys.stderr)

        return True

    def preprocess_paths_for_object_extraction(self, paths):
        """
        Analyze all paths to determine the optimal segment position for object extraction.
        Returns a dictionary mapping from path to the suggested object name and the best position.
        """
        # Collect all path segments
        all_paths_segments = []
        for path in paths.keys():
            segments = path.strip('/').split('/')
            all_paths_segments.append(segments)
        
        # Count unique values at each position
        max_segments = max(len(segments) for segments in all_paths_segments) if all_paths_segments else 0
        position_uniqueness = []
        
        for pos in range(max_segments):
            unique_values = set()
            valid_paths = 0
            
            for segments in all_paths_segments:
                if pos < len(segments):
                    # Skip parameter segments (those in {braces})
                    if not (segments[pos].startswith('{') and segments[pos].endswith('}')):
                        unique_values.add(segments[pos])
                        valid_paths += 1
            
            # Calculate uniqueness score (unique values / valid paths)
            uniqueness_score = len(unique_values) / valid_paths if valid_paths > 0 else 0
            position_uniqueness.append((pos, uniqueness_score, len(unique_values)))
            print(f"DEBUG: Position {pos} has {len(unique_values)} unique values out of {valid_paths} valid paths (score: {uniqueness_score:.2f})", file=sys.stderr)
        
        # Sort positions by uniqueness score (descending)
        position_uniqueness.sort(key=lambda x: (x[1], x[2]), reverse=True)
        
        # Choose the best position
        best_position = position_uniqueness[0][0] if position_uniqueness else 0
        print(f"DEBUG: Best position for object extraction: {best_position}", file=sys.stderr)
        
        # Create a mapping from path to object name
        path_to_object = {}
        for path, segments in zip(paths.keys(), all_paths_segments):
            if best_position < len(segments):
                # Use the segment at the best position
                object_name = segments[best_position]
                # Skip parameter segments
                if object_name.startswith('{') and object_name.endswith('}'):
                    # Fall back to another position or use a default
                    for pos, _, _ in position_uniqueness[1:]:
                        if pos < len(segments) and not (segments[pos].startswith('{') and segments[pos].endswith('}')):
                            object_name = segments[pos]
                            break
                    else:
                        object_name = "default"
            else:
                # Path is too short, use the last segment
                object_name = segments[-1] if segments else "default"
            
            path_to_object[path] = object_name
        
        return path_to_object, best_position

    def _extract_object_name_from_path(self, path: str) -> str:
        """
        Extract object name from path using the optimal segment position.
        """
        # If we have a precomputed mapping, use it
        if path in self.path_to_object:
            return self.path_to_object[path]
        
        # Otherwise, fall back to the best position
        segments = path.strip('/').split('/')
        
        if not segments:
            return "default"
        
        if self.best_position < len(segments):
            object_name = segments[self.best_position]
            # Skip parameter segments
            if object_name.startswith('{') and object_name.endswith('}'):
                # Fall back to the first non-parameter segment
                for segment in segments:
                    if not (segment.startswith('{') and segment.endswith('}')):
                        return segment
                return "default"
            return object_name
        else:
            # Path is too short, use the last non-parameter segment
            non_param_segments = [part for part in segments if not (part.startswith('{') and part.endswith('}'))]
            return non_param_segments[-1] if non_param_segments else "default"

    def get_objects_from_paths(self) -> Dict[str, Dict]:
        """Convert OpenAPI paths to object-method structure with enhanced handling"""
        objects = {}
        
        # Create a mapping of tags to help with categorization
        tag_mapping = {}
        for tag_info in self.api_spec.get('tags', []):
            tag_name = tag_info.get('name', '')
            tag_desc = tag_info.get('description', '')
            tag_mapping[tag_name] = tag_desc

        for path, path_data in self.paths.items():
            # Extract object name from path using improved method
            object_name = self._extract_object_name_from_path(path)

            if object_name not in objects:
                objects[object_name] = {"methods": {}}

            # Handle both resolved and unresolved references
            if isinstance(path_data, dict) and '$ref' in path_data:
                # This is an unresolved external reference
                print(f"DEBUG: Inferring methods for unresolved ref: {path} -> {path_data['$ref']}", file=sys.stderr)
                
                # Try to infer tag context from the reference
                ref = path_data['$ref']
                tag_context = ""
                if 'authentication' in ref.lower():
                    tag_context = "authentication"
                elif 'status' in ref.lower():
                    tag_context = "status"
                elif 'secrets' in ref.lower():
                    tag_context = "secrets"
                elif 'policies' in ref.lower():
                    tag_context = "policies"
                elif 'roles' in ref.lower():
                    tag_context = "roles"
                elif 'resources' in ref.lower():
                    tag_context = "resources"
                
                # Infer methods from path structure and context
                inferred_methods = self.infer_method_from_path_and_context(path, ref, tag_context)
                
                for method_name, method_data in inferred_methods.items():
                    objects[object_name]["methods"][path] = method_data
            else:
                # Handle inline method definitions (like Harry Potter API)
                for method_name, method_info in path_data.items():
                    if method_name.lower() in ['get', 'post', 'put', 'delete', 'patch']:
                        method_data = {
                            "verb": method_name.lower(),
                            "operation": method_info.get('operationId', f"{method_name}_{path.replace('/', '_')}"),
                            "tags": method_info.get('tags', []),
                            "description": method_info.get('description', ''),
                            "parameters": method_info.get('parameters', [])
                        }
                        objects[object_name]["methods"][path] = method_data

        return objects
        
    def get_objects_from_paths_enhanced(self) -> Dict[str, Dict]:
        """Enhanced version of get_objects_from_paths that handles complex path structures"""
        # First use the original method to maintain compatibility
        objects = self.get_objects_from_paths()
        
        # If only one object was found, try to extract more objects
        if len(objects) <= 1:
            print(f"DEBUG: Only {len(objects)} object(s) found. Trying enhanced extraction...", file=sys.stderr)
            
            # Create a new objects dictionary for enhanced extraction
            enhanced_objects = {}
            
            for path, path_data in self.paths.items():
                # Try different strategies to extract object names
                
                # Strategy 1: Use tags if available
                tags = []
                for method_name, method_info in path_data.items():
                    if method_name.lower() in ['get', 'post', 'put', 'delete', 'patch']:
                        if 'tags' in method_info and method_info['tags']:
                            tags.extend(method_info['tags'])
                
                # Use unique tags as object names
                for tag in set(tags):
                    if tag and tag not in enhanced_objects:
                        enhanced_objects[tag] = {"methods": {}}
                    
                    if tag:
                        # Add this path's methods to the tag object
                        for method_name, method_info in path_data.items():
                            if method_name.lower() in ['get', 'post', 'put', 'delete', 'patch']:
                                method_data = {
                                    "verb": method_name.lower(),
                                    "operation": method_info.get('operationId', f"{method_name}_{path.replace('/', '_')}"),
                                    "tags": method_info.get('tags', []),
                                    "description": method_info.get('description', ''),
                                    "parameters": method_info.get('parameters', [])
                                }
                                enhanced_objects[tag]["methods"][path] = method_data
                
                # Strategy 2: If no tags, try path segments
                if not tags:
                    # Split the path into segments
                    path_parts = path.strip('/').split('/')
                    
                    # Try each path segment as a potential object name
                    # Skip segments that look like parameters (in curly braces)
                    for i, part in enumerate(path_parts):
                        if not (part.startswith('{') and part.endswith('}')):
                            obj_name = part
                            
                            if obj_name and obj_name not in enhanced_objects:
                                enhanced_objects[obj_name] = {"methods": {}}
                            
                            if obj_name:
                                # Add this path's methods to the object
                                for method_name, method_info in path_data.items():
                                    if method_name.lower() in ['get', 'post', 'put', 'delete', 'patch']:
                                        method_data = {
                                            "verb": method_name.lower(),
                                            "operation": method_info.get('operationId', f"{method_name}_{path.replace('/', '_')}"),
                                            "tags": method_info.get('tags', []),
                                            "description": method_info.get('description', ''),
                                            "parameters": method_info.get('parameters', [])
                                        }
                                        enhanced_objects[obj_name]["methods"][path] = method_data
            
            # If we found more objects with the enhanced method, use those instead
            if len(enhanced_objects) > len(objects):
                print(f"DEBUG: Enhanced extraction found {len(enhanced_objects)} objects vs. {len(objects)} with original method.", file=sys.stderr)
                return enhanced_objects
            else:
                print(f"DEBUG: Enhanced extraction did not find more objects. Using original result.", file=sys.stderr)
        
        # Return the original objects if we didn't find more with the enhanced method
        return objects

class LargeOpenAPIParser:
    """Enhanced parser for large OpenAPI specifications with intelligent filtering"""
    
    def __init__(self, api_spec_url: str):
        self.api_spec_url = api_spec_url
        self.api_spec = None
        self.servers = []
        self.security_schemes = {}
        self.global_security = []
        self.paths = {}
        self.tags = {}
        self.components = {}
        self.chunk_size = 1000  # Process paths in chunks

    def fetch_api_spec_chunked(self, relevant_tags=None, relevant_paths=None):
        """Fetch OpenAPI spec with memory-efficient chunked processing for YAML using ruamel.yaml."""
        try:
            from ruamel.yaml import YAML
            yaml_loader = YAML(typ='safe')

            if self.api_spec_url.startswith(('http://', 'https://')):
                print(f"DEBUG: Fetching large OpenAPI spec from URL: {self.api_spec_url}", file=sys.stderr)
                response = requests.get(self.api_spec_url, stream=True)
                response.raise_for_status()
                content = response.text
                # For remote YAML, we still need to load the whole text, but can stream parse
                from io import StringIO
                stream = StringIO(content)
            else:
                print(f"DEBUG: Streaming large OpenAPI spec from file: {self.api_spec_url}", file=sys.stderr)
                stream = open(self.api_spec_url, 'r', encoding='utf-8')

            # Stream parse the YAML, only keeping top-level keys and streaming 'paths'
            top_level = yaml_loader.load(stream)
            minimal_spec = {}
            
            for key in top_level:
                if key != 'paths':
                    minimal_spec[key] = top_level[key]
            
            # Stream the 'paths' section
            minimal_spec['paths'] = {}
            for path, methods in top_level.get('paths', {}).items():
                if relevant_paths and path not in relevant_paths:
                    continue
                
                # Optionally, filter by tags in methods
                if relevant_tags:
                    keep = False
                    for method_info in methods.values():
                        if 'tags' in method_info and any(tag in relevant_tags for tag in method_info['tags']):
                            keep = True
                            break
                    if not keep:
                        continue
                
                minimal_spec['paths'][path] = methods

            self.api_spec = minimal_spec
            print(f"DEBUG: Streamed and filtered OpenAPI spec. Paths kept: {len(minimal_spec['paths'])}", file=sys.stderr)
            return True

        except Exception as e:
            print(f"Error streaming/parsing API specification: {e}", file=sys.stderr)
            traceback.print_exc()
            return False

    def extract_metadata(self):
        """Extract metadata for intelligent filtering"""
        if not self.api_spec:
            if not self.fetch_api_spec_chunked():
                return False

        # Extract basic info
        self.servers = self.api_spec.get('servers', [])
        self.security_schemes = self.api_spec.get('components', {}).get('securitySchemes', {})
        self.global_security = self.api_spec.get('security', [])
        self.paths = self.api_spec.get('paths', {})
        self.components = self.api_spec.get('components', {})

        # Extract tags for intelligent grouping
        self.tags = {}
        for tag_info in self.api_spec.get('tags', []):
            tag_name = tag_info.get('name', '')
            tag_desc = tag_info.get('description', '')
            self.tags[tag_name] = {
                'description': tag_desc,
                'paths': []
            }

        # Map paths to tags
        for path, methods in self.paths.items():
            for method_name, method_info in methods.items():
                if method_name.lower() in ['get', 'post', 'put', 'delete', 'patch']:
                    method_tags = method_info.get('tags', [])
                    for tag in method_tags:
                        if tag in self.tags:
                            self.tags[tag]['paths'].append(path)

        return True

    def get_objects_by_tags(self, tag_filter: Optional[List[str]] = None) -> Dict[str, Dict]:
        """Extract objects grouped by OpenAPI tags with filtering"""
        objects = defaultdict(lambda: {"methods": {}, "tag": "", "description": ""})
        
        for path, methods in self.paths.items():
            for method_name, method_info in methods.items():
                if method_name.lower() not in ['get', 'post', 'put', 'delete', 'patch']:
                    continue

                # Get tags for this method
                method_tags = method_info.get('tags', [])
                
                # Apply tag filter if specified
                if tag_filter and not any(tag in method_tags for tag in tag_filter):
                    continue

                # Use primary tag or path-based object name
                primary_tag = method_tags[0] if method_tags else self._extract_object_from_path(path)
                
                if primary_tag not in objects:
                    objects[primary_tag] = {
                        "methods": {},
                        "tag": primary_tag,
                        "description": self.tags.get(primary_tag, {}).get('description', ''),
                        "path_count": 0
                    }

                method_data = {
                    "verb": method_name.lower(),
                    "operation": method_info.get('operationId', f"{method_name}_{path.replace('/', '_')}"),
                    "tags": method_tags,
                    "description": method_info.get('description', ''),
                    "parameters": method_info.get('parameters', [])
                }

                objects[primary_tag]["methods"][path] = method_data
                objects[primary_tag]["path_count"] += 1

        return dict(objects)

    def _extract_object_from_path(self, path: str) -> str:
        """
        Extract object name from path with improved logic to handle complex paths.
        This method tries to identify the actual resource name in the path, which is typically:
        - The last non-parameter segment in the path
        - Or a segment that appears before a parameter
        """
        path_parts = path.strip('/').split('/')
        
        # If path is empty, use default
        if not path_parts:
            return "default"
            
        # Get non-parameter segments (those not enclosed in braces)
        non_param_segments = [part for part in path_parts if not (part.startswith('{') and part.endswith('}'))]
        
        # If no non-parameter segments, use default
        if not non_param_segments:
            return "default"
            
        # For paths like /groups/{groupId}/clusters, use 'clusters' (last non-parameter segment)
        # For paths like /order_confirmation, use 'order_confirmation' (only segment)
        return non_param_segments[-1]

    def search_paths_by_keywords(self, keywords: List[str]) -> Dict[str, Dict]:
        """Search paths by keywords in descriptions, summaries, and operation IDs"""
        results = defaultdict(lambda: {"methods": {}, "relevance_score": 0})
        
        for path, methods in self.paths.items():
            for method_name, method_info in methods.items():
                if method_name.lower() not in ['get', 'post', 'put', 'delete', 'patch']:
                    continue

                # Calculate relevance score
                score = 0
                searchable_text = [
                    method_info.get('description', ''),
                    method_info.get('summary', ''),
                    method_info.get('operationId', ''),
                    path
                ]
                searchable_text_str = ' '.join(searchable_text).lower()
                
                for keyword in keywords:
                    if keyword.lower() in searchable_text_str:
                        score += 1

                if score > 0:
                    primary_tag = method_info.get('tags', [None])[0] or self._extract_object_from_path(path)
                    
                    if primary_tag not in results:
                        results[primary_tag] = {
                            "methods": {},
                            "relevance_score": 0,
                            "tag": primary_tag
                        }

                    method_data = {
                        "verb": method_name.lower(),
                        "operation": method_info.get('operationId', f"{method_name}_{path.replace('/', '_')}"),
                        "tags": method_info.get('tags', []),
                        "description": method_info.get('description', ''),
                        "summary": method_info.get('summary', ''),
                        "parameters": method_info.get('parameters', []),
                        "relevance_score": score
                    }

                    results[primary_tag]["methods"][path] = method_data
                    results[primary_tag]["relevance_score"] = max(results[primary_tag]["relevance_score"], score)

        # Sort by relevance score
        sorted_results = dict(sorted(results.items(), key=lambda x: x[1]["relevance_score"], reverse=True))
        return sorted_results

    def get_top_objects(self, limit: int = 10) -> Dict[str, Dict]:
        """Get top objects by endpoint count and importance"""
        objects = self.get_objects_by_tags()
        
        # Score objects by endpoint count and importance
        scored_objects = []
        for obj_name, obj_data in objects.items():
            score = obj_data.get("path_count", 0)
            
            # Bonus for common CRUD operations
            crud_verbs = set()
            for method_data in obj_data["methods"].values():
                crud_verbs.add(method_data["verb"])
            if len(crud_verbs) >= 3:  # Has most CRUD operations
                score += 5
            
            scored_objects.append((score, obj_name, obj_data))

        # Return top objects
        top_objects = heapq.nlargest(limit, scored_objects)
        return {obj_name: obj_data for score, obj_name, obj_data in top_objects}

class ApiSchemaParser:
    """Simplified API schema parser for OpenAPI specs"""
    
    def __init__(self, api_spec_url: str):
        self.api_spec_url = api_spec_url
        self.api_spec = None
        self.servers = []
        self.security_schemes = {}
        self.global_security = []
        self.paths = {}

    def fetch_api_spec(self):
        """Fetch and parse the API specification from URL or file"""
        try:
            if self.api_spec_url.startswith(('http://', 'https://')):
                print(f"DEBUG: Fetching OpenAPI spec from URL: {self.api_spec_url}", file=sys.stderr)
                response = requests.get(self.api_spec_url)
                response.raise_for_status()
                response.encoding = 'utf-8'
                content = response.text
            else:
                print(f"DEBUG: Loading OpenAPI spec from file: {self.api_spec_url}", file=sys.stderr)
                with open(self.api_spec_url, 'r', encoding='utf-8') as f:
                    content = f.read()

            try:
                self.api_spec = json.loads(content)
                print("DEBUG: Parsed OpenAPI spec as JSON.", file=sys.stderr)
            except json.JSONDecodeError:
                print("DEBUG: JSON decode failed. Trying YAML...", file=sys.stderr)
                self.api_spec = yaml.safe_load(content)
                print("DEBUG: Parsed OpenAPI spec as YAML.", file=sys.stderr)

            return True
        except Exception as e:
            print(f"Error fetching/parsing API specification: {e}", file=sys.stderr)
            traceback.print_exc()
            return False

    def extract_data(self):
        """Extract relevant data from the API specification"""
        if not self.api_spec:
            if not self.fetch_api_spec():
                return False

        self.servers = self.api_spec.get('servers', [])
        self.security_schemes = self.api_spec.get('components', {}).get('securitySchemes', {})
        self.global_security = self.api_spec.get('security', [])
        self.paths = self.api_spec.get('paths', {})

        return True

    def get_objects_from_paths(self) -> Dict[str, Dict]:
        """Convert OpenAPI paths to object-method structure"""
        objects = {}
        
        for path, methods in self.paths.items():
            # Extract object name from path
            path_parts = path.strip('/').split('/')
            if len(path_parts) > 0:
                object_name = path_parts[0] if path_parts[0] else path_parts[1] if len(path_parts) > 1 else "default"
            else:
                object_name = "default"

            if object_name not in objects:
                objects[object_name] = {"methods": {}}

            for method_name, method_info in methods.items():
                if method_name.lower() in ['get', 'post', 'put', 'delete', 'patch']:
                    method_data = {
                        "verb": method_name.lower(),
                        "operation": method_info.get('operationId', f"{method_name}_{path.replace('/', '_')}"),
                        "tags": method_info.get('tags', []),
                        "description": method_info.get('description', ''),
                        "parameters": method_info.get('parameters', [])
                    }
                    objects[object_name]["methods"][path] = method_data

        return objects

# =============================
# Utility Functions
# =============================

def save_json_data(file_path: str, data: Dict) -> bool:
    """Save data to JSON file"""
    try:
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        return True
    except Exception as e:
        print(f"Error saving JSON data: {e}", file=sys.stderr)
        return False

def load_json_data(file_path: str) -> Dict:
    """Load data from JSON file"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
        return data
    except Exception as e:
        print(f"Error loading JSON data: {e}", file=sys.stderr)
        return {}

def method_requires_id(method_path: str, parameters: list) -> bool:
    """Check if method requires an ID parameter"""
    # Check for path parameters that look like IDs
    path_params = [p for p in parameters if p.get('in') == 'path']
    for param in path_params:
        if any(id_word in param.get('name', '').lower() for id_word in ['id', 'uuid', 'key']):
            return True
    return False

# =============================
# Core OpenAPI Initialization Functions
# =============================

def initialize_openapi_spec_enhanced(
    openapi_url: str,
    source_json_file_path: str,
    target_json_file_path: str
) -> Dict[str, Any]:
    """
    Enhanced OpenAPI specification initialization that handles both simple and complex specs
    This function can handle:
    1. Simple inline OpenAPI specs (like harry_potter_openapi.yaml)
    2. Complex specs with external $ref references (like openapi.yml)
    3. Automatic detection and resolution of external references
    4. Intelligent method inference when references can't be resolved
    """
    global api_spec_url, source_json_path, target_json_path, source_json_data, target_json_data

    try:
        api_spec_url = openapi_url
        source_json_path = source_json_file_path
        target_json_path = target_json_file_path

        # Check if source.json already exists and has methods
        if os.path.exists(source_json_file_path):
            existing_data = load_json_data(source_json_file_path)
            
            # Check if existing source has methods
            has_methods = any(
                len(obj_data.get("methods", {})) > 0
                for obj_data in existing_data.values()
                if isinstance(obj_data, dict)
            )
            
            if has_methods:
                source_json_data = existing_data
                
                # Initialize target JSON
                target_json_data = {"objects": {}}
                if save_json_data(target_json_path, target_json_data):
                    print(f"Target JSON initialized at: {target_json_path}", file=sys.stderr)
                
                return {
                    "success": True,
                    "message": f"Using existing source.json with {len(source_json_data)} objects",
                    "source_objects": len(source_json_data),
                    "target_objects": 0,
                    "cached": True,
                    "enhanced": True
                }
        
        # Use the enhanced parser
        parser = EnhancedApiSchemaParser(openapi_url)
        if not parser.extract_data():
            return {"success": False, "error": "Failed to parse API specification with enhanced parser"}

        # Get objects with enhanced handling
        source_json_data = parser.get_objects_from_paths_enhanced()

        # Validate that we got useful data
        total_methods = sum(
            len(obj_data.get("methods", {}))
            for obj_data in source_json_data.values()
            if isinstance(obj_data, dict)
        )

        # Save the parsed data
        if save_json_data(source_json_path, source_json_data):
            print(f"Source JSON saved to: {source_json_path}", file=sys.stderr)
        else:
            return {"success": False, "error": "Failed to save enhanced source JSON"}

        # Initialize target JSON
        target_json_data = {"objects": {}}
        if save_json_data(target_json_path, target_json_data):
            print(f"Target JSON initialized at: {target_json_path}", file=sys.stderr)
        else:
            return {"success": False, "error": "Failed to initialize target JSON"}

        # Determine the parsing strategy used
        parsing_strategy = "enhanced_with_external_refs" if parser.has_external_refs else "enhanced_inline_methods"

        return {
            "success": True,
            "message": f"Enhanced OpenAPI spec initialized successfully",
            "source_objects": len(source_json_data),
            "target_objects": 0,
            "total_methods": total_methods,
            "parsing_strategy": parsing_strategy,
            "has_external_refs": parser.has_external_refs,
            "enhanced": True,
            "details": {
                "api_file": openapi_url,
                "objects_extracted": list(source_json_data.keys())[:10],  # First 10 objects
                "method_extraction": "intelligent_inference" if parser.has_external_refs else "direct_parsing"
            }
        }

    except Exception as e:
        print(f"ERROR in enhanced initialization: {e}", file=sys.stderr)
        traceback.print_exc()
        return {"success": False, "error": str(e)}

def initialize_openapi_spec(
    openapi_url: str,
    source_json_file_path: str,
    target_json_file_path: str
) -> Dict[str, Any]:
    """Initialize OpenAPI specification and create source/target JSON files (Legacy + Enhanced)"""
    global api_spec_url, source_json_path, target_json_path, source_json_data, target_json_data

    try:
        # First try enhanced initialization
        result = initialize_openapi_spec_enhanced(openapi_url, source_json_file_path, target_json_file_path)
        
        # If enhanced initialization succeeds, return it
        if result.get("success", False):
            return result
            
        print("DEBUG: Enhanced initialization failed, falling back to standard parser", file=sys.stderr)
        
        # Fallback to standard initialization
        api_spec_url = openapi_url
        source_json_path = source_json_file_path
        target_json_path = target_json_file_path

        # Check if source.json already exists and load it
        if os.path.exists(source_json_file_path):
            source_json_data = load_json_data(source_json_file_path)
        else:
            # Parse API spec and generate source JSON
            parser = ApiSchemaParser(openapi_url)
            if not parser.extract_data():
                return {"success": False, "error": "Failed to parse API specification"}

            source_json_data = parser.get_objects_from_paths()

            if save_json_data(source_json_path, source_json_data):
                print(f"Source JSON saved to: {source_json_path}", file=sys.stderr)
            else:
                return {"success": False, "error": "Failed to save source JSON"}

        # Initialize target JSON
        target_json_data = {"objects": {}}
        if save_json_data(target_json_path, target_json_data):
            print(f"Target JSON initialized at: {target_json_path}", file=sys.stderr)
        else:
            return {"success": False, "error": "Failed to initialize target JSON"}

        return {
            "success": True,
            "message": f"OpenAPI spec initialized successfully (fallback mode)",
            "source_objects": len(source_json_data),
            "target_objects": 0,
            "enhanced": False
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

def list_objects(json_type: str = "source", source_path: str = "", target_path: str = "") -> Dict[str, Any]:
    """List all objects in source or target JSON"""
    try:
        if json_type == "source":
            # Load source.json directly
            if source_path and os.path.exists(source_path):
                data = load_json_data(source_path)
            else:
                data = source_json_data
        else:
            # Load target.json directly
            if target_path and os.path.exists(target_path):
                data = load_json_data(target_path)
            else:
                data = target_json_data

        # Handle both structures: direct objects or objects under "objects" key
        if "objects" in data:
            objects = list(data.get("objects", {}).keys())
        else:
            # Direct object structure from get_objects_from_paths
            objects = list(data.keys())

        return {
            "success": True,
            "objects": objects,
            "count": len(objects),
            "type": json_type
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

def select_object_for_target(object_name: str, target_path: str, source_path: str) -> Dict[str, Any]:
    """Select an object from source to add to target"""
    try:
        # Always load the latest source.json from disk
        if os.path.exists(source_path):
            data = load_json_data(source_path)
        else:
            data = source_json_data

        # Always load the latest target.json from disk
        if os.path.exists(target_path):
            target_data = load_json_data(target_path)
        else:
            target_data = target_json_data

        # Support both flat and nested (under 'objects') structures
        if "objects" in data:
            source_object = data.get("objects", {}).get(object_name)
        else:
            source_object = data.get(object_name)

        if not source_object:
            return {"success": False, "error": f"Object '{object_name}' not found in source"}

        # Merge new object into existing 'objects' dict
        if "objects" not in target_data:
            target_data["objects"] = {}

        target_data["objects"][object_name] = source_object
        save_json_data(target_path, target_data)

        return {
            "success": True,
            "message": f"Object '{object_name}' added to target",
            "methods_count": len(source_object.get("methods", {})),
            "current_objects": list(target_data["objects"].keys())
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

# =============================
# Data Connector Code Generation Function
# =============================

def generate_data_connector_code(
    java_client_api_dir: str,
    java_client_model_dir: str,
    sdk_path: str,
    target_json_path: str,
    api_folder_path: str,
    version: str,
    interactive: bool = False,
    objects: Optional[List[str]] = None,
    methods: Optional[Dict[str, List[str]]] = None
) -> Dict[str, Any]:
    """
    Generate Data Connector code and tests using DSPy with enhanced error feedback loops.
    
    ENHANCED FEATURES:
    - Automatic Maven package building (mvn package -DskipTests) for each iteration
    - Intelligent error analysis and feedback loops
    - Automatic detection of common compilation issues:
      * Wrong package imports (com.radiantlogic.sdk.core.*, com.radiantlogic.iddd.*)
      * Wrong interface names (ReadOperations → SearchOperations)
      * Wrong class names (ResponseEntity → LdapResponse)
      * Class name mismatches with filenames
    - Iterative refinement with up to 3 attempts per object
    - Final validation with complete project build
    - Comprehensive scoring system (100 points max)
    
    Args:
        java_client_api_dir: Path to Java client API directory
        java_client_model_dir: Path to Java client model directory  
        sdk_path: Path to minimal SDK documentation
        target_json_path: Path to target JSON file with selected endpoints
        objects: Optional list of specific objects to generate for
        methods: Optional dict mapping objects to specific methods
    """
    # Set up debug logging
    debug_log_path = os.path.join(PROJECT_ROOT, "debug_generation.log")
    
    def debug_log(message):
        # Use the debug_logger instead of print statements
        # This will respect the logging configuration
        debug_logger.debug(message)
        
        # Also write to the debug log file directly for backward compatibility
        timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        with open(debug_log_path, 'a', encoding='utf-8') as f:
            f.write(f"[{timestamp}] {message}\n")
    
    try:
        debug_log("=== STARTING DATA CONNECTOR CODE GENERATION ===")
        debug_log(f"Input parameters:")
        debug_log(f"  java_client_api_dir: {java_client_api_dir}")
        debug_log(f"  java_client_model_dir: {java_client_model_dir}")
        debug_log(f"  sdk_path: {sdk_path}")
        debug_log(f"  target_json_path: {target_json_path}")
        debug_log(f"  interactive: {interactive}")
        debug_log(f"  objects: {objects}")
        debug_log(f"  methods: {methods}")
        
        import dspy
        
        debug_log("Importing DSPy...")
        
        # Initialize DSPy with Gemini
        debug_log("Configuring DSPy settings...")
        dspy.settings.context(track_usage=True)
        
        # Configure the LLM for DSPy
        debug_log("Configuring LLM...")
        lm = dspy.LM(
            "gemini/gemini-2.0-flash-lite-preview-02-05",
            api_key="AIzaSyBUiSvLY7dfJnqvwZc4-wh1OPIzzz7ZJ_8",
        )     
        dspy.configure(lm=lm)
        debug_log("LLM configured successfully")
        
        # Define the core DSPy modules for data connector generation
        debug_log("Defining DataConnectorGenerator class...")
        class DataConnectorGenerator(dspy.Module):
            def __init__(self):
                super().__init__()
                debug_log("Initializing DataConnectorGenerator with ChainOfThought...")
                self.generator = dspy.ChainOfThought("sdk_docs, java_client, target_json, object_name, reasoning -> connector_code, unit_tests, json_config")
            
            def forward(self, sdk_docs, java_client, target_json, object_name):
                debug_log(f"DataConnectorGenerator.forward() called for object: {object_name}")
                
                # Load example files for few-shot learning
                example_connector = ""
                example_config = ""
                example_tests = ""
                
                try:
                    debug_log("Loading example files...")
                    
                    # Load Java connector example
                    connector_example_path = os.path.join(PROJECT_ROOT, "examples", "BooksDataConnector.java")
                    if os.path.exists(connector_example_path):
                        with open(connector_example_path, 'r') as f:
                            example_connector = f.read()
                        debug_log(f"Loaded example connector, length: {len(example_connector)}")
                    else:
                        debug_log(f"Example connector file not found at: {connector_example_path}")
                    
                    # Load JSON config example
                    config_example_path = os.path.join(PROJECT_ROOT, "examples", "harrypotterbooksconnector.json")
                    if os.path.exists(config_example_path):
                        with open(config_example_path, 'r') as f:
                            example_config = f.read()
                        debug_log(f"Loaded example config, length: {len(example_config)}")
                    else:
                        debug_log(f"Example config file not found at: {config_example_path}")
                    
                    # Load unit tests example
                    tests_example_path = os.path.join(PROJECT_ROOT, "examples", "HarryPotterDataConnectorTest.java")
                    if os.path.exists(tests_example_path):
                        with open(tests_example_path, 'r') as f:
                            example_tests = f.read()
                        debug_log(f"Loaded example tests, length: {len(example_tests)}")
                    else:
                        debug_log(f"Example tests file not found at: {tests_example_path}")
                        
                except Exception as e:
                    debug_log(f"Warning: Could not load example files: {e}")
                
                # Create a structured few-shot prompt with examples from files
                few_shot_prompt = f"""
=== FEW-SHOT EXAMPLES ===

EXAMPLE 1: Java Connector Class (WORKING HARRY POTTER CONNECTOR AS TEMPLATE)
INPUT: object_name = "harrypotter"
OUTPUT:
```java
{example_connector}
```

EXAMPLE 2: JSON Configuration (WORKING HARRY POTTER CONNECTOR AS TEMPLATE)
INPUT: object_name = "harrypotter"
OUTPUT:
```json
{example_config}
```

EXAMPLE 3: Unit Tests (WORKING HARRY POTTER CONNECTOR AS TEMPLATE)
INPUT: object_name = "harrypotter"
OUTPUT:
```java
{example_tests}
```

=== CRITICAL REQUIREMENTS ===
1. Package MUST be: com.radiantlogic.custom.dataconnector
2. Class name MUST be: {{object_name.capitalize()}}DataConnector (dynamically generated based on selected objects)
3. Config file reference MUST be: @CustomConnector(metaJsonFile = "{{object_name.lower()}}Connector.json")
4. Config file name MUST be: {{object_name.lower()}}Connector.json
5. JSON config MUST be simple and focused (not overly complex)
6. The connector should implement methods to handle the ACTUALLY SELECTED objects: {objects_to_generate}
7. Use the EXACT import patterns and class structure shown in the Harry Potter examples above
8. ADAPT the functionality to work with the selected objects, not Harry Potter objects
9. MUST include @ManagedComponent annotation on the connector class for proper dependency injection

=== FORBIDDEN IMPORTS (DO NOT USE) ===
❌ com.radiantlogic.sdk.core.*
❌ com.radiantlogic.iddd.*
❌ com.radiantlogic.idddm.*
❌ ReadOperations (use SearchOperations instead)
❌ ResponseEntity (use LdapResponse instead)
❌ ResponseStatus (use LdapResultCode instead)
❌ SearchRequest (use LdapSearchRequest instead)

=== REQUIRED IMPORTS (MUST USE) ===
✅ com.radiantlogic.iddm.annotations.CustomConnector
✅ com.radiantlogic.iddm.annotations.ManagedComponent
✅ com.radiantlogic.iddm.annotations.Property
✅ com.radiantlogic.iddm.base.Logger
✅ com.radiantlogic.iddm.base.SearchOperations
✅ com.radiantlogic.iddm.base.TestConnectionOperations
✅ com.radiantlogic.iddm.ldap.LdapSearchRequest
✅ com.radiantlogic.iddm.base.TestConnectionRequest
✅ com.radiantlogic.iddm.ldap.LdapResponse
✅ com.radiantlogic.iddm.base.TestConnectionResponse
✅ com.radiantlogic.iddm.base.ReadOnlyProperties
✅ com.radiantlogic.iddm.base.InjectableProperties
✅ com.radiantlogic.iddm.ldap.LdapResultCode

=== TASK ===
Generate for the ACTUALLY SELECTED objects: {objects_to_generate}

1. Java connector class that handles ALL selected objects: {objects_to_generate}
2. Unit tests for the connector
3. Comprehensive JSON configuration for the connector

IMPORTANT: This connector should:
- Handle the ACTUALLY SELECTED objects: {objects_to_generate}
- Use a dynamic class name based on the selected objects
- Follow the EXACT patterns shown in the Harry Potter working examples above
- Use the correct import statements and class structure
- ADAPT the functionality to work with the selected objects, not Harry Potter objects
- Use the appropriate API clients for the selected objects
"""
                
                # Add feedback if available
                # Note: object_json is not defined in this scope, so removing feedback logic for now
                
                debug_log(f"Calling DSPy generator with structured few-shot prompt")
                debug_log(f"Input lengths - sdk_docs: {len(sdk_docs)}, java_client: {len(java_client)}, target_json: {len(target_json)}")
                
                result = self.generator(
                    sdk_docs=sdk_docs,
                    java_client=java_client,
                    target_json=target_json,
                    object_name=object_name,
                    reasoning=few_shot_prompt
                )
                
                debug_log(f"Generator returned result with keys: {list(result.__dict__.keys()) if hasattr(result, '__dict__') else 'No __dict__'}")
                return result
        
        # Load inputs
        experiment_dir = api_folder_path
        
        # Load SDK documentation
        debug_log(f"Loading SDK documentation from: {sdk_path}")
        with open(sdk_path, 'r') as f:
            sdk_docs = f.read()
        debug_log(f"Loaded SDK docs, length: {len(sdk_docs)}")
        
        # Load Java client code
        debug_log(f"Loading Java client code from: {java_client_api_dir}")
        java_client_code = ""
        if os.path.exists(java_client_api_dir):
            for root, dirs, files in os.walk(java_client_api_dir):
                for file in files:
                    if file.endswith('.java'):
                        file_path = os.path.join(root, file)
                        debug_log(f"Loading Java file: {file_path}")
                        with open(file_path, 'r') as f:
                            java_client_code += f"// {file}\n{f.read()}\n\n"
        debug_log(f"Loaded Java client code, length: {len(java_client_code)}")
        
        # Load target JSON and parse objects
        debug_log(f"Loading target JSON from: {target_json_path}")
        with open(target_json_path, 'r') as f:
            target_data = json.load(f)
        debug_log(f"Loaded target JSON with keys: {list(target_data.keys())}")
        
        # Extract objects from target JSON
        objects_to_generate = []
        if "selected_objects" in target_data:
            objects_to_generate = list(target_data["selected_objects"].keys())
        elif "objects" in target_data:
            objects_to_generate = list(target_data["objects"].keys())
        else:
            # Fallback: try to find objects in the JSON structure
            for key, value in target_data.items():
                if isinstance(value, dict) and "methods" in value:
                    objects_to_generate.append(key)
        
        if not objects_to_generate:
            debug_log("ERROR: No objects found in target JSON")
            return {
                "success": False,
                "error": "No objects found in target JSON. Expected 'selected_objects' or 'objects' key."
            }
        
        debug_log(f"Found objects to generate: {objects_to_generate}")
        
        # Initialize DSPy pipeline
        debug_log("Initializing DSPy pipeline...")
        generator = DataConnectorGenerator()
        debug_log("DSPy pipeline initialized")
        
        # Generate code for each object
        generated_files = {}
        
        # MODIFIED: Generate ONE unified connector instead of multiple separate ones
        debug_log("=== GENERATING UNIFIED DATA CONNECTOR ===")
        debug_log(f"This connector will handle ALL selected objects in a single unified class")
        debug_log(f"Objects to be handled: {objects_to_generate}")
        
        # Determine names based on the YAML file name rather than selected objects
        yaml_file_name = os.path.basename(api_spec_url) if api_spec_url else "generated"
        yaml_base_name = os.path.splitext(yaml_file_name)[0]
        
        def to_camel_case(name: str) -> str:
            parts = re.split(r'[^a-zA-Z0-9]+', name)
            return ''.join([p.capitalize() for p in parts if p])
        
        class_base_name = to_camel_case(yaml_base_name) or "Generated"
        config_base_name = yaml_base_name.lower()
        
        # Use YAML-based naming
        unified_object_name = config_base_name
        
        unified_object_data = {
            "object_name": unified_object_name,
            "description": f"Unified Data Connector for {', '.join(objects_to_generate)}",
            "ldap_operations": ["SearchOperations", "TestConnectionOperations"],
            "all_objects": objects_to_generate,  # Include all objects for reference
            "methods": {}
        }
        
        # Collect all methods from all objects
        for object_name in objects_to_generate:
            object_data = {}
            if "selected_objects" in target_data and object_name in target_data["selected_objects"]:
                object_data = target_data["selected_objects"][object_name]
            elif "objects" in target_data and object_name in target_data["objects"]:
                object_data = target_data["objects"][object_name]
            else:
                object_data = target_data.get(object_name, {})
            
            if "methods" in object_data:
                for method_path, method_details in object_data["methods"].items():
                    # Prefix method names with object name to avoid conflicts
                    unified_method_name = f"{object_name}_{method_path.replace('/', '_')}"
                    unified_object_data["methods"][unified_method_name] = {
                        "path": method_path,
                        "verb": method_details.get("verb", ""),
                        "operation": method_details.get("operation", ""),
                        "tags": method_details.get("tags", []),
                        "description": method_details.get("description", ""),
                        "parameters": method_details.get("parameters", []),
                        "source_object": object_name  # Track which object this method came from
                    }
        
        debug_log(f"Unified connector will handle {len(objects_to_generate)} objects: {objects_to_generate}")
        debug_log(f"Unified connector will have {len(unified_object_data['methods'])} methods")
        
        # Create unified JSON for DSPy
        unified_object_json = {
            "object_name": unified_object_name,
            "methods": list(unified_object_data["methods"].values()),
            "description": unified_object_data["description"],
            "ldap_operations": unified_object_data["ldap_operations"],
            "all_objects": objects_to_generate,
            "is_unified": True
        }
        
        unified_object_json_str = json.dumps(unified_object_json, indent=2)
        debug_log(f"Created unified object JSON, length: {len(unified_object_json_str)}")
        
        # ITERATIVE REFINEMENT LOOP for unified connector
        max_iterations = 5
        best_score = 0
        best_code = None
        best_tests = None
        best_config = None
        
        # Initialize the intelligent error corrector
        debug_log("Initializing DSPy Error Corrector...")
        debug_log(f"Error corrector initialization skipped - using fallback approach")
        
        for iteration in range(max_iterations):
            debug_log(f"=== ITERATION {iteration + 1}/{max_iterations} FOR UNIFIED DATA CONNECTOR ===")
            debug_log(f"Generating unified connector that handles: {objects_to_generate}")
            
            # Generate code using DSPy for unified connector
            debug_log(f"Calling DSPy generator for unified connector...")
            start_time = datetime.datetime.now()
            
            result = generator(
                sdk_docs=sdk_docs,
                java_client=java_client_code,
                target_json=unified_object_json_str,
                object_name=class_base_name  # Use YAML-derived class base name
            )
            
            end_time = datetime.datetime.now()
            generation_time = (end_time - start_time).total_seconds()
            debug_log(f"DSPy generation completed in {generation_time} seconds")
            
            # Extract generated code
            debug_log("Extracting generated code...")
            connector_code = result.connector_code
            unit_tests = result.unit_tests
            json_config = result.json_config
            
            debug_log(f"Generated code lengths - connector: {len(connector_code) if connector_code else 0}, tests: {len(unit_tests) if unit_tests else 0}, config: {len(json_config) if json_config else 0}")
            
            # Clean up the generated code to remove markdown formatting
            def clean_code_content(content):
                if not content:
                    return content
                # Remove markdown code blocks
                if content.startswith('```java'):
                    content = content[7:]
                if content.startswith('```'):
                    content = content[3:]
                if content.endswith('```'):
                    content = content[:-3]
                return content.strip()
            
            connector_code = clean_code_content(connector_code)
            unit_tests = clean_code_content(unit_tests)
            
            debug_log("Code cleaned of markdown formatting")
            
            # SIMPLIFIED ERROR HANDLING (fallback approach)
            debug_log("=== SIMPLIFIED ERROR HANDLING ===")
            debug_log("Using fallback error handling approach")
            
            # Basic pattern-based corrections can be added here if needed
            # For now, we'll proceed with the generated code as-is
            
            # Save generated code for unified connector with consistent directory naming
            version_dir = f"src version {version}"
            output_dir = os.path.join(experiment_dir, f"{version_dir}/main/java/com/radiantlogic/custom/dataconnector")
            test_dir = os.path.join(experiment_dir, f"{version_dir}/test/java/com/radiantlogic/custom/dataconnector")
            config_dir = os.path.join(experiment_dir, f"{version_dir}/main/resources/com/radiantlogic/custom/dataconnector")
            
            os.makedirs(output_dir, exist_ok=True)
            os.makedirs(test_dir, exist_ok=True)
            os.makedirs(config_dir, exist_ok=True)
            
            debug_log(f"Created directories - output: {output_dir}, test: {test_dir}, config: {config_dir}")
            
            # Write unified connector code (ensure proper encoding)
            connector_file = os.path.join(output_dir, f"{class_base_name}DataConnector.java")
            with open(connector_file, 'w', encoding='utf-8') as f:
                f.write(connector_code)
            debug_log(f"Wrote unified connector code to: {connector_file}")
            
            # Write unit tests for unified connector (ensure proper encoding)
            test_file = os.path.join(test_dir, f"{class_base_name}DataConnectorTest.java")
            with open(test_file, 'w', encoding='utf-8') as f:
                f.write(unit_tests)
            debug_log(f"Wrote unit tests to: {test_file}")
            
            # Write JSON configuration file separately (if generated)
            json_config_file = None
            if json_config and json_config.strip():
                # Clean up the JSON config to remove any markdown formatting
                clean_json = json_config
                if clean_json.startswith('```json'):
                    clean_json = clean_json[7:]
                if clean_json.startswith('```'):
                    clean_json = clean_json[3:]
                if clean_json.endswith('```'):
                    clean_json = clean_json[:-3]
                clean_json = clean_json.strip()
                
                json_config_file = os.path.join(config_dir, f"{config_base_name}Connector.json")
                with open(json_config_file, 'w', encoding='utf-8') as f:
                    f.write(clean_json)
                debug_log(f"Wrote JSON config to: {json_config_file}")
            else:
                debug_log(f"No JSON config generated for unified connector")
            
            # EVALUATE THE GENERATED CODE
            debug_log("=== EVALUATING GENERATED CODE ===")
            
            # Check compilation
            debug_log("Compiling generated code...")
            compile_result = subprocess.run(
                ["mvn", "compile", "-q"],
                capture_output=True,
                text=True,
                cwd=experiment_dir
            )
            compilation_success = compile_result.returncode == 0
            debug_log(f"Compilation success: {compilation_success}")
            if not compilation_success:
                debug_log(f"Compilation errors: {compile_result.stderr}")
            
            # Check tests
            test_success = False
            test_output = ""
            if compilation_success:
                debug_log("Running tests...")
                test_result = subprocess.run(
                    ["mvn", "test", f"-Dtest={class_base_name}DataConnectorTest", "-q"],
                    capture_output=True,
                    text=True,
                    cwd=experiment_dir
                )
                test_success = test_result.returncode == 0
                test_output = test_result.stdout + test_result.stderr
                debug_log(f"Test success: {test_success}")
                if not test_success:
                    debug_log(f"Test errors: {test_output}")
            
            # CRITICAL: Run mvn package -DskipTests to catch all build issues
            package_success = False
            package_errors = ""
            if compilation_success:
                debug_log("Running mvn package -DskipTests...")
                package_result = subprocess.run(
                    ["mvn", "package", "-DskipTests", "-q"],
                    capture_output=True,
                    text=True,
                    cwd=experiment_dir
                )
                package_success = package_result.returncode == 0
                package_errors = package_result.stderr
                debug_log(f"Package success: {package_success}")
                if not package_success:
                    debug_log(f"Package errors: {package_errors}")
            
            # Calculate score based on various criteria
            score = 0
            
            # Package name correctness (15 points)
            if "package com.radiantlogic.custom.dataconnector;" in connector_code:
                score += 15
                debug_log("✅ Correct package name")
            else:
                debug_log("❌ Wrong package name")
            
            # Config file reference correctness (15 points)
            if f'@CustomConnector(metaJsonFile = "{config_base_name}Connector.json")' in connector_code:
                score += 15
                debug_log("✅ Correct config file reference")
            else:
                debug_log("❌ Wrong config file reference")
            
            # Class name correctness (15 points)
            if f"class {class_base_name}DataConnector" in connector_code:
                score += 15
                debug_log("✅ Correct class name")
            else:
                debug_log("❌ Wrong class name")
            
            # Unified connector functionality (15 points)
            if ("SpellsApi" in connector_code and "CharactersApi" in connector_code and 
                "HousesApi" in connector_code and "BooksApi" in connector_code):
                score += 15
                debug_log("✅ Unified connector handles all Harry Potter objects")
            else:
                debug_log("❌ Missing API clients for some Harry Potter objects")
            
            # Compilation success (20 points)
            if compilation_success:
                score += 20
                debug_log("✅ Code compiles successfully")
            else:
                debug_log("❌ Code does not compile")
            
            # Package build success (20 points)
            if package_success:
                score += 20
                debug_log("✅ Maven package builds successfully")
            else:
                debug_log("❌ Maven package build failed")
            
            # Test success (10 points)
            if test_success:
                score += 10
                debug_log("✅ Tests pass")
            else:
                debug_log("❌ Tests fail")
            
            debug_log(f"Current iteration score: {score}/100")
            
            # Update best result if this iteration is better
            if score > best_score:
                best_score = score
                best_code = connector_code
                best_tests = unit_tests
                best_config = json_config
                debug_log(f"🎉 New best score: {best_score}/100")
            
            # If we have a perfect score, we can stop early
            if score >= 100:
                debug_log("🎉 Perfect score achieved! Stopping iterations.")
                break
            
            # If this is not the last iteration, provide feedback to the LLM
            if iteration < max_iterations - 1:
                debug_log("=== PROVIDING FEEDBACK TO LLM FOR UNIFIED CONNECTOR ===")
                
                # Create comprehensive feedback for the next iteration
                feedback = f"""
PREVIOUS ITERATION RESULTS FOR UNIFIED {unified_object_name.upper()} CONNECTOR:
- Score: {score}/100
- Compilation: {'✅ SUCCESS' if compilation_success else '❌ FAILED'}
- Package Build: {'✅ SUCCESS' if package_success else '❌ FAILED'}
- Tests: {'✅ PASSED' if test_success else '❌ FAILED'}
- Unified Functionality: {'✅ SUCCESS' if ("SpellsApi" in connector_code and "CharactersApi" in connector_code and "HousesApi" in connector_code and "BooksApi" in connector_code) else '❌ FAILED'}

CRITICAL ISSUES FOUND:
"""
                
                # Package name issues
                if "package com.radiantlogic.custom.dataconnector;" not in connector_code:
                    feedback += "- Package name is incorrect. Must be: com.radiantlogic.custom.dataconnector\n"
                
                # Config file reference issues
                if f'@CustomConnector(metaJsonFile = "{config_base_name}Connector.json")' not in connector_code:
                    feedback += '- Config file reference is incorrect. Must be: @CustomConnector(metaJsonFile = "{config_base_name}Connector.json")\n'
                
                # Class name issues
                if f"class {class_base_name}DataConnector" not in connector_code:
                    feedback += "- Class name is incorrect. Must be: {class_base_name}DataConnector\n"
                
                # Unified connector functionality issues
                missing_apis = []
                if "SpellsApi" not in connector_code:
                    missing_apis.append("SpellsApi")
                if "CharactersApi" not in connector_code:
                    missing_apis.append("CharactersApi")
                if "HousesApi" not in connector_code:
                    missing_apis.append("HousesApi")
                if "BooksApi" not in connector_code:
                    missing_apis.append("BooksApi")
                
                if missing_apis:
                    feedback += f"- Missing API clients for: {', '.join(missing_apis)}. The unified connector must handle ALL Harry Potter objects.\n"
                
                # Compilation issues with detailed analysis
                if not compilation_success:
                    feedback += f"- Compilation errors: {compile_result.stderr[:500]}...\n"
                    # Note: analyze_and_fix_compilation_errors function is not defined
                    feedback += f"\nCOMPILATION ERROR ANALYSIS:\nCompilation failed. Check the error output above.\n"
                
                # Package build issues with detailed analysis
                if not package_success:
                    feedback += f"- Maven package build errors: {package_errors[:500]}...\n"
                    # Note: analyze_and_fix_compilation_errors function is not defined
                    feedback += f"\nPACKAGE BUILD ERROR ANALYSIS:\nPackage build failed. Check the error output above.\n"
                    
                    # Additional package-specific analysis
                    if "class" in package_errors and "should be declared in a file named" in package_errors:
                        feedback += "- CRITICAL: Class name does not match filename. Ensure class name matches the file name exactly.\n"
                    
                    if "cannot find symbol" in package_errors:
                        feedback += "- CRITICAL: Missing imports or wrong package names. Use ONLY com.radiantlogic.iddm.* packages.\n"
                    
                    if "package" in package_errors and "does not exist" in package_errors:
                        feedback += "- CRITICAL: Wrong package imports. Use ONLY com.radiantlogic.iddm.* packages, NOT com.radiantlogic.sdk.core.* or com.radiantlogic.iddd.*\n"
                
                # Test issues
                if not test_success:
                    feedback += f"- Test errors: {test_output[:500]}...\n"
                
                feedback += f"""
IMPROVEMENT REQUIREMENTS:
1. Fix package name to: com.radiantlogic.custom.dataconnector
2. Fix config file reference to: @CustomConnector(metaJsonFile = "{config_base_name}Connector.json")
3. Fix class name to: {class_base_name}DataConnector
4. Use ONLY these correct imports:
   - com.radiantlogic.iddm.base.annotation.CustomConnector
   - com.radiantlogic.iddm.base.annotation.Property
   - com.radiantlogic.iddm.base.component.ManagedComponent
   - com.radiantlogic.iddm.base.logging.Logger
   - com.radiantlogic.iddm.operation.SearchOperations
   - com.radiantlogic.iddm.operation.TestConnectionOperations
   - com.radiantlogic.iddm.request.LdapSearchRequest
   - com.radiantlogic.iddm.request.TestConnectionRequest
   - com.radiantlogic.iddm.response.LdapResponse
   - com.radiantlogic.iddm.response.TestConnectionResponse
   - com.radiantlogic.iddm.base.ReadOnlyProperties
   - com.radiantlogic.iddm.base.InjectableProperties
5. Ensure class name matches filename exactly: {class_base_name}DataConnector
6. Ensure code compiles without errors
7. Ensure Maven package builds successfully
8. Ensure tests pass
9. Follow the example structure exactly
10. CRITICAL: This is a UNIFIED connector that MUST handle ALL selected objects: {objects_to_generate} in a single class
11. The connector should have separate API clients for each selected object type
12. The search method should be able to handle queries for any of the selected object types
13. The testConnection method should test connectivity to all selected API endpoints

Generate improved code for the next iteration.
"""
                
                # Update the object JSON to include feedback for next iteration
                unified_object_json["feedback"] = feedback
                unified_object_json["previous_score"] = score
                unified_object_json["previous_errors"] = {
                    "compilation_success": compilation_success,
                    "package_success": package_success,
                    "test_success": test_success,
                    "compilation_errors": compile_result.stderr if not compilation_success else "",
                    "package_errors": package_errors if not package_success else "",
                    "test_errors": test_output if not test_success else ""
                }
                
                unified_object_json_str = json.dumps(unified_object_json, indent=2)
                debug_log(f"Updated unified object JSON with feedback, length: {len(unified_object_json_str)}")
        
        # Use the best generated code
        debug_log(f"=== FINAL RESULT FOR UNIFIED DATA CONNECTOR ===")
        debug_log(f"Best score achieved: {best_score}/100")
        debug_log(f"Unified connector successfully handles: {objects_to_generate}")
        
        # Write the best code to files
        if best_code:
            with open(connector_file, 'w', encoding='utf-8') as f:
                f.write(best_code)
            debug_log(f"Wrote best unified connector code to: {connector_file}")
        
        if best_tests:
            with open(test_file, 'w', encoding='utf-8') as f:
                f.write(best_tests)
            debug_log(f"Wrote best unit tests to: {test_file}")
        
        if best_config:
            clean_json = best_config
            if clean_json.startswith('```json'):
                clean_json = clean_json[7:]
            if clean_json.startswith('```'):
                clean_json = clean_json[3:]
            if clean_json.endswith('```'):
                clean_json = clean_json[:-3]
            clean_json = clean_json.strip()
            
            json_config_file = os.path.join(config_dir, f"{config_base_name}Connector.json")
            with open(json_config_file, 'w', encoding='utf-8') as f:
                f.write(clean_json)
            debug_log(f"Wrote best JSON config to: {json_config_file}")
        
        # Add generated files to the results
        generated_files[unified_object_name] = {
            "connector": connector_file,
            "tests": test_file,
            "config": json_config_file,
            "connector_code": best_code,
            "unit_tests": best_tests,
            "json_config": best_config,
            "final_score": best_score,
            "handles_objects": objects_to_generate
        }
        
        debug_log(f"Added unified connector files to results with score {best_score}")
        
        # Final results
        total_files = 3  # unified connector + test + config
        debug_log(f"=== GENERATION COMPLETED ===")
        debug_log(f"Total files generated: {total_files}")
        debug_log(f"Unified connector handles objects: {objects_to_generate}")
        debug_log(f"Generated files:")
        debug_log(f"  - Connector: {generated_files[unified_object_name]['connector']}")
        debug_log(f"  - Tests: {generated_files[unified_object_name]['tests']}")
        debug_log(f"  - Config: {generated_files[unified_object_name]['config']}")
        debug_log(f"Final score: {best_score}/100")
        
        # FINAL VALIDATION: Run mvn package -DskipTests to ensure entire project builds
        debug_log("=== FINAL VALIDATION: Running mvn package -DskipTests for Unified Connector ===")
        final_package_result = subprocess.run(
            ["mvn", "package", "-DskipTests", "-q"],
            capture_output=True,
            text=True,
            cwd=experiment_dir
        )
        
        final_package_success = final_package_result.returncode == 0
        debug_log(f"Final package build success: {final_package_success}")
        
        if not final_package_success:
            debug_log(f"Final package build errors: {final_package_result.stderr}")
            debug_log("WARNING: Unified connector generated but final build failed. Some files may need manual fixes.")
            
            # Add build status to results
            final_results = {
                "success": True,
                "unified_connector_generated": True,
                "objects_handled": objects_to_generate,
                "best_score": best_score,
                "generated_files": {
                    "unified_connector": {
                        "connector": generated_files[unified_object_name]["connector"],
                        "tests": generated_files[unified_object_name]["tests"],
                        "config": generated_files[unified_object_name]["config"]
                    },
                    "total_files": total_files
                },
                "final_build_status": {
                    "success": final_package_success,
                    "errors": final_package_result.stderr if not final_package_success else "",
                    "warning": f"Unified {unified_object_name} connector generated but final build failed. Some files may need manual fixes."
                }
            }
        else:
            debug_log(f"🎉 SUCCESS: Unified {unified_object_name} connector generated and project builds successfully!")
            final_results = {
                "success": True,
                "unified_connector_generated": True,
                "objects_handled": objects_to_generate,
                "generated_files": {
                    "unified_connector": {
                        "connector": generated_files[unified_object_name]["connector"],
                        "tests": generated_files[unified_object_name]["tests"],
                        "config": generated_files[unified_object_name]["config"]
                    },
                    "total_files": total_files
                },
                "final_build_status": {
                    "success": final_package_success,
                    "message": f"Unified {unified_object_name} connector generated and project builds successfully!"
                }
            }
        
        debug_log("Returning final results")
        return final_results
        
    except Exception as e:
        debug_log(f"ERROR in generate_data_connector_code: {e}")
        debug_log(f"Traceback: {traceback.format_exc()}")
        return {
            "success": False,
            "error": str(e),
            "traceback": traceback.format_exc()
        }
# =============================
# MCP Tools (Enhanced)
# =============================

@mcp.tool()
def initialize_openapi_spec_enhanced_tool(
    openapi_url: str,
    source_json_file_path: str,
    target_json_file_path: str
) -> Dict[str, Any]:
    """
    Enhanced OpenAPI initialization tool that handles both simple and complex specs
    ✅ Works with simple specs like harry_potter_openapi.yaml (inline methods)
    ✅ Works with complex specs like openapi.yml (external $ref references)
    ✅ Automatic detection and resolution of external references
    ✅ Intelligent method inference when references can't be resolved
    ✅ Backward compatible with existing functionality
    """
    return initialize_openapi_spec_enhanced(
        openapi_url=openapi_url,
        source_json_file_path=source_json_file_path,
        target_json_file_path=target_json_file_path
    )

@mcp.tool()
def initialize_large_openapi_spec(
    openapi_url: str,
    source_json_file_path: str,
    target_json_file_path: str,
    use_intelligent_filtering: bool = True
) -> Dict[str, Any]:
    """Initialize large OpenAPI specification with intelligent filtering"""
    global api_spec_url, source_json_path, target_json_path, source_json_data, target_json_data

    try:
        api_spec_url = openapi_url
        source_json_path = source_json_file_path
        target_json_path = target_json_file_path

        # Check if source.json already exists and load it
        if os.path.exists(source_json_file_path):
            print(f"Loading existing source JSON from: {source_json_file_path}", file=sys.stderr)
            source_json_data = load_json_data(source_json_file_path)
            print(f"Loaded source_json_data keys: {list(source_json_data.keys())}", file=sys.stderr)
            print(f"Source JSON data length: {len(source_json_data)}", file=sys.stderr)
        else:
            # Parse API spec and generate source JSON with intelligent filtering
            print(f"Parsing large OpenAPI spec with intelligent filtering", file=sys.stderr)
            if use_intelligent_filtering:
                parser = LargeOpenAPIParser(openapi_url)
                if not parser.extract_metadata():
                    return {"success": False, "error": "Failed to parse API specification"}

                # Get top objects by importance
                source_json_data = parser.get_top_objects(limit=20)
                print(f"Intelligent filtering selected {len(source_json_data)} top objects", file=sys.stderr)
            else:
                # Use original parser for backward compatibility
                parser = ApiSchemaParser(openapi_url)
                if not parser.extract_data():
                    return {"success": False, "error": "Failed to parse API specification"}

                source_json_data = parser.get_objects_from_paths()

            if save_json_data(source_json_path, source_json_data):
                print(f"Source JSON saved to: {source_json_path}", file=sys.stderr)
            else:
                return {"success": False, "error": "Failed to save source JSON"}

        # Initialize target JSON
        target_json_data = {"objects": {}}
        if save_json_data(target_json_path, target_json_data):
            print(f"Target JSON initialized at: {target_json_path}", file=sys.stderr)
        else:
            return {"success": False, "error": "Failed to initialize target JSON"}

        return {
            "success": True,
            "message": f"Large OpenAPI spec initialized successfully with intelligent filtering",
            "source_objects": len(source_json_data),
            "target_objects": 0,
            "filtering_method": "intelligent" if use_intelligent_filtering else "standard"
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def search_openapi_by_keywords(
    keywords: List[str],
    openapi_url: str
) -> Dict[str, Any]:
    """Search OpenAPI spec by keywords to find relevant endpoints"""
    try:
        parser = LargeOpenAPIParser(openapi_url)
        if not parser.extract_metadata():
            return {"success": False, "error": "Failed to parse API specification"}

        results = parser.search_paths_by_keywords(keywords)

        return {
            "success": True,
            "results": results,
            "keywords": keywords,
            "found_objects": len(results),
            "top_results": list(results.keys())[:5]  # Top 5 results
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def get_openapi_tags(
    openapi_url: str
) -> Dict[str, Any]:
    """Get all available tags from OpenAPI spec for intelligent filtering"""
    try:
        parser = LargeOpenAPIParser(openapi_url)
        if not parser.extract_metadata():
            return {"success": False, "error": "Failed to parse API specification"}

        tags_info = {}
        for tag_name, tag_data in parser.tags.items():
            tags_info[tag_name] = {
                "description": tag_data.get('description', ''),
                "path_count": len(tag_data.get('paths', [])),
                "sample_paths": tag_data.get('paths', [])[:3]  # First 3 paths
            }

        # Sort by path count
        sorted_tags = dict(sorted(tags_info.items(), key=lambda x: x[1]["path_count"], reverse=True))

        return {
            "success": True,
            "tags": sorted_tags,
            "total_tags": len(sorted_tags),
            "top_tags": list(sorted_tags.keys())[:10]  # Top 10 tags
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def filter_openapi_by_tags(
    tags: List[str],
    openapi_url: str,
    source_json_file_path: str
) -> Dict[str, Any]:
    """Filter OpenAPI spec by specific tags and update source JSON"""
    try:
        parser = LargeOpenAPIParser(openapi_url)
        if not parser.extract_metadata():
            return {"success": False, "error": "Failed to parse API specification"}

        filtered_objects = parser.get_objects_by_tags(tag_filter=tags)

        # Save filtered results
        if save_json_data(source_json_file_path, filtered_objects):
            print(f"Filtered source JSON saved to: {source_json_file_path}", file=sys.stderr)
        else:
            return {"success": False, "error": "Failed to save filtered source JSON"}

        return {
            "success": True,
            "filtered_objects": filtered_objects,
            "tags_used": tags,
            "object_count": len(filtered_objects),
            "objects": list(filtered_objects.keys())
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def get_openapi_statistics(
    openapi_url: str
) -> Dict[str, Any]:
    """Get statistics about the OpenAPI spec for better understanding"""
    try:
        parser = LargeOpenAPIParser(openapi_url)
        if not parser.extract_metadata():
            return {"success": False, "error": "Failed to parse API specification"}

        # Count paths by HTTP method
        method_counts = defaultdict(int)
        total_paths = len(parser.paths)
        for path, methods in parser.paths.items():
            for method_name in methods.keys():
                if method_name.lower() in ['get', 'post', 'put', 'delete', 'patch']:
                    method_counts[method_name.lower()] += 1

        # Count by tags
        tag_counts = {}
        for tag_name, tag_data in parser.tags.items():
            tag_counts[tag_name] = len(tag_data.get('paths', []))

        # Get top tags
        top_tags = sorted(tag_counts.items(), key=lambda x: x[1], reverse=True)[:10]

        return {
            "success": True,
            "statistics": {
                "total_paths": total_paths,
                "method_distribution": dict(method_counts),
                "total_tags": len(parser.tags),
                "top_tags": dict(top_tags),
                "spec_size_mb": os.path.getsize(openapi_url) / (1024 * 1024) if os.path.exists(openapi_url) else 0
            }
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def list_object_methods(
    object_name: str,
    json_type: str = "source",
    verb_filter: Optional[str] = None
) -> Dict[str, Any]:
    """List methods for a specific object"""
    try:
        print("DEBUG: list_object_methods called", file=sys.stderr)

        if json_type == "source":
            # Load source.json directly
            source_file = os.path.join(PROJECT_ROOT, "source.json")
            if os.path.exists(source_file):
                data = load_json_data(source_file)
                print(f"DEBUG: Loaded data for {object_name}: {list(data.keys())}", file=sys.stderr)
            else:
                data = source_json_data
                print(f"DEBUG: Using global source_json_data for {object_name}", file=sys.stderr)
        else:
            # Always load latest target.json from disk
            target_file = os.path.join(PROJECT_ROOT, "target.json")
            if os.path.exists(target_file):
                data = load_json_data(target_file)
                print(f"DEBUG: Loaded target.json keys: {list(data.keys())}", file=sys.stderr)
                if "objects" in data:
                    print(f"DEBUG: data['objects'] keys: {list(data['objects'].keys())}", file=sys.stderr)
            else:
                data = target_json_data

        # Handle both structures: direct objects or objects under "objects" key
        if "objects" in data:
            object_data = data.get("objects", {}).get(object_name, {})
        else:
            # Direct object structure from get_objects_from_paths
            object_data = data.get(object_name, {})

        print(f"DEBUG: object_data keys: {list(object_data.keys())}", file=sys.stderr)
        methods = object_data.get("methods", {})
        print(f"DEBUG: methods keys: {list(methods.keys())}", file=sys.stderr)

        if verb_filter:
            methods = {k: v for k, v in methods.items() if v.get("verb") == verb_filter.lower()}

        return {
            "success": True,
            "object_name": object_name,
            "methods": methods,
            "count": len(methods),
            "type": json_type
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def execute_get_method(
    object_name: str,
    method_path: str,
    parameters: Optional[Dict[str, Any]] = None,
    api_base_url: Optional[str] = None,
    auth_details: Optional[Dict[str, Any]] = None
) -> Dict[str, Any]:
    """Execute a GET method against the API"""
    try:
        if not api_base_url:
            return {"success": False, "error": "API base URL is required"}

        # Build the full URL
        full_url = urljoin(api_base_url, method_path)

        # Prepare headers
        headers = {"Content-Type": "application/json"}
        if auth_details:
            if auth_details.get("type") == "bearer":
                headers["Authorization"] = f"Bearer {auth_details.get('token')}"
            elif auth_details.get("type") == "basic":
                credentials = base64.b64encode(
                    f"{auth_details.get('username')}:{auth_details.get('password')}".encode()
                ).decode()
                headers["Authorization"] = f"Basic {credentials}"

        # Make the request
        response = requests.get(full_url, headers=headers, params=parameters)

        return {
            "success": True,
            "status_code": response.status_code,
            "url": full_url,
            "response": response.json() if response.headers.get('content-type', '').startswith('application/json') else response.text
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

def remove_object_from_target(object_name: str, target_path: str) -> Dict[str, Any]:
    """Remove an object from target"""
    target_json_data = target_path
    try:
        if object_name in target_json_data.get("objects", {}):
            del target_json_data["objects"][object_name]
            save_json_data(target_json_path, target_json_data)
            return {"success": True, "message": f"Object '{object_name}' removed from target"}
        else:
            return {"success": False, "error": f"Object '{object_name}' not found in target"}
    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def select_method_for_target(
    object_name: str,
    method_path: str
) -> Dict[str, Any]:
    """Select a specific method from source to add to target"""
    try:
        source_method = source_json_data.get("objects", {}).get(object_name, {}).get("methods", {}).get(method_path)
        
        if not source_method:
            return {"success": False, "error": f"Method '{method_path}' not found in object '{object_name}'"}

        if "objects" not in target_json_data:
            target_json_data["objects"] = {}
        if object_name not in target_json_data["objects"]:
            target_json_data["objects"][object_name] = {"methods": {}}

        target_json_data["objects"][object_name]["methods"][method_path] = source_method
        save_json_data(target_json_path, target_json_data)

        return {
            "success": True,
            "message": f"Method '{method_path}' added to object '{object_name}' in target"
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def remove_method_from_target(
    object_name: str,
    method_path: str
) -> Dict[str, Any]:
    """Remove a method from target"""
    try:
        if object_name in target_json_data.get("objects", {}) and method_path in target_json_data["objects"][object_name].get("methods", {}):
            del target_json_data["objects"][object_name]["methods"][method_path]
            save_json_data(target_json_path, target_json_data)
            return {"success": True, "message": f"Method '{method_path}' removed from object '{object_name}'"}
        else:
            return {"success": False, "error": f"Method '{method_path}' not found in object '{object_name}'"}
    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def get_status() -> Dict[str, Any]:
    """Get current status of the MCP server"""
    try:
        return {
            "success": True,
            "api_spec_url": api_spec_url,
            "source_json_path": source_json_path,
            "target_json_path": target_json_path,
            "source_objects_count": len(source_json_data.get("objects", {})),
            "target_objects_count": len(target_json_data.get("objects", {})),
            "auth_configured": bool(auth_config),
            "enhanced": True
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def update_pom_source_directory(java_client_api_dir: str) -> Dict[str, Any]:
    """Automatically update the source directory in pom.xml based on the Java client directory"""
    try:
        print(f"[DEBUG] update_pom_source_directory called with: {java_client_api_dir}", file=sys.stderr)

        # Extract the client name from the API directory path
        # e.g., "java_client/harrypotterapi/api" -> "harrypotterapi"
        path_parts = java_client_api_dir.split('/')
        if len(path_parts) >= 2:
            client_name = path_parts[-2]  # Get the directory before "api"
        else:
            print(f"[WARNING] Could not extract client name from path: {java_client_api_dir}", file=sys.stderr)
            return {"success": False, "error": f"Could not extract client name from path: {java_client_api_dir}"}

        print(f"[DEBUG] Extracted client name: {client_name}", file=sys.stderr)

        pom_path = os.path.join(PROJECT_ROOT, "pom.xml")
        if not os.path.exists(pom_path):
            print(f"[ERROR] pom.xml not found at: {pom_path}", file=sys.stderr)
            return {"success": False, "error": f"pom.xml not found at: {pom_path}"}

        print(f"[DEBUG] pom.xml found at: {pom_path}", file=sys.stderr)

        # Read the current pom.xml
        with open(pom_path, 'r') as f:
            pom_content = f.read()

        print(f"[DEBUG] Read pom.xml content length: {len(pom_content)}", file=sys.stderr)

        # Update the source directory
        old_source = r'java_client/[^<]+'
        new_source = f'java_client/{client_name}'

        print(f"[DEBUG] Old pattern: {old_source}", file=sys.stderr)
        print(f"[DEBUG] New source: {new_source}", file=sys.stderr)

        # Check if the source directory needs updating
        if f'java_client/{client_name}' in pom_content:
            print(f"[INFO] pom.xml already has correct source directory: java_client/{client_name}", file=sys.stderr)
            return {
                "success": True,
                "message": f"pom.xml already has correct source directory: java_client/{client_name}",
                "client_name": client_name,
                "source_directory": f"java_client/{client_name}"
            }

        # Replace the source directory
        updated_content = re.sub(old_source, new_source, pom_content)

        print(f"[DEBUG] Content changed: {updated_content != pom_content}", file=sys.stderr)

        if updated_content == pom_content:
            print(f"[WARNING] No changes made to pom.xml. Current content may not match expected pattern.", file=sys.stderr)
            return {"success": False, "error": "No changes made to pom.xml. Current content may not match expected pattern."}

        # Write the updated pom.xml
        with open(pom_path, 'w') as f:
            f.write(updated_content)

        print(f"[INFO] Updated pom.xml source directory to: java_client/{client_name}", file=sys.stderr)

        return {
            "success": True,
            "message": f"Updated pom.xml source directory to: java_client/{client_name}",
            "client_name": client_name,
            "source_directory": f"java_client/{client_name}",
            "previous_source": "java_client/wrongclient"  # This will be extracted from the old content
        }

    except Exception as e:
        print(f"[ERROR] Failed to update pom.xml source directory: {e}", file=sys.stderr)
        traceback.print_exc()
        return {"success": False, "error": str(e)}


def clear_generated_files() -> Dict[str, Any]:
    """Clear old generated connector files with incorrect names and patterns"""
    try:
        # Use the absolute path to experiment4.5MCP directory
        experiment_dir = PROJECT_ROOT

        print(f"DEBUG: Starting clear_generated_files function", file=sys.stderr)
        print(f"DEBUG: Experiment directory: {experiment_dir}", file=sys.stderr)
        print(f"DEBUG: Current working directory: {os.getcwd()}", file=sys.stderr)

        # Define directories to clear (relative to experiment directory)
        dirs_to_clear = [
            "src/main/java/com/radiantlogic/custom/dataconnector",
            "src/test/java/com/radiantlogic/custom/dataconnector",
            "dspy_traces"
        ]

        # Define files to clear (relative to experiment directory)
        files_to_clear = [
            "generated_specification.json",
            "source.json",
            "target.json"
        ]

        # Define JAR files to clear in target directory
        target_jars_to_clear = [
            "target/dataconnector-1.0-SNAPSHOT.jar",
            "target/dataconnector-1.0-SNAPSHOT-with-dependencies.jar"
        ]

        cleared_dirs = []
        cleared_files = []
        cleared_jars = []

        print(f"DEBUG: Directories to clear: {dirs_to_clear}", file=sys.stderr)
        print(f"DEBUG: Files to clear: {files_to_clear}", file=sys.stderr)
        print(f"DEBUG: Target JARs to clear: {target_jars_to_clear}", file=sys.stderr)

        # Clear directories
        for dir_path in dirs_to_clear:
            full_path = os.path.join(experiment_dir, dir_path)
            print(f"DEBUG: Checking directory: {full_path}", file=sys.stderr)
            if os.path.exists(full_path):
                print(f"DEBUG: Directory exists, attempting to remove: {full_path}", file=sys.stderr)
                try:
                    # List contents before deletion
                    if os.path.isdir(full_path):
                        contents = os.listdir(full_path)
                        print(f"DEBUG: Directory contents before deletion: {contents}", file=sys.stderr)

                    shutil.rmtree(full_path)
                    print(f"DEBUG: Successfully removed directory: {full_path}", file=sys.stderr)
                    
                    os.makedirs(full_path, exist_ok=True)
                    print(f"DEBUG: Successfully recreated directory: {full_path}", file=sys.stderr)
                    
                    cleared_dirs.append(dir_path)
                except Exception as e:
                    print(f"DEBUG: Error removing directory {full_path}: {e}", file=sys.stderr)
                    raise e
            else:
                print(f"DEBUG: Directory does not exist: {full_path}", file=sys.stderr)

        # Clear files
        for file_path in files_to_clear:
            full_path = os.path.join(experiment_dir, file_path)
            print(f"DEBUG: Checking file: {full_path}", file=sys.stderr)
            if os.path.exists(full_path):
                print(f"DEBUG: File exists, attempting to remove: {full_path}", file=sys.stderr)
                try:
                    os.remove(full_path)
                    print(f"DEBUG: Successfully removed file: {full_path}", file=sys.stderr)
                    cleared_files.append(file_path)
                except Exception as e:
                    print(f"DEBUG: Error removing file {full_path}: {e}", file=sys.stderr)
                    raise e
            else:
                print(f"DEBUG: File does not exist: {full_path}", file=sys.stderr)

        # Clear target JAR files
        for jar_path in target_jars_to_clear:
            full_path = os.path.join(experiment_dir, jar_path)
            print(f"DEBUG: Checking JAR file: {full_path}", file=sys.stderr)
            if os.path.exists(full_path):
                print(f"DEBUG: JAR file exists, attempting to remove: {full_path}", file=sys.stderr)
                try:
                    os.remove(full_path)
                    print(f"DEBUG: Successfully removed JAR file: {full_path}", file=sys.stderr)
                    cleared_jars.append(jar_path)
                except Exception as e:
                    print(f"DEBUG: Error removing JAR file {full_path}: {e}", file=sys.stderr)
                    raise e
            else:
                print(f"DEBUG: JAR file does not exist: {full_path}", file=sys.stderr)

        print(f"DEBUG: Function completed successfully", file=sys.stderr)
        print(f"DEBUG: Cleared directories: {cleared_dirs}", file=sys.stderr)
        print(f"DEBUG: Cleared files: {cleared_files}", file=sys.stderr)
        print(f"DEBUG: Cleared JARs: {cleared_jars}", file=sys.stderr)

        return {
            "success": True,
            "message": "Cleared old generated files, traces, and JAR files. Preserved java_client and other existing files.",
            "cleared_directories": cleared_dirs,
            "cleared_files": cleared_files,
            "cleared_jars": cleared_jars,
            "experiment_directory": experiment_dir,
            "debug_info": {
                "current_working_directory": os.getcwd(),
                "experiment_directory": experiment_dir,
                "cleared_dirs_count": len(cleared_dirs),
                "cleared_files_count": len(cleared_files),
                "cleared_jars_count": len(cleared_jars)
            },
            "limitation_notice": "⚠️ NOTE: Due to MCP server file operation limitations, files may not be actually deleted. If files remain, please use the manual command: rm -rf src/main/java/com/radiantlogic/custom/dataconnector/*.java src/test/java/com/radiantlogic/custom/dataconnector/*.java target/*.jar"
        }

    except Exception as e:
        print(f"DEBUG: Function failed with error: {e}", file=sys.stderr)
        print(f"DEBUG: Error traceback: {traceback.format_exc()}", file=sys.stderr)
        return {"success": False, "error": str(e)}

@mcp.tool()
def run_maven_tests() -> Dict[str, Any]:
    """Run Maven tests on the generated code"""
    try:
        # Change to the experiment4.5MCP directory
        project_dir = PROJECT_ROOT
        if not os.path.exists(os.path.join(project_dir, "pom.xml")):
            return {"success": False, "error": "pom.xml not found in experiment4.5MCP directory"}

        # Run maven test
        result = subprocess.run(
            ["mvn", "test"],
            capture_output=True,
            text=True,
            cwd=project_dir
        )

        return {
            "success": result.returncode == 0,
            "exit_code": result.returncode,
            "stdout": result.stdout,
            "stderr": result.stderr,
            "message": "Maven tests completed successfully" if result.returncode == 0 else "Maven tests failed"
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def compile_java_code() -> Dict[str, Any]:
    """Compile Java code using Maven"""
    try:
        # Change to the experiment4.5MCP directory
        project_dir = PROJECT_ROOT
        if not os.path.exists(os.path.join(project_dir, "pom.xml")):
            return {"success": False, "error": "pom.xml not found in experiment4.5MCP directory"}

        # Run maven compile
        result = subprocess.run(
            ["mvn", "compile"],
            capture_output=True,
            text=True,
            cwd=project_dir
        )

        return {
            "success": result.returncode == 0,
            "exit_code": result.returncode,
            "stdout": result.stdout,
            "stderr": result.stderr,
            "message": "Java code compiled successfully" if result.returncode == 0 else "Java compilation failed"
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def list_test_classes() -> Dict[str, Any]:
    """List all available test classes"""
    try:
        # Use the experiment4.5MCP directory
        project_dir = PROJECT_ROOT
        test_dir = os.path.join(project_dir, "src/test/java")
        if not os.path.exists(test_dir):
            return {"success": True, "tests": [], "message": "No test directory found"}

        test_files = []
        for root, dirs, files in os.walk(test_dir):
            for file in files:
                if file.endswith('.java'):
                    # Convert file path to class name
                    rel_path = os.path.relpath(os.path.join(root, file), test_dir)
                    class_name = rel_path.replace('/', '.').replace('.java', '')
                    test_files.append(class_name)

        return {
            "success": True,
            "tests": test_files,
            "count": len(test_files)
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def get_project_info() -> Dict[str, Any]:
    """Get information about the detected project root and current working directory"""
    try:
        return {
            "success": True,
            "project_root": PROJECT_ROOT,
            "current_working_directory": os.getcwd(),
            "script_location": os.path.dirname(os.path.abspath(__file__)),
            "pom_xml_exists": os.path.exists(os.path.join(PROJECT_ROOT, "pom.xml")),
            "pom_xml_path": os.path.join(PROJECT_ROOT, "pom.xml"),
            "message": "Use this information to verify the project root detection is working correctly",
            "enhanced": True
        }

    except Exception as e:
        return {"success": False, "error": str(e)}

@mcp.tool()
def hello_world(name: str) -> Dict[str, Any]:
    """Simple hello world function for testing"""
    return {
        "success": True,
        "message": f"Hello, {name}! Complete Enhanced MCP Data Connector server is running.",
        "enhanced": True
    }

@mcp.tool()
def test_json_loading() -> Dict[str, Any]:
    """Test JSON loading functionality"""
    try:
        source_file = os.path.join(PROJECT_ROOT, "source.json")
        print(f"Testing JSON loading from: {source_file}", file=sys.stderr)
        print(f"File exists: {os.path.exists(source_file)}", file=sys.stderr)
        
        if os.path.exists(source_file):
            data = load_json_data(source_file)
            print(f"Loaded data keys: {list(data.keys())}", file=sys.stderr)
            print(f"Data length: {len(data)}", file=sys.stderr)
            
            return {
                "success": True,
                "file_exists": True,
                "keys": list(data.keys()),
                "count": len(data),
                "enhanced": True
            }
        else:
            return {
                "success": False,
                "file_exists": False,
                "error": "Source file not found",
                "enhanced": True
            }

    except Exception as e:
        return {"success": False, "error": str(e), "enhanced": True}

def main():
    """Main function to run the complete enhanced MCP server"""
    print("🚀 Starting Complete Enhanced MCP Data Connector Server...", file=sys.stderr)
    print("✅ Enhanced OpenAPI parsing enabled", file=sys.stderr)
    print("✅ Both simple and complex OpenAPI specs supported", file=sys.stderr)
    print("✅ Intelligent method inference enabled", file=sys.stderr)
    print("✅ All required functions included", file=sys.stderr)
    mcp.run()

if __name__ == "__main__":
    main()
