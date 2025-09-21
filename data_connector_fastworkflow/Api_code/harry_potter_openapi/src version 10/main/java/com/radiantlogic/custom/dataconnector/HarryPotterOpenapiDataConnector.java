/* -------------------------------------------------------------
 * HarryPotterOpenapiDataConnector.java
 * -------------------------------------------------------------
 * Unified connector for the Harry Potter OpenAPI (houses object).
 * Implements SearchOperations and TestConnectionOperations.
 * ------------------------------------------------------------- */

package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.base.annotation.CustomConnector;
import com.radiantlogic.iddm.base.annotation.Property;
import com.radiantlogic.iddm.base.component.ManagedComponent;
import com.radiantlogic.iddm.base.logging.Logger;
import com.radiantlogic.iddm.base.ReadOnlyProperties;
import com.radiantlogic.iddm.operation.SearchOperations;
import com.radiantlogic.iddm.operation.TestConnectionOperations;
import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.request.TestConnectionRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import com.radiantlogic.iddm.response.ResponseStatus;

import java.util.List;
import java.util.Map;

/**
 * Unified data connector for the Harry Potter OpenAPI.
 * Handles the {@code houses} object only (as required by the
 * current unified configuration).
 */
@CustomConnector(metaJsonFile = "harry_potter_openapiConnector.json")
public class HarryPotterOpenapiDataConnector implements SearchOperations, TestConnectionOperations {

    private static final Logger LOG = Logger.getLogger(HarryPotterOpenapiDataConnector.class);

    private final HousesApi housesApi;

    /**
     * Constructor with injected configuration and API client.
     *
     * @param connectionProps injected connection properties (hostname, etc.)
     * @param housesApi       managed component that talks to the remote API
     */
    public HarryPotterOpenapiDataConnector(
            @Property(name = "CONNECTION_CONFIGURATION") ReadOnlyProperties connectionProps,
            @ManagedComponent HousesApi housesApi) {
        this.housesApi = housesApi;
        LOG.info("HarryPotterOpenapiDataConnector initialized with host: " +
                connectionProps.get("hostname"));
    }

    /* ---------------------------------------------------------
     * Search operation – maps an LDAP search request to the
     * HousesApi#getHouses call.
     * --------------------------------------------------------- */
    @Override
    public LdapResponse search(LdapSearchRequest request) {
        LOG.debug("Received LDAP search request: " + request);

        // Extract pagination / search parameters (defaults provided)
        int index = request.getParameterAsInt("index", 0);
        int max = request.getParameterAsInt("max", 10);
        int page = request.getParameterAsInt("page", 1);
        String search = request.getParameterAsString("search", null);

        LOG.debug(String.format("Calling HousesApi.getHouses(index=%d, max=%d, page=%d, search=%s)",
                index, max, page, search));

        List<Map<String, Object>> houses = housesApi.getHouses(index, max, page, search);

        LOG.debug("HousesApi returned " + houses.size() + " entries");
        return new LdapResponse(ResponseStatus.OK, houses);
    }

    /* ---------------------------------------------------------
     * Test connection operation – verifies that the remote API
     * endpoint is reachable.
     * --------------------------------------------------------- */
    @Override
    public TestConnectionResponse testConnection(TestConnectionRequest request) {
        LOG.debug("Executing testConnection operation");

        boolean reachable = housesApi.ping();

        ResponseStatus status = reachable ? ResponseStatus.OK : ResponseStatus.ERROR;
        String message = reachable ? "Connection successful" : "Connection failed";

        LOG.info("Test connection result: " + message);
        return new TestConnectionResponse(status, message);
    }
}

/* -------------------------------------------------------------
 * HousesApi.java – Managed component that abstracts the remote
 * Harry Potter OpenAPI calls for the "houses" object.
 * ------------------------------------------------------------- */
@ManagedComponent
interface HousesApi {

    /**
     * Retrieves a list of Hogwarts houses.
     *
     * @param index  start index for pagination (ignored if not needed)
     * @param max    maximum number of records to return
     * @param page   page number (1‑based)
     * @param search optional free‑text filter; may be {@code null}
     * @return list of house representations (each entry is a map of attributes)
     */
    List<Map<String, Object>> getHouses(int index, int max, int page, String search);

    /**
     * Simple health‑check call – returns {@code true} when the API
     * endpoint is reachable.
     */
    boolean ping();
}