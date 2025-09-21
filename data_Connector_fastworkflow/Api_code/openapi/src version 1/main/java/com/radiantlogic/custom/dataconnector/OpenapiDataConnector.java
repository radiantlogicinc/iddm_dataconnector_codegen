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
import com.radiantlogic.iddm.response.ResponseStatus;
import java.util.List;

/**
 * Unified OpenAPI data connector handling all authentication related objects.
 */
@CustomConnector(metaJsonFile = "openapiConnector.json")
public class OpenapiDataConnector implements SearchOperations<LdapSearchRequest>, TestConnectionOperations<TestConnectionRequest> {

    private static final Logger logger = Logger.getLogger(OpenapiDataConnector.class);

    // API client stubs – one per selected object
    private final LoginApi loginApi;
    private final InjectClientCertApi injectClientCertApi;
    private final AuthenticateApi authenticateApi;
    private final AuthnIamApi authnIamApi;
    private final AuthnAzureApi authnAzureApi;
    private final AuthnK8sApi authnK8sApi;
    private final AuthnLdapApi authnLdapApi;
    private final AuthnJwtApi authnJwtApi;
    private final PasswordApi passwordApi;
    private final ApiKeyApi apiKeyApi;

    public OpenapiDataConnector(
            @Property(name = "loginApi") LoginApi loginApi,
            @Property(name = "injectClientCertApi") InjectClientCertApi injectClientCertApi,
            @Property(name = "authenticateApi") AuthenticateApi authenticateApi,
            @Property(name = "authnIamApi") AuthnIamApi authnIamApi,
            @Property(name = "authnAzureApi") AuthnAzureApi authnAzureApi,
            @Property(name = "authnK8sApi") AuthnK8sApi authnK8sApi,
            @Property(name = "authnLdapApi") AuthnLdapApi authnLdapApi,
            @Property(name = "authnJwtApi") AuthnJwtApi authnJwtApi,
            @Property(name = "passwordApi") PasswordApi passwordApi,
            @Property(name = "apiKeyApi") ApiKeyApi apiKeyApi) {
        this.loginApi = loginApi;
        this.injectClientCertApi = injectClientCertApi;
        this.authenticateApi = authenticateApi;
        this.authnIamApi = authnIamApi;
        this.authnAzureApi = authnAzureApi;
        this.authnK8sApi = authnK8sApi;
        this.authnLdapApi = authnLdapApi;
        this.authnJwtApi = authnJwtApi;
        this.passwordApi = passwordApi;
        this.apiKeyApi = apiKeyApi;
    }

    /**
     * Simple search implementation – logs the LDAP filter and returns an empty result set.
     */
    @Override
    public LdapResponse search(LdapSearchRequest request) {
        String filter = request.getFilter(); // SDK provides getFilter()
        logger.info("Search invoked with filter: " + filter);
        // In a real implementation we would map the filter to one of the ten objects.
        return new LdapResponse(ResponseStatus.OK, List.of());
    }

    /**
     * Test connectivity by invoking a lightweight `ping` on each API client.
     */
    @Override
    public TestConnectionResponse testConnection(TestConnectionRequest request) {
        try {
            loginApi.ping();
            injectClientCertApi.ping();
            authenticateApi.ping();
            authnIamApi.ping();
            authnAzureApi.ping();
            authnK8sApi.ping();
            authnLdapApi.pping();
            authnJwtApi.ping();
            passwordApi.ping();
            apiKeyApi.ping();
            return new TestConnectionResponse(ResponseStatus.OK, "All endpoints reachable");
        } catch (Exception e) {
            logger.error("Test connection failed", e);
            return new TestConnectionResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }
}

/**
 * Minimal API client definitions – each is a @ManagedComponent so the SDK can inject them.
 */
@ManagedComponent
interface LoginApi { void ping(); }

@ManagedComponent
interface InjectClientCertApi { void ping(); }

@ManagedComponent
interface AuthenticateApi { void ping(); }

@ManagedComponent
interface AuthnIamApi { void ping(); }

@ManagedComponent
interface AuthnAzureApi { void ping(); }

@ManagedComponent
interface AuthnK8sApi { void ping(); }

@ManagedComponent
interface AuthnLdapApi { void ping(); }

@ManagedComponent
interface AuthnJwtApi { void ping(); }

@ManagedComponent
interface PasswordApi { void ping(); }

@ManagedComponent
interface ApiKeyApi { void ping(); }