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
 * Unified data connector for the Harry Potter OpenAPI.
 * Handles books, characters, houses and spells in a single class.
 */
@CustomConnector(metaJsonFile = "harry_potter_openapiConnector.json")
public class HarryPotterOpenapiDataConnector implements
        SearchOperations<LdapSearchRequest>,
        TestConnectionOperations<TestConnectionRequest> {

    private static final Logger logger = Logger.getLogger(HarryPotterOpenapiDataConnector.class);

    private final BooksApi booksApi;
    private final CharactersApi charactersApi;
    private final HousesApi housesApi;
    private final SpellsApi spellsApi;

    /**
     * Constructor used by the IDDM runtime.
     *
     * @param connectionProps injected configuration (currently unused but kept for future extensions)
     * @param booksApi        client for book‑related endpoints
     * @param charactersApi   client for character‑related endpoints
     * @param housesApi       client for house‑related endpoints
     * @param spellsApi       client for spell‑related endpoints
     */
    public HarryPotterOpenapiDataConnector(
            @Property(name = "CONNECTION_CONFIGURATION") ReadOnlyProperties connectionProps,
            BooksApi booksApi,
            CharactersApi charactersApi,
            HousesApi housesApi,
            SpellsApi spellsApi) {
        this.booksApi = booksApi;
        this.charactersApi = charactersApi;
        this.housesApi = housesApi;
        this.spellsApi = spellsApi;
    }

    /**
     * Handles LDAP search requests. The base DN is inspected to decide which
     * underlying API client should be called.
     *
     * @param request LDAP search request coming from IDDM
     * @return LdapResponse containing the list of result entries
     */
    @Override
    public LdapResponse search(LdapSearchRequest request) {
        String baseDn = request.getBaseDn().toLowerCase();
        List<Map<String, Object>> results;

        if (baseDn.contains("books")) {
            results = booksApi.getAllBooks();
        } else if (baseDn.contains("characters")) {
            results = charactersApi.getAllCharacters();
        } else if (baseDn.contains("houses")) {
            results = housesApi.getAllHouses();
        } else if (baseDn.contains("spells")) {
            results = spellsApi.getAllSpells();
        } else {
            logger.warn("Search request received for unknown object type: {}", baseDn);
            results = Collections.emptyList();
        }

        return new LdapResponse(LdapResponse.Status.OK, results);
    }

    /**
     * Tests connectivity to all four OpenAPI endpoints.
     *
     * @param request test‑connection request (payload not used)
     * @return TestConnectionResponse indicating overall health
     */
    @Override
    public TestConnectionResponse testConnection(TestConnectionRequest request) {
        boolean booksOk = booksApi.ping();
        boolean charactersOk = charactersApi.ping();
        boolean housesOk = housesApi.ping();
        boolean spellsOk = spellsApi.ping();

        boolean allOk = booksOk && charactersOk && housesOk && spellsOk;
        TestConnectionResponse.Status status = allOk
                ? TestConnectionResponse.Status.OK
                : TestConnectionResponse.Status.ERROR;

        return new TestConnectionResponse(status);
    }
}

/* -------------------------------------------------------------------------- */
/* -------------------------- API CLIENTS ----------------------------------- */
/* -------------------------------------------------------------------------- */

@ManagedComponent
class BooksApi {
    List<Map<String, Object>> getAllBooks() {
        // Placeholder – real implementation would call the external REST endpoint
        return Collections.emptyList();
    }

    Map<String, Object> getRandomBook() {
        return Collections.emptyMap();
    }

    boolean ping() {
        return true; // simple health‑check stub
    }
}

@ManagedComponent
class CharactersApi {
    List<Map<String, Object>> getAllCharacters() {
        return Collections.emptyList();
    }

    Map<String, Object> getRandomCharacter() {
        return Collections.emptyMap();
    }

    boolean ping() {
        return true;
    }
}

@ManagedComponent
class HousesApi {
    List<Map<String, Object>> getAllHouses() {
        return Collections.emptyList();
    }

    Map<String, Object> getRandomHouse() {
        return Collections.emptyMap();
    }

    boolean ping() {
        return true;
    }
}

@ManagedComponent
class SpellsApi {
    List<Map<String, Object>> getAllSpells() {
        return Collections.emptyList();
    }

    Map<String, Object> getRandomSpell() {
        return Collections.emptyMap();
    }

    boolean ping() {
        return true;
    }
}