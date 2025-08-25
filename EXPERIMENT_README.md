# Data Connector MCP Server - Experiment 4.5

A comprehensive Model Context Protocol (MCP) server for Data Connector development with a streamlined, working workflow.

## 📁 Directory Structure

```
experiment4.5MCP/
├── python_files/                 # Python MCP server
│   ├── mcp_server.py            # Main MCP server
│   └── requirements.txt         # Python dependencies for the server
├── examples/                     # Few-shot learning examples
│   ├── BooksDataConnector.java  # Example Java connector class
│   ├── harrypotterbooksconnector.json # Example JSON configuration
│   └── HarryPotterDataConnectorTest.java # Example unit tests
├── yamlfiles/                   # OpenAPI specifications
│   ├── harry_potter_openapi.yaml
│   └── idp-minimal.yaml
├── sdkfiles/                    # SDK reference docs
│   ├── radiantlogicinc-iddm-sdk.txt
│   ├── minimal-radiantlogic-iddm-sdk.txt
│   └── minimal_user_guide.txt
├── java_client/                 # Generated Java client(s)
│   ├── harrypotterapi/
│   └── myaccountmanagement/
├── src/                         # Java data connector project
│   ├── main/
│   │   ├── java/com/radiantlogic/custom/dataconnector/   # Generated connector code (empty until generated)
│   │   └── resources/
│   └── test/
├── target/                      # Maven build output
├── pom.xml                      # Maven project configuration
├── requirements.txt             # Project-level extras (optional)
├── debug_generation.log         # Tooling logs (if present)
└── select_object_debug.log      # Tooling logs (if present)
```

## 🚀 Quick Start (Current Workflow)

1. Install Python dependencies
   ```bash
   cd python_files
   pip install -r requirements.txt
   ```

2. Start the MCP server
   ```bash
   # From anywhere: point to your local clone
   python /absolute/path/to/experiment4.5MCP/python_files/mcp_server.py
   # or
   python3 /absolute/path/to/experiment4.5MCP/python_files/mcp_server.py
   ```

3. Configure in Cursor (works from any project)
   - In the project where you want to use these tools, create or edit `.cursor/mcp.json` and add an entry.
   - Option A: call Python directly with an absolute path
   ```json
   {
     "mcpServers": {
       "data-connector-mcp": {
         "command": "python3",
         "args": ["/absolute/path/to/experiment4.5MCP/python_files/mcp_server.py"],
         "disabled": false
       }
     }
   }
   ```
   - Option B: ensure the server runs with the correct working directory
   ```json
   {
     "mcpServers": {
       "data-connector-mcp": {
         "command": "bash",
         "args": ["-lc", "cd '/absolute/path/to/experiment4.5MCP/python_files' && python3 mcp_server.py"],
         "disabled": false
       }
     }
   }
   ```
   - Option C: portable configuration (recommended)
   ```json
   {
     "mcpServers": {
       "data-connector-mcp": {
         "command": "/Library/Frameworks/Python.framework/Versions/3.12/bin/python3",
         "args": [
           "/absolute/path/to/experiment4.5MCP/python_files/mcp_server.py"
         ],
         "env": {
           "PYTHONPATH": "/absolute/path/to/experiment4.5MCP"
         },
         "cwd": "/absolute/path/to/experiment4.5MCP"
       }
     }
   }
   ```
   - Tip: If your editor supports `${workspaceFolder}`, you can use it in place of absolute paths.

4. Use the MCP tools from Cursor
   - **NEW**: Use `get_project_info` to verify the server detected your project root correctly
   - Initialize or load an OpenAPI spec (use one under `yamlfiles/`, e.g., `harry_potter_openapi.yaml`)
   - Explore/list objects and methods
   - Select objects and methods to include in the target

5. Generate code
   - Generate Java client code → output under `java_client/`
   - Generate Data Connector code → output under `src/main/java/com/radiantlogic/custom/dataconnector/`

6. Build the Java project
   ```bash
   mvn package -DskipTests
   ```
   - Artifacts are written to `target/`

## 🔧 Available MCP Tooling (high level)

- **Project Management**: `get_project_info` - verify project root detection and configuration
- OpenAPI operations: initialize specs, list objects/methods, select targets
- Data Connector operations: generate Java client code, generate connector code, compile/test with Maven

## ⚠️ Notes

- File deletion limitation: if generated files need to be cleared, remove them manually:
  ```bash
  rm -rf src/main/java/com/radiantlogic/custom/dataconnector/*.java
  rm -rf src/test/java/com/radiantlogic/custom/dataconnector/*.java
  ```
- The `requirements.txt` at the project root is optional; the server uses `python_files/requirements.txt`.
- If you work outside the original directory, absolute paths in certain advanced operations (like automatic POM updates) may need adjusting. Core MCP features (OpenAPI, selection, codegen, build) work with the configuration above.

## 🧩 Tips

- OpenAPI specs live under `yamlfiles/`. Point the MCP tools at these when initializing.
- The `src/main/java/com/radiantlogic/custom/dataconnector/` directory will be empty until you generate code.
- Check `debug_generation.log` and `select_object_debug.log` (if present) for troubleshooting information.

## 📚 Examples Folder

The `examples/` folder contains few-shot learning examples used by the MCP server to generate high-quality Data Connector code:

- **`BooksDataConnector.java`** - Complete working Java connector class showing proper structure, imports, and patterns
- **`harrypotterbooksconnector.json`** - Example JSON configuration file for the connector
- **`HarryPotterDataConnectorTest.java`** - Example unit tests demonstrating proper testing patterns

These examples are automatically loaded by the server and used as templates when generating new connectors. They ensure consistent code quality and proper adherence to the Data Connector framework requirements.

### Customizing Examples
You can modify these example files to:
- Change import patterns for different frameworks
- Update annotation styles
- Modify testing approaches
- Add new functionality patterns

The server will automatically use your updated examples in future code generation. 