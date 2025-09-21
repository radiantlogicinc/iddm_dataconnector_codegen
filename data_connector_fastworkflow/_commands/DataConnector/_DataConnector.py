"""
Context navigation class for DataConnector.
This class defines how to navigate from the DataConnector context to its parent context.
"""

from ...application.data_connector import DataConnector

class Context:
    """Context navigation class for DataConnector."""
    
    @classmethod
    def get_parent(cls, command_context_object: DataConnector) -> None:
        """
        Get the parent context of the DataConnector.
        
        Args:
            command_context_object: The DataConnector instance
            
        Returns:
            None: DataConnector is the root context
        """
        return None  # DataConnector is the root context
