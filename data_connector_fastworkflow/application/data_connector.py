"""
DataConnector class for FastWorkflow application.
This class wraps the MCP server functions and maintains state between commands.
"""

import os
import shutil
import zipfile
import logging
from . import mcp_server
from . import enhanced_mcp_server

# Set up logger for version tracking
version_logger = logging.getLogger('version_tracker')

class DataConnector:
    """
    DataConnector class that wraps MCP server functions and maintains state.
    """
    
    def __init__(self):
        """Initialize the DataConnector with empty state."""
        self.api_spec_path = None
        self.source_json_path = None
        self.target_json_path = None
        self.java_client_api_dir = None
        self.java_client_model_dir = None
        self.sdk_path = None
        self.selected_objects = []
        self.initialized = False
        self.source_objects = []
        self.api_folder = None
        self.current_version = 1  # Start with version 1
    
    def initialize(self, api_spec_path, source_json_path, target_json_path, 
                  java_client_api_dir, java_client_model_dir, sdk_path, api_folder_path):
        """
        Initialize the data connector with paths from environment variables.
        
        Args:
            api_spec_path: Path to the OpenAPI specification file
            source_json_path: Path where the source JSON file will be created
            target_json_path: Path where the target JSON file will be created
            java_client_api_dir: Path to the Java client API directory
            java_client_model_dir: Path to the Java client model directory
            sdk_path: Path to the SDK documentation
            
        Returns:
            Dict: Result of the initialization
        """
        self.api_spec_path = api_spec_path
        self.source_json_path = source_json_path
        self.target_json_path = target_json_path
        self.java_client_api_dir = java_client_api_dir
        self.java_client_model_dir = java_client_model_dir
        self.sdk_path = sdk_path
        self.api_folder = api_folder_path
        
        # Call the MCP server function to initialize OpenAPI spec
        result = enhanced_mcp_server.initialize_openapi_spec(
            self.api_spec_path, 
            self.source_json_path, 
            self.target_json_path
        )

        self.source_objects = enhanced_mcp_server.list_objects("source")

        
        self.initialized = result.get("success", False)
        return result
    
    def list_objects(self, json_type="source"):
        """
        List objects from source or target JSON.
        
        Args:
            json_type: Which JSON file to list objects from ('source' or 'target')
            
        Returns:
            Dict: Result containing the list of objects
        """
        return enhanced_mcp_server.list_objects(json_type, self.source_json_path, self.target_json_path)
    
    def select_object(self, object_name):
        """
        Select an object from source to add to target.
        
        Args:
            object_name: Name of the object to select
            
        Returns:
            Dict: Result of the selection
        """
        if not isinstance(object_name, (list, tuple)):
            return {"success": False, "error": "object_name must be a list of strings"}

        # Don't reset selected_objects list at the beginning of each call
        # This allows accumulating objects when selecting one by one
        
        aggregated = {
            "success": True,
            "details": {},
            "methods_count": 0,
            "current_objects": []
        }

        for obj in object_name:
            result = enhanced_mcp_server.select_object_for_target(obj, target_path=self.target_json_path, source_path=self.source_json_path)
            aggregated["details"][obj] = result

            if not result.get("success", False):
                aggregated["success"] = False

            if result.get("success", False):
                if obj not in self.selected_objects:
                    self.selected_objects.append(obj)

            try:
                aggregated["methods_count"] += int(result.get("methods_count", 0) or 0)
            except Exception:
                pass

            current = result.get("current_objects")
            if isinstance(current, list):
                for c in current:
                    if c not in aggregated["current_objects"]:
                        aggregated["current_objects"].append(c)

        return aggregated
        # result = enhanced_mcp_server.select_object_for_target(object_name, target_path=self.target_json_path)
        # if result.get("success", False):
        #     if object_name not in self.selected_objects:
        #         self.selected_objects.append(object_name)
        # return result
    
    def generate_code(self, overwrite=True):
        """
        Generate data connector code for selected objects.
        
        Args:
            overwrite: Whether to overwrite the current version or create a new one
            
        Returns:
            Dict: Result of the code generation
        """
        if not self.selected_objects:
            return {"success": False, "error": "No objects selected for target"}
        
        # Determine version to use
        latest_version = self._get_latest_version()
        version_logger.debug(f"Latest version from _get_latest_version: {latest_version}")
        version_logger.debug(f"Overwrite flag: {overwrite}")
        
        # Log before if statement
        version_logger.debug(f"Before if statement - latest_version: {latest_version}")
        
        if overwrite and latest_version > 0:
            # Use the latest version if overwrite is True and there's an existing version
            version_to_use = latest_version
            version_logger.debug(f"Using existing version: {version_to_use}")
        else:
            # Otherwise, use the next version
            version_to_use = latest_version + 1
            version_logger.debug(f"Using next version: {version_to_use}")
        
        # Log after if statement
        version_logger.debug(f"After if statement - version_to_use: {version_to_use}")
        
        # Define version directory path consistently
        version_str = f"src version {version_to_use}"
        version_dir = os.path.join(self.api_folder, version_str)
        version_logger.debug(f"Version directory path: {version_dir}")
        
        # If directory exists, clear its contents
        if os.path.exists(version_dir):
            for item in os.listdir(version_dir):
                item_path = os.path.join(version_dir, item)
                if os.path.isfile(item_path):
                    os.remove(item_path)
                elif os.path.isdir(item_path):
                    shutil.rmtree(item_path)
        else:
            # Create the directory if it doesn't exist
            os.makedirs(version_dir, exist_ok=True)
        
        # Call MCP server to generate code
        result = enhanced_mcp_server.generate_data_connector_code(
            self.java_client_api_dir or "",  # Provide empty string if None
            self.java_client_model_dir or "",  # Provide empty string if None
            self.sdk_path,
            self.target_json_path,
            self.api_folder,
            str(version_to_use),  # Pass just the version number, not the full directory name
            objects=self.selected_objects
        )
        
        if result.get("success", False):
            # Copy target.json to versioned directory
            versioned_target_json = os.path.join(version_dir, "target.json")
            shutil.copy2(self.target_json_path, versioned_target_json)
            
            # Create new empty target.json
            with open(self.target_json_path, 'w') as f:
                f.write('{"objects": {}}')
            
            # Update current version
            self.current_version = version_to_use
                
            # Add version info to result
            result["version"] = version_str
            result["target_json_moved_to"] = versioned_target_json
            result["new_target_json_created"] = True
        
        return result
        
    def _get_next_version(self):
        """Find the next available version number"""
        return self._get_latest_version() + 1

    def _get_latest_version(self):
        """Find the latest version number"""
        highest_version = 0
        version_logger.debug(f"Initial highest_version: {highest_version}")
        
        if os.path.exists(self.api_folder):
            version_logger.debug(f"Scanning directory: {self.api_folder}")
            for item in os.listdir(self.api_folder):
                if item.startswith("src version "):
                    try:
                        version_num = int(item.replace("src version ", ""))
                        version_logger.debug(f"Found version: {version_num}")
                        highest_version = max(highest_version, version_num)
                        version_logger.debug(f"Updated highest_version: {highest_version}")
                    except ValueError:
                        version_logger.warning(f"Could not parse version from: {item}")
                        pass
        
        version_logger.debug(f"Final highest_version: {highest_version}")
        return highest_version
        
    def zip_latest_code(self):
        """
        Zip the latest version folder and save it in the zip directory.
        
        Returns:
            Dict: Result of the zip operation
        """
        # Find the latest version
        latest_version = self._get_latest_version()
        
        if latest_version < 1:
            return {"success": False, "error": "No code has been generated yet"}
        
        # Define paths
        src_dir = os.path.join(self.api_folder, f"src version {latest_version}")
        
        if not os.path.exists(src_dir):
            return {"success": False, "error": f"Source directory {src_dir} does not exist"}
        
        # Create zip directory if it doesn't exist
        zip_dir = os.path.join(self.api_folder, "zip")
        os.makedirs(zip_dir, exist_ok=True)
        
        # Define zip file path
        zip_filename = f"src_version_{latest_version}.zip"
        zip_path = os.path.join(zip_dir, zip_filename)
        
        try:
            # Create zip file
            with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
                for root, dirs, files in os.walk(src_dir):
                    for file in files:
                        file_path = os.path.join(root, file)
                        arcname = os.path.relpath(file_path, src_dir)
                        zipf.write(file_path, arcname)
            
            return {
                "success": True, 
                "version": latest_version,
                "zip_path": zip_path
            }
        except Exception as e:
            return {"success": False, "error": str(e)}
