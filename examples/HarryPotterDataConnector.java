package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.annotations.CustomConnector;
import com.radiantlogic.iddm.annotations.ManagedComponent;
import com.radiantlogic.iddm.annotations.Property;
import com.radiantlogic.iddm.base.Logger;
import com.radiantlogic.iddm.base.SearchOperations;
import com.radiantlogic.iddm.base.TestConnectionOperations;
import com.radiantlogic.iddm.ldap.LdapSearchRequest;
import com.radiantlogic.iddm.base.TestConnectionRequest;
import com.radiantlogic.iddm.ldap.LdapResponse;
import com.radiantlogic.iddm.base.TestConnectionResponse;
import com.radiantlogic.iddm.base.ReadOnlyProperties;
import com.radiantlogic.iddm.base.InjectableProperties;
import com.radiantlogic.iddm.ldap.LdapResultCode;
import com.radiantlogic.openapi.generated.harrypotterapi.api.BooksApi;
import com.radiantlogic.openapi.generated.harrypotterapi.api.CharactersApi;
import com.radiantlogic.openapi.generated.harrypotterapi.api.HousesApi;
import com.radiantlogic.openapi.generated.harrypotterapi.api.SpellsApi;
import com.radiantlogic.openapi.generated.harrypotterapi.model.Book;
import com.radiantlogic.openapi.generated.harrypotterapi.model.Character;
import com.radiantlogic.openapi.generated.harrypotterapi.model.House;
import com.radiantlogic.openapi.generated.harrypotterapi.model.Spell;
import org.springframework.web.client.RestClientException;
import java.util.List;
import java.util.stream.Collectors;

@CustomConnector(metaJsonFile = "harryPotterConnector.json")
@ManagedComponent
public class HarryPotterDataConnector implements SearchOperations<LdapSearchRequest, LdapResponse<String>>, TestConnectionOperations<TestConnectionRequest, TestConnectionResponse> {

    private final Logger log;
    private final CharactersApi charactersApi;
    private final SpellsApi spellsApi;
    private final HousesApi housesApi;
    private final BooksApi booksApi;
    private final String baseUrl;

    public HarryPotterDataConnector(
            Logger log,
            @Property(name = InjectableProperties.CUSTOM_DATASOURCE_PROPERTIES) ReadOnlyProperties connectionProperties
    ) {
        this.log = log;
        String urlFromProps = (String) connectionProperties.get("baseUrl");
        this.baseUrl = (urlFromProps != null && !urlFromProps.trim().isEmpty()) ? urlFromProps : "https://potterapi-fedeperin.vercel.app/en";
        
        // Initialize API clients - they already have the correct default basePath
        this.charactersApi = new CharactersApi();
        this.spellsApi = new SpellsApi();
        this.housesApi = new HousesApi();
        this.booksApi = new BooksApi();
        
        log.info("HarryPotterDataConnector initialized with base URL: " + this.baseUrl);
    }

    @Override
    public LdapResponse<String> search(LdapSearchRequest searchRequest) {
        try {
            String searchTerm = searchRequest.getFilter().toString();
            log.info("Searching with term: " + searchTerm);
            
            StringBuilder results = new StringBuilder();
            
            // Search across all object types
            if (searchTerm.toLowerCase().contains("book") || searchTerm.toLowerCase().contains("volume")) {
                List<Book> books = booksApi.booksGet(null, null, null, searchTerm);
                String bookResults = books.stream().map(Book::toString).collect(Collectors.joining("\n"));
                results.append("BOOKS:\n").append(bookResults).append("\n\n");
            }
            
            if (searchTerm.toLowerCase().contains("character") || searchTerm.toLowerCase().contains("person")) {
                List<Character> characters = charactersApi.charactersGet(null, null, null, searchTerm);
                String charResults = characters.stream().map(charObj -> charObj.toString()).collect(Collectors.joining("\n"));
                results.append("CHARACTERS:\n").append(charResults).append("\n\n");
            }
            
            if (searchTerm.toLowerCase().contains("house") || searchTerm.toLowerCase().contains("gryffindor") || 
                searchTerm.toLowerCase().contains("slytherin") || searchTerm.toLowerCase().contains("hufflepuff") || 
                searchTerm.toLowerCase().contains("ravenclaw")) {
                List<House> houses = housesApi.housesGet(null, null, null, searchTerm);
                String houseResults = houses.stream().map(House::toString).collect(Collectors.joining("\n"));
                results.append("HOUSES:\n").append(houseResults).append("\n\n");
            }
            
            if (searchTerm.toLowerCase().contains("spell") || searchTerm.toLowerCase().contains("charm") || 
                searchTerm.toLowerCase().contains("jinx") || searchTerm.toLowerCase().contains("hex")) {
                List<Spell> spells = spellsApi.spellsGet(null, null, null, searchTerm);
                String spellResults = spells.stream().map(Spell::toString).collect(Collectors.joining("\n"));
                results.append("SPELLS:\n").append(spellResults).append("\n\n");
            }
            
            if (results.length() == 0) {
                results.append("No results found for: ").append(searchTerm);
            }
            
            return new LdapResponse<>(LdapResultCode.SUCCESS, results.toString());
            
        } catch (RestClientException e) {
            log.error("Error during search: " + e.getMessage());
            return new LdapResponse<>(LdapResultCode.OTHER, "Error: " + e.getMessage());
        }
    }

    @Override
    public TestConnectionResponse testConnection(TestConnectionRequest testConnectionRequest) {
        try {
            log.info("Testing connection to Harry Potter API at: " + this.baseUrl);
            
            // First, test if we can reach the API by making a simple call
            try {
                List<Book> books = booksApi.booksGet(1, 1, null, null);
                
                // An empty list is still a valid response - it means the API is working
                if (books != null) {
                    log.info("Connection test successful - Retrieved " + books.size() + " books");
                    return TestConnectionResponse.from("HarryPotterAPI", true, "Connection successful - API responding with " + books.size() + " books");
                } else {
                    log.warn("Connection test returned null response");
                    return TestConnectionResponse.from("HarryPotterAPI", false, "Connection test returned null response");
                }
            } catch (Exception apiException) {
                log.error("API call failed: " + apiException.getMessage(), apiException);
                
                // Try a different endpoint as fallback
                try {
                    List<Character> characters = charactersApi.charactersGet(1, 1, null, null);
                    if (characters != null) {
                        log.info("Fallback connection test successful - Retrieved " + characters.size() + " characters");
                        return TestConnectionResponse.from("HarryPotterAPI", true, "Connection successful via fallback endpoint");
                    }
                } catch (Exception fallbackException) {
                    log.error("Fallback API call also failed: " + fallbackException.getMessage(), fallbackException);
                }
                
                return TestConnectionResponse.from("HarryPotterAPI", false, "API connection failed: " + apiException.getMessage());
            }
        } catch (Exception e) {
            log.error("Connection test failed with unexpected error: " + e.getMessage(), e);
            return TestConnectionResponse.from("HarryPotterAPI", false, "Connection test failed: " + e.getMessage());
        }
    }
} 