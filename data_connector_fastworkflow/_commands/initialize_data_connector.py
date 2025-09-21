"""
Initialize data connector command.
This command accepts paths as parameters and initializes the DataConnector.
"""

import fastworkflow
from fastworkflow.train.generate_synthetic import generate_diverse_utterances
from pydantic import BaseModel, Field
import os
from typing import Optional

from ..application.data_connector import DataConnector

class Signature:
    """Initialize the data connector with provided paths."""
    
    class Input(BaseModel):
        api_spec_path: str = Field(
            description="Path to the OpenAPI specification file (required)",
            examples=[
                "/home/shivam/data_connector_fastworkflow2/yamlfiles/harry_potter_openapi.yaml",
                "/path/to/openapi-spec.yaml",
                "/home/user/api/swagger.json"
            ]
        )
        java_client_api_dir: Optional[str] = Field(
            default=None,
            description="Path to the Java client API directory (optional)",
            examples=[
                "/home/shivam/data_connector_fastworkflow2/java_client/harrypotterapi/api",
                "/path/to/java_client/api",
                "/home/user/java_client/myapi/api"
            ]
        )
        java_client_model_dir: Optional[str] = Field(
            default=None,
            description="Path to the Java client model directory (optional)",
            examples=[
                "/home/shivam/data_connector_fastworkflow2/java_client/harrypotterapi/model",
                "/path/to/java_client/model",
                "/home/user/java_client/myapi/model"
            ]
        )
    
    plain_utterances = [
        "initialize data connector with api spec path /home/shivam/data_connector_fastworkflow2/yamlfiles/harry_potter_openapi.yaml",
        "setup data connector with api spec /home/shivam/data_connector_fastworkflow2/yamlfiles/harry_potter_openapi.yaml and java client api dir /home/shivam/data_connector_fastworkflow2/java_client/harrypotterapi/api",
        "start new data connector project using api spec /path/to/openapi-spec.yaml",
        "initialize openapi spec at /path/to/swagger.json with java client api dir /path/to/java_client/api and model dir /path/to/java_client/model",
        "create data connector with api spec /home/user/api/swagger.json",
        "initialize data connector with api spec path /home/shivam/data_connector_fastworkflow2/yamlfiles/harry_potter_openapi.yaml and java client model dir /home/shivam/data_connector_fastworkflow2/java_client/harrypotterapi/model"
    ]
    
    @staticmethod
    def generate_utterances(workflow: fastworkflow.Workflow, command_name: str) -> list[str]:
        """Generate training utterances for the command."""
        return [
            command_name.split('/')[-1].lower().replace('_', ' ')
        ] + generate_diverse_utterances(Signature.plain_utterances, command_name)


class ResponseGenerator:
    """Initialize the data connector and set it as the root context."""
    
    def __call__(
        self,
        workflow: fastworkflow.Workflow,
        command: str,
        command_parameters: Signature.Input,
    ) -> fastworkflow.CommandOutput:
        """Process the command and generate a response."""
        # Create a new DataConnector instance
        data_connector = DataConnector()
        
        # Get paths from parameters
        api_spec_path = command_parameters.api_spec_path
        java_client_api_dir = command_parameters.java_client_api_dir
        java_client_model_dir = command_parameters.java_client_model_dir
        
        # Generate paths dynamically based on api_spec_path
        # Extract API name from API_SPEC_PATH (between the last "/" and ".yaml" or ".json")
        api_spec_file = os.path.basename(api_spec_path)
        api_name = os.path.splitext(api_spec_file)[0]
        
        # Set root directory to current working directory
        root_dir = os.getcwd()
        
        # Create Api_code directory if it doesn't exist
        api_folder_path = os.path.join(root_dir, "Api_code", api_name)
        os.makedirs(api_folder_path, exist_ok=True)
        
        # Generate source and target JSON paths
        source_json_path = os.path.join(api_folder_path, "source.json")
        target_json_path = os.path.join(api_folder_path, "target.json")
        
        # Set SDK path
        sdk_path = os.path.join(root_dir, "sdkfiles", "minimal-radiantlogic-iddm-sdk.txt")
        
        # Initialize with generated parameters
        result = data_connector.initialize(
            api_spec_path,
            source_json_path,
            target_json_path,
            java_client_api_dir,
            java_client_model_dir,
            sdk_path,
            api_folder_path
        )
        
        # Set as root context
        workflow.root_command_context = data_connector
        
        if result.get("success", False):
            # Get the list of objects from the data connector
            objects_list = data_connector.source_objects.get("objects", [])
            
            response = (
                f"Data connector initialized successfully.\n"
                f"API spec: {api_spec_path}\n"
                f"Source JSON: {source_json_path}\n"
                f"Target JSON: {target_json_path}\n"
                f"API folder: {api_folder_path}\n"
                f"Found {len(objects_list)} source objects in the API spec:\n"
                f"{', '.join(objects_list)}"
            )
            
            if java_client_api_dir:
                response += f"\nJava client API dir: {java_client_api_dir}"
            if java_client_model_dir:
                response += f"\nJava client model dir: {java_client_model_dir}"
        else:
            response = (
                f"Failed to initialize data connector: {result.get('error', 'Unknown error')}"
            )
        
        return fastworkflow.CommandOutput(
            workflow_id=workflow.id,
            command_responses=[
                fastworkflow.CommandResponse(response=response)
            ]
        )
