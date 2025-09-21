package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.base.annotation.CustomConnector;
import com.radiantlogic.iddm.base.annotation.Properties;
import com.radiantlogic.iddm.base.component.ManagedComponent;
import com.radiantlogic.iddm.base.logging.Logger;
import com.radiantlogic.iddm.base.ReadOnlyProperties;
import com.radiantlogic.iddm.operation.SearchOperations;
import com.radiantlogic.iddm.operation.TestConnectionOperations;
import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.request.TestConnectionRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import com.radiantlogic.iddm.base.ResponseEntity;
import com.radiantlogic.iddm.base.ResponseStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Unified connector for the Harry Potter OpenAPI.  Currently only the
 * {@code characters} object is required, but the design allows additional
 * objects (e.g., spells, houses, books) to be added later.
 */
@CustomConnector(metaJsonFile = "harry_potter_openapiConnector.json")
public class HarryPotterOpenapiDataConnector implements SearchOperations, TestConnectionOperations {

    private static final Logger LOG = Logger.getLogger(HarryPotterOpenapiDataConnector.class);

    private final ReadOnlyProperties connectionProps;
    private final CharactersApi charactersApi;

    /**
     * Constructor injected by the IDDM runtime.
     *
     * @param connectionProps connection configuration (hostname, apiKey, …)
     * @param charactersApi   client that talks to the /characters endpoint
     */
    public HarryPotterOpenapiDataConnector(
            @Properties(name = "CONNECTION_CONFIGURATION") ReadOnlyProperties connectionProps,
            CharactersApi charactersApi) {
        this.connectionProps = connectionProps;
        this.charactersApi = charactersApi;
        LOG.info("HarryPotterOpenapiDataConnector initialized with host: " + connectionProps.get("hostname"));
    }

    /* -------------------------------------------------------------
     *  SearchOperations implementation
     * ------------------------------------------------------------- */
    @Override
    public ResponseEntity<LdapResponse> search(LdapSearchRequest request) {
        LOG.debug("Received LDAP search request: " + request);

        // Extract pagination / filter parameters from the LDAP request.
        // The SDK does not prescribe a concrete API, so we assume a simple map.
        Map<String, String> params = request.getParameters();

        int index = parseIntOrDefault(params.get("index"), 0);
        int max = parseIntOrDefault(params.get("max"), 50);
        int page = parseIntOrDefault(params.get("page"), 1);
        String search = params.getOrDefault("search", "");

        // For the unified connector we only have the "characters" source object.
        List<Map<String, Object>> rawResults = charactersApi.getCharacters(index, max, page, search);

        // Build the LDAP‑style response.
        LdapResponse ldapResponse = new LdapResponse();
        ldapResponse.setEntries(rawResults);
        ldapResponse.setTotalCount(rawResults.size());

        LOG.info("Search completed – returning " + rawResults.size() + " entries.");
        return new ResponseEntity<>(ResponseStatus.OK, ldapResponse);
    }

    /* -------------------------------------------------------------
     *  TestConnectionOperations implementation
     * ------------------------------------------------------------- */
    @Override
    public ResponseEntity<TestConnectionResponse> testConnection(TestConnectionRequest request) {
        LOG.debug("Running test‑connection request.");

        boolean reachable = charactersApi.ping();

        TestConnectionResponse resp = new TestConnectionResponse();
        resp.setSuccess(reachable);
        resp.setMessage(reachable ? "Connection successful" : "Unable to reach Harry Potter API");

        LOG.info("Test‑connection result: " + resp.getMessage());
        return new ResponseEntity<>(ResponseStatus.OK, resp);
    }

    /* -------------------------------------------------------------
     *  Helper utilities
     * ------------------------------------------------------------- */
    private int parseIntOrDefault(String value, int defaultVal) {
        try {
            return (value != null) ? Integer.parseInt(value) : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}

/* -------------------------------------------------------------
 *  Managed component that abstracts the HTTP calls to the
 *  Harry Potter OpenAPI.  In production this would use a real
 *  HTTP client; for unit testing it is mocked.
 * ------------------------------------------------------------- */
@ManagedComponent
class CharactersApi {

    private final ReadOnlyProperties connectionProps;

    public CharactersApi(@Properties(name = "CONNECTION_CONFIGURATION") ReadOnlyProperties connectionProps) {
        this.connectionProps = connectionProps;
    }

    /**
     * Retrieves characters from the remote API.
     *
     * @param index  start index (ignored by the mock implementation)
     * @param max    maximum number of records to return
     * @param page   page number (ignored by the mock implementation)
     * @param search optional free‑text filter
     * @return list of character objects represented as maps
     */
    public List<Map<String, Object>> getCharacters(int index, int max, int page, String search) {
        // Placeholder implementation – real code would perform an HTTP GET.
        // The method is deliberately simple because unit tests replace it with a mock.
        return Collections.emptyList();
    }

    /**
     * Simple health‑check call.
     *
     * @return true if the remote service is reachable
     */
    public boolean ping() {
        // Placeholder – real implementation would perform a lightweight request.
        return true;
    }
}