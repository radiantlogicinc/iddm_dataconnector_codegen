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
import com.radiantlogic.iddm.response.ResponseStatus;

import java.util.List;
import java.util.Collections;

/**
 * Unified data connector for the Harry Potter OpenAPI. It currently supports the "books" object.
 */
@CustomConnector(metaJsonFile = "harry_potter_openapiConnector.json")
public class HarryPotterOpenapiDataConnector implements
        SearchOperations<LdapSearchRequest>,
        TestConnectionOperations<TestConnectionRequest> {

    private static final Logger logger = Logger.getLogger(HarryPotterOpenapiDataConnector.class);
    private final BooksApi booksApi;

    /**
     * Constructor used by the IDDM runtime. The {@link BooksApi} implementation is a managed component
     * and will be injected automatically.
     */
    public HarryPotterOpenapiDataConnector(BooksApi booksApi) {
        this.booksApi = booksApi;
    }

    /**
     * Handles LDAP search requests. The request may contain the following optional parameters:
     *   - index: start index for pagination (default 0)
     *   - max:   maximum number of records to return (default 10)
     *   - search: free‑text filter applied by the backend (optional)
     */
    @Override
    public LdapResponse search(LdapSearchRequest request) {
        try {
            int index = request.getParameterAsInt("index", 0);
            int max = request.getParameterAsInt("max", 10);
            String search = request.getParameter("search");

            List<Book> books = booksApi.getBooks(index, max, search);
            return new LdapResponse(ResponseStatus.OK, books);
        } catch (Exception e) {
            logger.error("Search operation failed", e);
            return new LdapResponse(ResponseStatus.ERROR, Collections.emptyList());
        }
    }

    /**
     * Tests connectivity to the underlying OpenAPI by performing a lightweight call.
     */
    @Override
    public TestConnectionResponse testConnection(TestConnectionRequest request) {
        try {
            // A simple call that should succeed if the service is reachable.
            booksApi.getBooks(0, 1, null);
            return new TestConnectionResponse(ResponseStatus.OK, "Connection successful");
        } catch (Exception e) {
            logger.error("Test connection failed", e);
            return new TestConnectionResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }
}

/**
 * Simple POJO representing a book returned by the Harry Potter OpenAPI.
 */
class Book {
    private String id;
    private String title;
    private String author;

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}

/**
 * API client interface for the "books" object.
 */
interface BooksApi {
    /**
     * Retrieves a list of books.
     * @param index  zero‑based start index for pagination
     * @param max    maximum number of records to return
     * @param search optional free‑text filter; may be null
     * @return list of {@link Book}
     */
    List<Book> getBooks(int index, int max, String search);

    /**
     * Retrieves a random book.
     */
    Book getRandomBook();
}

/**
 * Managed component that would normally call the external HTTP service. For the purpose of this
 * connector skeleton it returns empty data structures.
 */
@ManagedComponent
class BooksApiImpl implements BooksApi {
    @Override
    public List<Book> getBooks(int index, int max, String search) {
        // In a real implementation an HTTP client would be used here.
        return Collections.emptyList();
    }

    @Override
    public Book getRandomBook() {
        return null;
    }
}