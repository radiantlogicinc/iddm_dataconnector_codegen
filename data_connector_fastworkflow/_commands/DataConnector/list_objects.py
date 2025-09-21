"""
List objects command.
This command lists objects from the source or target JSON.
"""

import fastworkflow
from fastworkflow.train.generate_synthetic import generate_diverse_utterances
from pydantic import BaseModel, Field

from ...application.data_connector import DataConnector

class Signature:
    """List objects from source or target JSON."""
    
    class Input(BaseModel):
        json_type: str = Field(
            description="Which JSON file to list objects from",
            examples=['source', 'target'],
            default='source'
        )
    
    plain_utterances = [
        "list all objects in source json",
        "show me the objects in target json",
        "what objects are available in the source",
        "display all objects from target",
        "get objects from source json",
        "enumerate objects in target"
    ]
    
    @staticmethod
    def generate_utterances(workflow: fastworkflow.Workflow, command_name: str) -> list[str]:
        """Generate training utterances for the command."""
        return [
            command_name.split('/')[-1].lower().replace('_', ' ')
        ] + generate_diverse_utterances(Signature.plain_utterances, command_name)


class ResponseGenerator:
    """List objects from source or target JSON."""
    
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
        
        result = data_connector.list_objects(command_parameters.json_type)
        
        if result.get("success", False):
            objects = result.get("objects", [])
            if objects:
                response = (
                    f"Found {len(objects)} objects in {command_parameters.json_type} JSON:\n"
                    f"{', '.join(objects)}"
                )
            else:
                response = f"No objects found in {command_parameters.json_type} JSON."
        else:
            response = (
                f"Failed to list objects: {result.get('error', 'Unknown error')}"
            )
        
        return fastworkflow.CommandOutput(
            workflow_id=workflow.id,
            command_responses=[
                fastworkflow.CommandResponse(response=response)
            ]
        )
