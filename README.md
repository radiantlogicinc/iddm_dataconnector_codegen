# IDDM Data Connector CodeGen with MCP Server

An AI-powered application for rapid development of IDDM data connectors, enhanced with a comprehensive Model Context Protocol (MCP) server for streamlined development workflows.

## 🚀 Features

- **MCP Server Integration**: Full Model Context Protocol server for seamless IDE integration
- **OpenAPI Processing**: Automated OpenAPI specification parsing and code generation
- **Java Client Generation**: Automatic generation of Java client code from OpenAPI specs
- **Data Connector Templates**: Pre-built templates and examples for common connector patterns
- **Few-Shot Learning**: AI-powered code generation using example-based learning
- **Maven Integration**: Complete Maven project structure with automated builds

## 📁 Project Structure

```
iddm_dataconnector_codegen/
├── mcp_server/                   # MCP Server implementation
│   ├── mcp_server.py            # Main MCP server with full tooling
│   └── requirements.txt         # Python dependencies for the server
├── examples/                     # Few-shot learning examples
│   ├── HarryPotterDataConnector.java  # Example Java connector class
│   ├── harrypotterbooksconnector.json # Example JSON configuration
│   └── HarryPotterDataConnectorTest.java # Example unit tests
├── yamlfiles/                   # OpenAPI specifications
│   ├── harry_potter_openapi.yaml
│   └── idp-minimal.yaml
├── sdkfiles/                    # SDK reference documentation
│   ├── radiantlogicinc-iddm-sdk.txt
│   ├── minimal-radiantlogic-iddm-sdk.txt
│   └── minimal_user_guide.txt
├── java_client/                 # Generated Java client code
│   ├── harrypotterapi/
│   └── myaccountmanagement/
├── pom.xml                      # Maven project configuration
├── requirements.txt             # Project-level extras (optional)
└── debug_generation.log         # Tooling logs (if present)
```

## 🚀 Quick Start (Current Workflow)

### 1. Install Dependencies

```bash
# Install Python dependencies
cd mcp_server
pip install -r requirements.txt

# Install Java dependencies (if building)
mvn install
```

### 2. Start the MCP Server

```bash
# From anywhere: point to your local clone
python /absolute/path/to/iddm_dataconnector_codegen/mcp_server/mcp_server.py
# or
python3 /absolute/path/to/iddm_dataconnector_codegen/mcp_server/mcp_server.py
```

### 3. Configure in Cursor (works from any project)

In the project where you want to use these tools, create or edit `.cursor/mcp.json` and add an entry:

**Option A: call Python directly with an absolute path**
```json
{
  "mcpServers": {
    "iddm-dataconnector": {
      "command": "python3",
      "args": ["/absolute/path/to/iddm_dataconnector_codegen/mcp_server/mcp_server.py"],
      "disabled": false
    }
  }
}
```

**Option B: ensure the server runs with the correct working directory**
```json
{
  "mcpServers": {
    "iddm-dataconnector": {
      "command": "bash",
      "args": ["-lc", "cd '/absolute/path/to/iddm_dataconnector_codegen/mcp_server' && python3 mcp_server.py"],
      "disabled": false
    }
  }
}
```

**Option C: portable configuration (recommended)**
```json
{
  "mcpServers": {
    "iddm-dataconnector": {
      "command": "/Library/Frameworks/Python.framework/Versions/3.12/bin/python3",
      "args": [
        "/absolute/path/to/iddm_dataconnector_codegen/mcp_server/mcp_server.py"
      ],
      "env": {
        "PYTHONPATH": "/absolute/path/to/iddm_dataconnector_codegen"
      },
      "cwd": "/absolute/path/to/iddm_dataconnector_codegen"
    }
  }
}
```

**Tip**: If your editor supports `${workspaceFolder}`, you can use it in place of absolute paths.

### 4. Use the MCP Tools from Cursor

- **NEW**: Use `get_project_info` to verify the server detected your project root correctly
- Initialize or load an OpenAPI spec (use one under `yamlfiles/`, e.g., `harry_potter_openapi.yaml`)
- Explore/list objects and methods
- Select objects and methods to include in the target

### 5. Generate Code

- Generate Java client code → output under `java_client/`
- Generate Data Connector code → output under `src/main/java/com/radiantlogic/custom/dataconnector/`

### 6. Build the Java Project

```bash
mvn package -DskipTests
```
- Artifacts are written to `target/`

## 🔧 Available MCP Tooling (High Level)

- **Project Management**: `get_project_info` - verify project root detection and configuration
- **OpenAPI Operations**: initialize specs, list objects/methods, select targets
- **Data Connector Operations**: generate Java client code, generate connector code, compile/test with Maven

## ⚠️ Important Notes

- **File Deletion Limitation**: if generated files need to be cleared, remove them manually:
  ```bash
  rm -rf src/main/java/com/radiantlogic/custom/dataconnector/*.java
  rm -rf src/test/java/com/radiantlogic/custom/dataconnector/*.java
  ```
- The `requirements.txt` at the project root is optional; the server uses `mcp_server/requirements.txt`.
- If you work outside the original directory, absolute paths in certain advanced operations (like automatic POM updates) may need adjusting. Core MCP features (OpenAPI, selection, codegen, build) work with the configuration above.

## 🧩 Tips

- OpenAPI specs live under `yamlfiles/`. Point the MCP tools at these when initializing.
- The `src/main/java/com/radiantlogic/custom/dataconnector/` directory will be empty until you generate code.
- Check `debug_generation.log` and `select_object_debug.log` (if present) for troubleshooting information.

## 📚 Examples Folder

The `examples/` folder contains few-shot learning examples used by the MCP server to generate high-quality Data Connector code:

- **`HarryPotterDataConnector.java`** - Complete working Java connector class showing proper structure, imports, and patterns
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

## 🏗️ Architecture

This project combines:
- **FastMCP**: High-performance MCP server framework
- **DSPy**: AI-powered code generation and optimization
- **OpenAPI**: Standard API specification processing
- **Maven**: Java project management and build automation

## 🤝 Contributing

This integration brings together the power of MCP servers with IDDM data connector development, providing a streamlined workflow for rapid connector creation and deployment. We welcome:

- **Feedback**: On usability and feature requests
- **Examples**: Additional connector patterns and templates
- **Testing**: Real-world usage scenarios and edge cases
- **Documentation**: Improvements and additional examples

## 📄 License

See LICENSE file for details.
