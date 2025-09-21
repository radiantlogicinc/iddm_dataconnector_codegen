"""
Select object command.
This command selects an object from the source JSON to add to the target JSON.
"""
from typing import List
import fastworkflow
from fastworkflow.utils.signatures import DatabaseValidator
from fastworkflow.train.generate_synthetic import generate_diverse_utterances
from pydantic import BaseModel, Field

from ...application.data_connector import DataConnector

class Signature:
    """Select an object from source to add to target."""
    
    class Input(BaseModel):
        object_name: List[str] = Field(
            description="Name of the object to select",
            examples=["'users', 'products', 'orders', 'characters'"],
            json_schema_extra={'db_lookup': False}
        )
    
    plain_utterances = [
        "select the users, product and orders object for target",
        "add products and users object to target json",
        "I want to include the orders object",
        "choose the customers object for data connector",
        "select object users and products",
        "add object products, users, orders and characters to target",
        "include object orders in the target json"
    ]
    
    @staticmethod
    def db_lookup(workflow: fastworkflow.Workflow, 
                  field_name: List[str], 
                  field_value: List[str]
                  ) -> tuple[bool, str | None, list[str]]:
        if field_name == 'object_name':
            data_connector: DataConnector = workflow.command_context_for_response_generation
            key_values = data_connector.source_objects.get("objects", [])
            matched, corrected_value, field_value_suggestions = DatabaseValidator.fuzzy_match(field_value, key_values)
            return (matched, corrected_value, field_value_suggestions)
        return (False, '', [])

    @staticmethod
    def generate_utterances(workflow: fastworkflow.Workflow, command_name: str) -> list[str]:
        """Generate training utterances for the command."""
        return [
            command_name.split('/')[-1].lower().replace('_', ' ')
        ] + generate_diverse_utterances(Signature.plain_utterances, command_name)


class ResponseGenerator:
    """Select an object from source to add to target."""
    
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
        
        # Check if the object exists in the source JSON
        source_objects_result = data_connector.list_objects("source")
        if not source_objects_result.get("success", False):
            return fastworkflow.CommandOutput(
                workflow_id=workflow.id,
                command_responses=[
                    fastworkflow.CommandResponse(
                        response=f"Failed to list source objects: {source_objects_result.get('error', 'Unknown error')}"
                    )
                ]
            )
        
        source_objects = source_objects_result.get("objects", [])
        
        # Check if each object in the list exists in source_objects
        missing_objects = [obj for obj in command_parameters.object_name if obj not in source_objects]
        if missing_objects:
            return fastworkflow.CommandOutput(
                workflow_id=workflow.id,
                command_responses=[
                    fastworkflow.CommandResponse(
                        response=f"Error: Object(s) {missing_objects} not found in source JSON. Available objects: {', '.join(source_objects)}"
                    )
                ]
            )
        
        # Select the object
        result = data_connector.select_object(command_parameters.object_name)
        
        if result.get("success", False):
            response = (
                f"Object(s) {command_parameters.object_name} added to target.\n"
                f"Methods count: {result.get('methods_count', 0)}\n"
                f"Current objects in target: {', '.join(result.get('current_objects', []))}"
            )
        else:
            response = (
                f"Failed to select object: {result.get('error', 'Unknown error')}"
            )
        
        return fastworkflow.CommandOutput(
            workflow_id=workflow.id,
            command_responses=[
                fastworkflow.CommandResponse(response=response)
            ]
        )
