package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.base.annotation.CustomConnector;
import com.radiantlogic.iddm.base.annotation.Property;
import com.radiantlogic.iddm.base.component.ManagedComponent;
import com.radiantlogic.iddm.base.logging.Logger;
import com.radiantlogic.iddm.operation.SearchOperations;
import com.radiantlogic.iddm.operation.TestConnectionOperations;
import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.request.TestConnectionRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import com.radiantlogic.iddm.base.ReadOnlyProperties;
import com.radiantlogic.iddm.base.InjectableProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Unified Data Connector for Sailpoint API IIQ (1).
 * Handles all objects defined in the target JSON in a single class.
 */
@CustomConnector(metaJsonFile = "sailpoint-api.iiq (1)Connector.json")
public class SailpointApiIiq1DataConnector implements
        SearchOperations<LdapSearchRequest>,
        TestConnectionOperations<TestConnectionRequest> {

    private static final Logger LOG = Logger.getLogger(SailpointApiIiq1DataConnector.class);

    // ----- Configuration ----------------------------------------------------
    private final ReadOnlyProperties connectionProperties;

    // ----- API Clients -------------------------------------------------------
    private final AccountsApi accountsApi;
    private final AlertsApi alertsApi;
    private final ApplicationsApi applicationsApi;
    private final CheckedPolicyViolationsApi cpvApi;
    private final EntitlementsApi entitlementsApi;
    private final LaunchedWorkflowsApi launchedWorkflowsApi;
    private final LaunchedWorkflowApi launchedWorkflowApi;
    private final ObjectConfigsApi objectConfigsApi;
    private final ObjectConfigApi objectConfigApi;
    private final PolicyViolationsApi policyViolationsApi;

    // ----- Constructor -------------------------------------------------------
    public SailpointApiIiq1DataConnector(
            @Property(name = "CONNECTION_CONFIGURATION") ReadOnlyProperties connectionProperties,
            AccountsApi accountsApi,
            AlertsApi alertsApi,
            ApplicationsApi applicationsApi,
            CheckedPolicyViolationsApi cpvApi,
            EntitlementsApi entitlementsApi,
            LaunchedWorkflowsApi launchedWorkflowsApi,
            LaunchedWorkflowApi launchedWorkflowApi,
            ObjectConfigsApi objectConfigsApi,
            ObjectConfigApi objectConfigApi,
            PolicyViolationsApi policyViolationsApi) {

        this.connectionProperties = connectionProperties;
        this.accountsApi = accountsApi;
        this.alertsApi = alertsApi;
        this.applicationsApi = applicationsApi;
        this.cpvApi = cpvApi;
        this.entitlementsApi = entitlementsApi;
        this.launchedWorkflowsApi = launchedWorkflowsApi;
        this.launchedWorkflowApi = launchedWorkflowApi;
        this.objectConfigsApi = objectConfigsApi;
        this.objectConfigApi = objectConfigApi;
        this.policyViolationsApi = policyViolationsApi;
    }

    // ----- Search Operation --------------------------------------------------
    @Override
    public LdapResponse search(LdapSearchRequest request) {
        LOG.info("Received LDAP search request: baseDn={}", request.getBaseDn());

        // Very simple mapping: the first OU component determines the object type.
        String baseDn = request.getBaseDn().toLowerCase();
        List<Map<String, Object>> results;

        if (baseDn.contains("ou=accounts")) {
            results = accountsApi.search(request);
        } else if (baseDn.contains("ou=alerts")) {
            results = alertsApi.search(request);
        } else if (baseDn.contains("ou=applications")) {
            results = applicationsApi.search(request);
        } else if (baseDn.contains("ou=checkedpolicyviolations")) {
            results = cpvApi.search(request);
        } else if (baseDn.contains("ou=entitlements")) {
            results = entitlementsApi.search(request);
        } else if (baseDn.contains("ou=launchedworkflows")) {
            results = launchedWorkflowsApi.search(request);
        } else if (baseDn.contains("ou=launchedworkflow")) {
            results = launchedWorkflowApi.search(request);
        } else if (baseDn.contains("ou=objectconfigs")) {
            results = objectConfigsApi.search(request);
        } else if (baseDn.contains("ou=objectconfig")) {
            results = objectConfigApi.search(request);
        } else if (baseDn.contains("ou=policyviolations")) {
            results = policyViolationsApi.search(request);
        } else {
            LOG.warn("Unsupported base DN: {}", baseDn);
            results = Collections.emptyList();
        }

        LOG.info("Search returned {} entries", results.size());
        return new LdapResponse(LdapResponse.ResponseStatus.OK, results);
    }

    // ----- Test Connection Operation -----------------------------------------
    @Override
    public TestConnectionResponse testConnection(TestConnectionRequest request) {
        LOG.info("Testing connection to all Sailpoint endpoints");

        boolean allOk = accountsApi.ping()
                && alertsApi.ping()
                && applicationsApi.ping()
                && cpvApi.ping()
                && entitlementsApi.ping()
                && launchedWorkflowsApi.ping()
                && launchedWorkflowApi.ping()
                && objectConfigsApi.ping()
                && objectConfigApi.ping()
                && policyViolationsApi.ping();

        TestConnectionResponse.ResponseStatus status = allOk
                ? TestConnectionResponse.ResponseStatus.OK
                : TestConnectionResponse.ResponseStatus.ERROR;

        LOG.info("Test connection result: {}", status);
        return new TestConnectionResponse(status);
    }

    // ------------------------------------------------------------------------
    // Helper method to expose configuration for potential future use
    public ReadOnlyProperties getConnectionProperties() {
        return connectionProperties;
    }
}

/* -------------------------------------------------------------------------
 *  Minimal ManagedComponent API client stubs.
 *  In a real implementation each client would perform HTTP calls to the
 *  Sailpoint REST endpoints. For compilation and unit‑testing purposes they
 *  simply return static data.
 * ------------------------------------------------------------------------- */
@ManagedComponent
class AccountsApi {
    public List<Map<String, Object>> search(LdapSearchRequest req) {
        return Collections.singletonList(
                Map.of("id", "acc-123", "displayName", "Demo Account"));
    }
    public boolean ping() { return true; }
}

@ManagedComponent
class AlertsApi {
    public List<Map<String, Object>> search(LdapSearchRequest req) {
        return Collections.singletonList(
                Map.of("id", "alert-456", "name", "Demo Alert"));
    }
    public boolean ping() { return true; }
}

@ManagedComponent
class ApplicationsApi {
    public List<Map<String, Object>> search(LdapSearchRequest req) {
        return Collections.singletonList(
                Map.of("id", "app-789", "name", "Demo Application"));
    }
    public boolean ping() { return true; }
}

@ManagedComponent
class CheckedPolicyViolationsApi {
    public List<Map<String, Object>> search(LdapSearchRequest req) {
        return Collections.singletonList(
                Map.of("id", "cpv-001", "status", "PASSED"));
    }
    public boolean ping() { return true; }
}

@ManagedComponent
class EntitlementsApi {
    public List<Map<String, Object>> search(LdapSearchRequest req) {
        return Collections.singletonList(
                Map.of("id", "ent-002", "application", "DemoApp"));
    }
    public boolean ping() { return true; }
}

@ManagedComponent
class LaunchedWorkflowsApi {
    public List<Map<String, Object>> search(LdapSearchRequest req) {
        return Collections.singletonList(
                Map.of("id", "lwf-003", "workflowName", "DemoWorkflow"));
    }
    public boolean ping() { return true; }
}

@ManagedComponent
class LaunchedWorkflowApi {
    public List<Map<String, Object>> search(LdapSearchRequest req) {
        return Collections.singletonList(
                Map.of("id", "lwf-004", "status", "COMPLETED"));
    }
    public boolean ping() { return true; }
}

@ManagedComponent
class ObjectConfigsApi {
    public List<Map<String, Object>> search(LdapSearchRequest req) {
        return Collections.singletonList(
                Map.of("id", "oc-005", "name", "DemoConfig"));
    }
    public boolean ping() { return true; }
}

@ManagedComponent
class ObjectConfigApi {
    public List<Map<String, Object>> search(LdapSearchRequest req) {
        return Collections.singletonList(
                Map.of("id", "oc-006", "name", "SingleConfig"));
    }
    public boolean ping() { return true; }
}

@ManagedComponent
class PolicyViolationsApi {
    public List<Map<String, Object>> search(LdapSearchRequest req) {
        return Collections.singletonList(
                Map.of("id", "pv-007", "policyName", "DemoPolicy"));
    }
    public boolean ping() { return true; }
}