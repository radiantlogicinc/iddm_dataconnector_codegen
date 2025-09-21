"""
Zip code command.
This command zips the latest generated code and saves it in the zip directory.
"""

import fastworkflow
from fastworkflow.train.generate_synthetic import generate_diverse_utterances
from pydantic import BaseModel

from ...application.data_connector import DataConnector

class Signature:
    """Zip the latest generated code."""
    
    class Input(BaseModel):
        pass
    
    plain_utterances = [
        "zip the latest code",
        "create a zip file of the latest version",
        "archive the latest generated code",
        "compress the latest src folder",
        "zip the most recent version",
        "create a zip archive of the latest code",
        "package the latest src version",
        "zip up the latest generated source code"
    ]
    
    @staticmethod
    def generate_utterances(workflow: fastworkflow.Workflow, command_name: str) -> list[str]:
        """Generate training utterances for the command."""
        return [
            command_name.split('/')[-1].lower().replace('_', ' ')
        ] + generate_diverse_utterances(Signature.plain_utterances, command_name)


class ResponseGenerator:
    """Zip the latest generated code."""
    
    def __call__(
        self,
        workflow: fastworkflow.Workflow,
        command: str,
        command_parameters: Signature.Input,
    ) -> fastworkflow.CommandOutput:
        """Process the command and generate a response."""
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
        
        # Zip the latest code
        result = data_connector.zip_latest_code()
        
        if result.get("success", False):
            version = result.get("version")
            zip_path = result.get("zip_path")
            
            response = (
                f"Successfully zipped the latest code (version {version}).\n"
                f"Zip file created at: {zip_path}"
            )
        else:
            response = (
                f"Failed to zip code: {result.get('error', 'Unknown error')}"
            )
        
        # Create the response output
        output = fastworkflow.CommandOutput(
            workflow_id=workflow.id,
            command_responses=[
                fastworkflow.CommandResponse(response=response)
            ]
        )
        
        return output
