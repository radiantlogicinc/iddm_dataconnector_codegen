"""
Generate code command.
This command generates data connector code for the selected objects.
"""

import fastworkflow
import logging
from fastworkflow.train.generate_synthetic import generate_diverse_utterances
from pydantic import BaseModel, Field

from ...application.data_connector import DataConnector

# Set up logger for command tracking
command_logger = logging.getLogger('command_tracker')

class Signature:
    """Generate data connector code for selected objects."""
    
    class Input(BaseModel):
        overwrite: bool = Field(
            default=False,
            description="Whether to overwrite the current version or create a new one"
        )
    
    plain_utterances = [
        "generate data connector code",
        "create code for selected objects",
        "build the data connector",
        "generate the connector code for target objects",
        "create data connector for selected objects",
        "build code for the data connector",
        "generate java code for the connector",
        "generate code and overwrite the current version",
        "create a new version of the data connector code",
        "build the connector in a new version",
        "generate code in a new src folder",
        "overwrite the existing code with new connector"
    ]
    
    @staticmethod
    def generate_utterances(workflow: fastworkflow.Workflow, command_name: str) -> list[str]:
        """Generate training utterances for the command."""
        return [
            command_name.split('/')[-1].lower().replace('_', ' ')
        ] + generate_diverse_utterances(Signature.plain_utterances, command_name)


class ResponseGenerator:
    """Generate data connector code for selected objects."""
    
    def __call__(
        self,
        workflow: fastworkflow.Workflow,
        command: str,
        command_parameters: Signature.Input,
    ) -> fastworkflow.CommandOutput:
        """Process the command and generate a response."""
        # Log the command parameters
        command_logger.info(f"[COMMAND] Generate code called with overwrite={command_parameters.overwrite}")
        
        data_connector: DataConnector = workflow.command_context_for_response_generation
        
        # Check if data connector is initialized
        if not data_connector.initialized:
            return fastworkflow.CommandOutput(
                workflow_id=workflow.id,
                command_responses=[
                    fastworkflow.CommandResponse(
                        response="Error: Data connector is not initialized. Please initialize it first."
                    )
                ]
            )
        
        # Check if any objects have been selected
        if not data_connector.selected_objects:
            return fastworkflow.CommandOutput(
                workflow_id=workflow.id,
                command_responses=[
                    fastworkflow.CommandResponse(
                        response="Error: No objects have been selected for the target. Please select at least one object first."
                    )
                ]
            )
        
        # Log before calling generate_code
        command_logger.info(f"[COMMAND] About to call data_connector.generate_code with overwrite={command_parameters.overwrite}")
        command_logger.info(f"[COMMAND] Selected objects: {data_connector.selected_objects}")
        
        # Generate code with overwrite parameter
        result = data_connector.generate_code(overwrite=command_parameters.overwrite)
        
        # Log after calling generate_code
        command_logger.info(f"[COMMAND] data_connector.generate_code completed with result success={result.get('success', False)}")
        if result.get("success", False):
            command_logger.info(f"[COMMAND] Version created/used: {result.get('version', 'unknown')}")
        
        if result.get("success", False):
            version_info = result.get("version", "?")
            action_type = "overwritten" if command_parameters.overwrite else "created"
            
            response = (
                f"Data connector code {action_type} successfully in {version_info}.\n"
                f"Selected objects: {', '.join(data_connector.selected_objects)}\n"
            )
            
            if "unified_connector_generated" in result and result["unified_connector_generated"]:
                files = result.get("generated_files", {}).get("unified_connector", {})
                response += (
                    f"Generated files:\n"
                    f"- Connector: {files.get('connector', 'N/A')}\n"
                    f"- Tests: {files.get('tests', 'N/A')}\n"
                    f"- Config: {files.get('config', 'N/A')}\n"
                )
                
                # Add best score to the output
                if "best_score" in result:
                    response += f"Code quality score: {result['best_score']}/100\n"
                
                build_status = result.get("final_build_status", {})
                if build_status.get("success", False):
                    response += f"Build status: Success\n"
                else:
                    response += f"Build status: Failed - {build_status.get('errors', 'Unknown error')}\n"
            else:
                response += "No files were generated."
                
            # Add information about target.json
            if result.get("target_json_moved_to"):
                response += (
                    f"\nTarget JSON has been moved to: {result.get('target_json_moved_to')}\n"
                    f"A new empty target JSON has been created for future selections."
                )
        else:
            response = (
                f"Failed to generate code: {result.get('error', 'Unknown error')}"
            )
        
        # Create the response output
        output = fastworkflow.CommandOutput(
            workflow_id=workflow.id,
            command_responses=[
                fastworkflow.CommandResponse(response=response)
            ]
        )
        
        # Reset selected objects after constructing the response
        if result.get("success", False):
            data_connector.selected_objects = []
            
        return output
