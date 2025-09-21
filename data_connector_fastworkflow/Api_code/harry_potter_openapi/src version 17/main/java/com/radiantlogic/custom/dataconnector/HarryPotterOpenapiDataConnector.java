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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@CustomConnector(metaJsonFile = "harry_potter_openapiConnector.json")
public class HarryPotterOpenapiDataConnector implements SearchOperations<LdapSearchRequest>, TestConnectionOperations {

    private final Logger logger = Logger.getLogger(HarryPotterOpenapiDataConnector.class);

    @Property(name = "api_base_url")
    private String apiBaseUrl;

    @ManagedComponent
    private final RestTemplate restTemplate;

    public HarryPotterOpenapiDataConnector(RestTemplate restTemplate, @InjectableProperties ReadOnlyProperties properties) {
        this.restTemplate = restTemplate;
        this.apiBaseUrl = properties.get("api_base_url");
    }

    @Override
    public ResponseEntity<LdapResponse> search(LdapSearchRequest searchRequest) {
        String objectType = searchRequest.getBaseDn(); // Assuming baseDn contains the object type (e.g., "books", "characters")
        String searchTerm = searchRequest.getFilter(); // Assuming filter contains the search term

        if (objectType == null || objectType.isEmpty()) {
            return new ResponseEntity<>(LdapResponse.builder().status(400).message("Object type not specified in baseDn.").build(), org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        try {
            List<Map<String, Object>> results = new ArrayList<>();
            String endpoint = "";

            if ("books".equalsIgnoreCase(objectType)) {
                endpoint = "/books";
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + endpoint);
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    builder.queryParam("search", searchTerm);
                }
                ResponseEntity<Book[]> response = restTemplate.getForEntity(builder.toUriString(), Book[].class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    for (Book book : response.getBody()) {
                        Map<String, Object> bookMap = new HashMap<>();
                        bookMap.put("title", book.title);
                        bookMap.put("author", book.author);
                        results.add(bookMap);
                    }
                }
            } else if ("characters".equalsIgnoreCase(objectType)) {
                endpoint = "/characters";
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + endpoint);
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    builder.queryParam("search", searchTerm);
                }
                ResponseEntity<Character[]> response = restTemplate.getForEntity(builder.toUriString(), Character[].class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    for (Character character : response.getBody()) {
                        Map<String, Object> characterMap = new HashMap<>();
                        characterMap.put("name", character.name);
                        characterMap.put("house", character.house);
                        results.add(characterMap);
                    }
                }
            } else {
                return new ResponseEntity<>(LdapResponse.builder().status(400).message("Unsupported object type: " + objectType).build(), org.springframework.http.HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(LdapResponse.builder().status(200).data(results).build(), org.springframework.http.HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error during search: " + e.getMessage(), e);
            return new ResponseEntity<>(LdapResponse.builder().status(500).message("Error during search: " + e.getMessage()).build(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<TestConnectionResponse> testConnection(TestConnectionRequest request) {
        try {
            // Test connection to /books endpoint
            ResponseEntity<Book[]> booksResponse = restTemplate.getForEntity(apiBaseUrl + "/books", Book[].class);
            if (!booksResponse.getStatusCode().is2xxSuccessful()) {
                return new ResponseEntity<>(TestConnectionResponse.builder().status(500).message("Failed to connect to /books endpoint. Status: " + booksResponse.getStatusCodeValue()).build(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Test connection to /characters endpoint
            ResponseEntity<Character[]> charactersResponse = restTemplate.getForEntity(apiBaseUrl + "/characters", Character[].class);
            if (!charactersResponse.getStatusCode().is2xxSuccessful()) {
                return new ResponseEntity<>(TestConnectionResponse.builder().status(500).message("Failed to connect to /characters endpoint. Status: " + charactersResponse.getStatusCodeValue()).build(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(TestConnectionResponse.builder().status(200).message("Connection successful").build(), org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error testing connection: " + e.getMessage(), e);
            return new ResponseEntity<>(TestConnectionResponse.builder().status(500).message("Error testing connection: " + e.getMessage()).build(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Inner classes for API responses
    private static class Book {
        public String title;
        public String author;
    }

    private static class Character {
        public String name;
        public String house;
    }
}