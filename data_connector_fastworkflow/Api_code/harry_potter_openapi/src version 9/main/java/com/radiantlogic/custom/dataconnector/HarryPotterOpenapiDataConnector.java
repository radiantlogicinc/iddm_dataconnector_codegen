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

/**
 * Unified connector for the Harry Potter OpenAPI.
 * Handles both {@code books} and {@code spells} objects.
 */
@CustomConnector(metaJsonFile = "harry_potter_openapiConnector.json")
public class HarryPotterOpenapiDataConnector implements SearchOperations, TestConnectionOperations {

    private static final Logger LOG = Logger.getLogger(HarryPotterOpenapiDataConnector.class);

    private final BooksApi booksApi;
    private final SpellsApi spellsApi;

    /**
     * Constructor with injected configuration and API clients.
     *
     * @param connectionProps injected connection configuration (not used directly here but required by SDK)
     * @param booksApi        managed component for books endpoint
     * @param spellsApi       managed component for spells endpoint
     */
    public HarryPotterOpenapiDataConnector(
            @Property(name = "CONNECTION_CONFIGURATION") ReadOnlyProperties connectionProps,
            @ManagedComponent BooksApi booksApi,
            @ManagedComponent SpellsApi spellsApi) {
        this.booksApi = booksApi;
        this.spellsApi = spellsApi;
        LOG.info("HarryPotterOpenapiDataConnector instantiated");
    }

    /* -------------------------------------------------
     *  SearchOperations implementation
     * ------------------------------------------------- */
    @Override
    public LdapResponse search(LdapSearchRequest request) {
        LOG.debug("Received search request: baseDn={}, filter={}", request.getBaseDn(), request.getFilter());

        // Determine which object type the request targets.
        // The OpenAPI connector uses the base DN to indicate the object:
        //   "ou=books,..."  -> books
        //   "ou=spells,..." -> spells
        String baseDn = request.getBaseDn().toLowerCase();

        List<Object> results;
        if (baseDn.contains("books")) {
            results = booksApi.getAllBooks(request.getParameters());
        } else if (baseDn.contains("spells")) {
            results = spellsApi.getAllSpells(request.getParameters());
        } else {
            LOG.warn("Unsupported base DN in search request: {}", baseDn);
            results = Collections.emptyList();
        }

        return new LdapResponse("OK", results);
    }

    /* -------------------------------------------------
     *  TestConnectionOperations implementation
     * ------------------------------------------------- */
    @Override
    public TestConnectionResponse testConnection(TestConnectionRequest request) {
        LOG.info("Testing connectivity to Harry Potter OpenAPI endpoints");

        boolean booksOk = booksApi.ping();
        boolean spellsOk = spellsApi.ping();

        String status = (booksOk && spellsOk) ? "OK" : "FAILED";
        String message = (booksOk && spellsOk)
                ? "All endpoints reachable"
                : "One or more endpoints unreachable";

        LOG.info("Test connection result: {}", status);
        return new TestConnectionResponse(status, message);
    }
}

/* -------------------------------------------------
 *  Managed API client components (stubs – real
 *  implementations would call the external REST API)
 * ------------------------------------------------- */
@ManagedComponent
class BooksApi {

    /**
     * Returns a list of all books. The {@code parameters} map contains
     * pagination, search, etc., mirroring the OpenAPI query parameters.
     */
    public List<Object> getAllBooks(InjectableProperties parameters) {
        // Real implementation would invoke the external REST endpoint.
        // Here we just return an empty list – unit tests will mock this method.
        return Collections.emptyList();
    }

    /** Simple health‑check used by testConnection. */
    public boolean ping() {
        // Real implementation would perform a lightweight request.
        return true;
    }
}

@ManagedComponent
class SpellsApi {

    public List<Object> getAllSpells(InjectableProperties parameters) {
        return Collections.emptyList();
    }

    public boolean ping() {
        return true;
    }
}