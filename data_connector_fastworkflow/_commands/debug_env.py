"""
Debug environment variables command.
This command prints the environment variables to help debug the issue.
"""

import fastworkflow
from fastworkflow.train.generate_synthetic import generate_diverse_utterances
from pydantic import BaseModel, Field
import os

class Signature:
    """Debug environment variables."""
    
    class Input(BaseModel):
        pass  # No input parameters needed
    
    plain_utterances = [
        "debug environment variables",
        "print environment variables",
        "show env vars",
        "check environment"
    ]
    
    @staticmethod
    def generate_utterances(workflow: fastworkflow.Workflow, command_name: str) -> list[str]:
        """Generate training utterances for the command."""
        return [
            command_name.split('/')[-1].lower().replace('_', ' ')
        ] + generate_diverse_utterances(Signature.plain_utterances, command_name)


class ResponseGenerator:
    """Debug environment variables."""
    
    def __call__(
        self,
        workflow: fastworkflow.Workflow,
        command: str,
        command_parameters: Signature.Input,
    ) -> fastworkflow.CommandOutput:
        """Process the command and generate a response."""
        # Get all environment variables
        env_vars = os.environ
        
        # Check for specific environment variables
        api_spec_path = os.environ.get('API_SPEC_PATH', 'Not set')
        source_json_path = os.environ.get('SOURCE_JSON_PATH', 'Not set')
        target_json_path = os.environ.get('TARGET_JSON_PATH', 'Not set')
        java_client_api_dir = os.environ.get('JAVA_CLIENT_API_DIR', 'Not set')
        java_client_model_dir = os.environ.get('JAVA_CLIENT_MODEL_DIR', 'Not set')
        sdk_path = os.environ.get('SDK_PATH', 'Not set')
        
        # Create response
        response = (
            f"Environment variables:\n"
            f"API_SPEC_PATH: {api_spec_path}\n"
            f"SOURCE_JSON_PATH: {source_json_path}\n"
            f"TARGET_JSON_PATH: {target_json_path}\n"
            f"JAVA_CLIENT_API_DIR: {java_client_api_dir}\n"
            f"JAVA_CLIENT_MODEL_DIR: {java_client_model_dir}\n"
            f"SDK_PATH: {sdk_path}\n\n"
            f"All environment variables:\n"
        )
        
        # Add all environment variables
        for key, value in env_vars.items():
            response += f"{key}: {value}\n"
        
        return fastworkflow.CommandOutput(
            workflow_id=workflow.id,
            command_responses=[
                fastworkflow.CommandResponse(response=response)
            ]
        )
