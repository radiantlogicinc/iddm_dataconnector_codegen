package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.annotations.CustomConnector;
import com.radiantlogic.iddm.annotations.ManagedComponent;
import com.radiantlogic.iddm.annotations.Property;
import com.radiantlogic.iddm.base.Logger;
import com.radiantlogic.iddm.base.SearchOperations;
import com.radiantlogic.iddm.base.TestConnectionOperations;
import com.radiantlogic.iddm.ldap.LdapSearchRequest;
import com.radiantlogic.iddm.base.TestConnectionRequest;
import com.radiantlogic.iddm.ldap.LdapResponse;
import com.radiantlogic.iddm.base.TestConnectionResponse;
import com.radiantlogic.iddm.base.ReadOnlyProperties;
import com.radiantlogic.iddm.base.InjectableProperties;
import com.radiantlogic.iddm.ldap.LdapResultCode;

import java.util.*;

@CustomConnector(metaJsonFile = "idpConnector.json")
@ManagedComponent
public class IdpDataConnector implements SearchOperations<LdapResponse>, TestConnectionOperations {

    private final Logger log;
    private final ReadOnlyProperties connectionProperties;

    public IdpDataConnector(
        Logger log,
        @Property(name = InjectableProperties.CUSTOM_DATASOURCE_PROPERTIES) ReadOnlyProperties connectionProperties
    ) {
        this.log = log;
        this.connectionProperties = connectionProperties;
    }

    @Override
    public TestConnectionResponse testConnection(TestConnectionRequest request) {
        try {
            log.info("Testing connection to Idp API");
            
            // Basic connection test
            String baseUrl = connectionProperties.getProperty("baseUrl", "");
            if (baseUrl.isEmpty()) {
                return TestConnectionResponse.from("IdpAPI", false, "Base URL not configured");
            }
            
            return TestConnectionResponse.from("IdpAPI", true, "Connection successful");
        } catch (Exception e) {
            log.error("Connection test failed", e);
            return TestConnectionResponse.from("IdpAPI", false, e.getMessage());
        }
    }

    @Override
    public LdapResponse search(LdapSearchRequest searchRequest) {
        try {
            log.info("Performing search for Idp objects");
            
            String filter = searchRequest.getFilter().toString();
            log.debug("Search filter: " + filter);
            
            // Create mock results for selected objects: idp
            List<Map<String, Object>> results = new ArrayList<>();
            
            // Add sample data for demonstration
            Map<String, Object> sampleResult = new HashMap<>();
            sampleResult.put("id", "1");
            sampleResult.put("name", "Sample Idp Object");
            sampleResult.put("type", "idp");
            results.add(sampleResult);
            
            return new LdapResponse<>(LdapResultCode.SUCCESS, results.toString());
        } catch (Exception e) {
            log.error("Search failed", e);
            return new LdapResponse<>(LdapResultCode.OTHER, "Error: " + e.getMessage());
        }
    }
}