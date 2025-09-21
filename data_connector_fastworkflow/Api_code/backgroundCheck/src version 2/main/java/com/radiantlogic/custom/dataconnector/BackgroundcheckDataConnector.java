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

/**
 * Unified Data Connector for the Backgroundcheck objects:
 *   - order_confirmation
 *   - result
 *
 * The connector implements LDAP search (required by the SDK) and a test‑connection
 * operation that validates connectivity to both underlying REST APIs.
 */
@CustomConnector(metaJsonFile = "backgroundcheckConnector.json")
public class BackgroundcheckDataConnector implements
        SearchOperations<LdapSearchRequest>,
        TestConnectionOperations<TestConnectionRequest> {

    private final OrderConfirmationApi orderConfirmationApi;
    private final ResultApi resultApi;
    private final Logger logger;

    /**
     * Constructor injection.
     *
     * @param connectionProps injected configuration (e.g., hostname, credentials)
     * @param orderConfirmationApi client for the /order_confirmation endpoint
     * @param resultApi client for the /result endpoint
     * @param logger SDK logger
     */
    public BackgroundcheckDataConnector(
            @Property(name = "CONNECTION_CONFIGURATION") ReadOnlyProperties connectionProps,
            OrderConfirmationApi orderConfirmationApi,
            ResultApi resultApi,
            Logger logger) {
        this.orderConfirmationApi = orderConfirmationApi;
        this.resultApi = resultApi;
        this.logger = logger;
        this.logger.info("BackgroundcheckDataConnector initialized with host: {}",
                connectionProps.get("hostname"));
    }

    /**
     * LDAP search implementation.
     * The unified connector does not expose searchable attributes, so an empty
     * result set is returned. This satisfies the SDK contract while keeping the
     * connector focused on the write‑only POST operations.
     */
    @Override
    public LdapResponse search(LdapSearchRequest request) {
        logger.debug("Received LDAP search request: baseDn={}, filter={}",
                request.getBaseDn(), request.getFilter());
        // Return an empty result set – no searchable objects.
        return new LdapResponse();
    }

    /**
     * Test‑connection implementation.
     * Calls a lightweight “ping” on each API client. Returns success only if
     * both endpoints respond positively.
     */
    @Override
    public TestConnectionResponse testConnection(TestConnectionRequest request) {
        logger.debug("Executing testConnection for Backgroundcheck connector");
        boolean orderOk = orderConfirmationApi.ping();
        boolean resultOk = resultApi.ping();
        boolean overall = orderOk && resultOk;
        logger.info("Test connection result – orderConfirmation: {}, result: {}, overall: {}",
                orderOk, resultOk, overall);
        return new TestConnectionResponse(overall);
    }

    /* --------------------------------------------------------------------- */
    /*  Managed component API clients (simple placeholders for the example) */
    /* --------------------------------------------------------------------- */

    @ManagedComponent
    public static class OrderConfirmationApi {
        public boolean ping() {
            // In a real implementation this would perform a lightweight HTTP call.
            return true;
        }

        public void postOrderConfirmation(Object body) {
            // POST /order_confirmation implementation would go here.
        }
    }

    @ManagedComponent
    public static class ResultApi {
        public boolean ping() {
            // In a real implementation this would perform a lightweight HTTP call.
            return true;
        }

        public void postResult(Object body, String clientReferenceId) {
            // POST /result implementation would go here.
        }
    }
}