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
│   └── requirements.txt         # Python dependencies
├── examples/                     # Few-shot learning examples
│   ├── BooksDataConnector.java  # Example Java connector class
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
├── pom.xml                      # Maven project configuration
├── requirements.txt             # Python dependencies
└── EXPERIMENT_README.md         # Detailed experiment documentation
```

## 🛠️ Quick Start

### 1. Install Dependencies

```bash
# Install Python dependencies
cd mcp_server
pip install -r requirements.txt

# Install Java dependencies (if building)
mvn install
```

### 2. Start MCP Server

```bash
python mcp_server/mcp_server.py
```

### 3. Configure in Your IDE

Create `.cursor/mcp.json` in your project:

```json
{
  "mcpServers": {
    "iddm-dataconnector": {
      "command": "python3",
      "args": ["/path/to/iddm_dataconnector_codegen/mcp_server/mcp_server.py"],
      "disabled": false
    }
  }
}
```

## 🔧 MCP Tools Available

- **Project Management**: `get_project_info` - Verify project configuration
- **OpenAPI Operations**: Initialize specs, list objects/methods, select targets
- **Code Generation**: Generate Java clients and Data Connector code
- **Build & Test**: Maven integration for compilation and testing

## 📚 Examples

The `examples/` folder contains working examples that demonstrate:
- Complete Data Connector implementations
- Proper testing patterns
- Configuration file structures
- Best practices for IDDM development

## 🏗️ Architecture

This project combines:
- **FastMCP**: High-performance MCP server framework
- **DSPy**: AI-powered code generation
- **OpenAPI**: Standard API specification processing
- **Maven**: Java project management and build automation

## 📖 Documentation

- See `EXPERIMENT_README.md` for detailed workflow instructions
- Check `sdkfiles/` for SDK reference documentation
- Review examples for implementation patterns

## 🤝 Contributing

This integration brings together the power of MCP servers with IDDM data connector development, providing a streamlined workflow for rapid connector creation and deployment.

## 📄 License

See LICENSE file for details.
