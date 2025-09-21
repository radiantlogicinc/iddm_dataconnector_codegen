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
import com.radiantlogic.iddm.base.ResponseEntity;
import com.radiantlogic.iddm.base.ResponseStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Unified connector for the Harry Potter OpenAPI.
 * Handles both "books" and "characters" objects.
 */
@CustomConnector(metaJsonFile = "harry_potter_openapiConnector.json")
public class HarryPotterOpenapiDataConnector implements SearchOperations, TestConnectionOperations {

    private static final Logger LOG = Logger.getLogger(HarryPotterOpenapiDataConnector.class);

    private final BooksApi booksApi;
    private final CharactersApi charactersApi;

    public HarryPotterOpenapiDataConnector(
            @ManagedComponent BooksApi booksApi,
            @ManagedComponent CharactersApi charactersApi) {
        this.booksApi = booksApi;
        this.charactersApi = charactersApi;
    }

    /**
     * Handles LDAP search requests. The filter is inspected to decide which
     * backend API (books or characters) should be called.
     *
     * Expected simple filter patterns:
     *   (objectClass=books)      – search books
     *   (objectClass=characters) – search characters
     *
     * The request is assumed to expose a map of parameters (index, max, page, search)
     * via {@code getAttributes()} – this is a simplification for the example.
     */
    @Override
    public ResponseEntity<LdapResponse> search(LdapSearchRequest request) {
        String filter = request.getFilter() != null ? request.getFilter().toLowerCase() : "";
        Map<String, String> params = request.getAttributes(); // simplified accessor

        List<Map<String, Object>> results;

        if (filter.contains("objectclass=books")) {
            results = booksApi.searchBooks(params);
        } else if (filter.contains("objectclass=characters")) {
            results = charactersApi.searchCharacters(params);
        } else {
            LOG.warn("Search filter does not match any known object type: " + filter);
            results = Collections.emptyList();
        }

        LdapResponse ldapResponse = new LdapResponse(ResponseStatus.OK, results);
        return new ResponseEntity<>(ResponseStatus.OK, ldapResponse);
    }

    /**
     * Tests connectivity to all configured backend endpoints.
     * A lightweight call (fetching a single record) is performed on each API.
     */
    @Override
    public ResponseEntity<TestConnectionResponse> testConnection(TestConnectionRequest request) {
        try {
            // Attempt to fetch a single book and a single character
            booksApi.searchBooks(Collections.singletonMap("max", "1"));
            charactersApi.searchCharacters(Collections.singletonMap("max", "1"));
        } catch (Exception e) {
            LOG.error("Test connection failed: " + e.getMessage(), e);
            TestConnectionResponse failure = new TestConnectionResponse(ResponseStatus.ERROR, e.getMessage());
            return new ResponseEntity<>(ResponseStatus.ERROR, failure);
        }

        TestConnectionResponse success = new TestConnectionResponse(ResponseStatus.OK, "All endpoints reachable");
        return new ResponseEntity<>(ResponseStatus.OK, success);
    }
}

/* -------------------------------------------------------------------------- */
/* Managed API client components – simple stubs for illustration purposes.   */
/* In a real implementation these would perform HTTP calls to the OpenAPI.   */
/* -------------------------------------------------------------------------- */

@ManagedComponent
class BooksApi {

    /**
     * Searches books using the supplied parameters.
     *
     * @param params map containing possible keys: index, max, page, search
     * @return list of book objects represented as maps
     */
    public List<Map<String, Object>> searchBooks(Map<String, String> params) {
        // Placeholder implementation – replace with real HTTP call.
        return Collections.emptyList();
    }

    public Map<String, Object> getRandomBook() {
        // Placeholder implementation.
        return Collections.emptyMap();
    }
}

@ManagedComponent
class CharactersApi {

    public List<Map<String, Object>> searchCharacters(Map<String, String> params) {
        // Placeholder implementation – replace with real HTTP call.
        return Collections.emptyList();
    }

    public Map<String, Object> getRandomCharacter() {
        // Placeholder implementation.
        return Collections.emptyMap();
    }
}