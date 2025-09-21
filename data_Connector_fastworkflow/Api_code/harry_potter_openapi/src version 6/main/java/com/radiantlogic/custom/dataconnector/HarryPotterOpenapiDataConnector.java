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

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Unified connector for the Harry Potter OpenAPI.
 * Handles books, characters and spells in a single class.
 */
@CustomConnector(metaJsonFile = "harry_potter_openapiConnector.json")
public class HarryPotterOpenapiDataConnector implements
        SearchOperations<LdapSearchRequest>,
        TestConnectionOperations<TestConnectionRequest> {

    private static final Logger LOG = Logger.getLogger(HarryPotterOpenapiDataConnector.class);

    private final BooksApi booksApi;
    private final CharactersApi charactersApi;
    private final SpellsApi spellsApi;

    /**
     * Constructor with injected configuration and API clients.
     *
     * @param connectionProps injected connection properties (currently unused)
     * @param booksApi        client for book related endpoints
     * @param charactersApi   client for character related endpoints
     * @param spellsApi       client for spell related endpoints
     */
    public HarryPotterOpenapiDataConnector(
            @Property(name = "CONNECTION_CONFIGURATION") ReadOnlyProperties connectionProps,
            BooksApi booksApi,
            CharactersApi charactersApi,
            SpellsApi spellsApi) {
        this.booksApi = booksApi;
        this.charactersApi = charactersApi;
        this.spellsApi = spellsApi;
    }

    /**
     * Routes the LDAP search request to the appropriate API client based on the base DN.
     *
     * @param request LDAP search request
     * @return LDAP response containing the list of entries
     */
    @Override
    public LdapResponse search(LdapSearchRequest request) {
        String baseDn = request.getBaseDn(); // SDK provides this accessor
        List<Map<String, Object>> results;

        if (baseDn != null && baseDn.toLowerCase().contains("books")) {
            LOG.debug("Routing search to BooksApi");
            results = booksApi.getAllBooks(request);
        } else if (baseDn != null && baseDn.toLowerCase().contains("characters")) {
            LOG.debug("Routing search to CharactersApi");
            results = charactersApi.getAllCharacters(request);
        } else if (baseDn != null && baseDn.toLowerCase().contains("spells")) {
            LOG.debug("Routing search to SpellsApi");
            results = spellsApi.getAllSpells(request);
        } else {
            LOG.warn("Unsupported base DN for search: " + baseDn);
            results = Collections.emptyList();
        }

        return new LdapResponse(LdapResponse.Status.OK, results);
    }

    /**
     * Tests connectivity to all three API endpoints.
     *
     * @param request test‑connection request (not used)
     * @return test‑connection response indicating overall health
     */
    @Override
    public TestConnectionResponse testConnection(TestConnectionRequest request) {
        boolean booksOk = booksApi.ping();
        boolean charactersOk = charactersApi.ping();
        boolean spellsOk = spellsApi.ping();

        boolean overall = booksOk && charactersOk && spellsOk;
        LOG.info("Test connection results – books: " + booksOk +
                ", characters: " + charactersOk + ", spells: " + spellsOk);

        return new TestConnectionResponse(
                overall ? TestConnectionResponse.Status.OK : TestConnectionResponse.Status.ERROR);
    }
}

/* -------------------------------------------------------------------------- */
/* -------------------------- API CLIENTS ----------------------------------- */
/* -------------------------------------------------------------------------- */

@ManagedComponent
class BooksApi {

    /**
     * Retrieves all books. In a real implementation this would call the external REST API.
     *
     * @param request the original LDAP search request (may contain paging/filter info)
     * @return list of book entries represented as maps
     */
    public List<Map<String, Object>> getAllBooks(LdapSearchRequest request) {
        // Placeholder implementation – return empty list
        return Collections.emptyList();
    }

    /** Simple health‑check for the books endpoint. */
    public boolean ping() {
        return true;
    }
}

@ManagedComponent
class CharactersApi {

    public List<Map<String, Object>> getAllCharacters(LdapSearchRequest request) {
        return Collections.emptyList();
    }

    public boolean ping() {
        return true;
    }
}

@ManagedComponent
class SpellsApi {

    public List<Map<String, Object>> getAllSpells(LdapSearchRequest request) {
        return Collections.emptyList();
    }

    public boolean ping() {
        return true;
    }
}