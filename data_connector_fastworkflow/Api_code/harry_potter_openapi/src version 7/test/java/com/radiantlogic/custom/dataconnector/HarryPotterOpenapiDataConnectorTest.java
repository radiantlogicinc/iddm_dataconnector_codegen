package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.request.TestConnectionRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HarryPotterOpenapiDataConnectorTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private com.radiantlogic.iddm.base.ReadOnlyProperties properties;

    @InjectMocks
    private HarryPotterOpenapiDataConnector dataConnector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(properties.get("api_base_url")).thenReturn("http://example.com/api");
    }

    @Test
    void testSearchBooksSuccess() {
        LdapSearchRequest request = new LdapSearchRequest();
        request.setBaseDn("books");
        request.setFilter("Harry");

        HarryPotterOpenapiDataConnector.Book[] books = new HarryPotterOpenapiDataConnector.Book[]{
                createBook("Harry Potter and the Sorcerer's Stone", "J.K. Rowling")
        };
        when(restTemplate.getForEntity("http://example.com/api/books?search=Harry", HarryPotterOpenapiDataConnector.Book[].class))
                .thenReturn(new ResponseEntity<>(books, HttpStatus.OK));

        ResponseEntity<LdapResponse> response = dataConnector.search(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        Map<String, Object> book = (Map<String, Object>) response.getBody().getData().get(0);
        assertEquals("Harry Potter and the Sorcerer's Stone", book.get("title"));
    }

    @Test
    void testSearchCharactersSuccess() {
        LdapSearchRequest request = new LdapSearchRequest();
        request.setBaseDn("characters");
        request.setFilter("Harry");

        HarryPotterOpenapiDataConnector.Character[] characters = new HarryPotterOpenapiDataConnector.Character[]{
                createCharacter("Harry Potter", "Gryffindor")
        };
        when(restTemplate.getForEntity("http://example.com/api/characters?search=Harry", HarryPotterOpenapiDataConnector.Character[].class))
                .thenReturn(new ResponseEntity<>(characters, HttpStatus.OK));

        ResponseEntity<LdapResponse> response = dataConnector.search(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        Map<String, Object> character = (Map<String, Object>) response.getBody().getData().get(0);
        assertEquals("Harry Potter", character.get("name"));
    }

    @Test
    void testSearchInvalidObjectType() {
        LdapSearchRequest request = new LdapSearchRequest();
        request.setBaseDn("invalid");

        ResponseEntity<LdapResponse> response = dataConnector.search(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unsupported object type: invalid", response.getBody().getMessage());
    }

    @Test
    void testTestConnectionSuccess() {
        HarryPotterOpenapiDataConnector.Book[] books = new HarryPotterOpenapiDataConnector.Book[]{};
        HarryPotterOpenapiDataConnector.Character[] characters = new HarryPotterOpenapiDataConnector.Character[]{};

        when(restTemplate.getForEntity("http://example.com/api/books", HarryPotterOpenapiDataConnector.Book[].class))
                .thenReturn(new ResponseEntity<>(books, HttpStatus.OK));
        when(restTemplate.getForEntity("http://example.com/api/characters", HarryPotterOpenapiDataConnector.Character[].class))
                .thenReturn(new ResponseEntity<>(characters, HttpStatus.OK));

        ResponseEntity<TestConnectionResponse> response = dataConnector.testConnection(new TestConnectionRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Connection successful", response.getBody().getMessage());
    }

    @Test
    void testTestConnectionBooksFailure() {
        when(restTemplate.getForEntity("http://example.com/api/books", HarryPotterOpenapiDataConnector.Book[].class))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<TestConnectionResponse> response = dataConnector.testConnection(new TestConnectionRequest());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed to connect to /books endpoint. Status: 500", response.getBody().getMessage());
    }

    @Test
    void testTestConnectionCharactersFailure() {
        HarryPotterOpenapiDataConnector.Book[] books = new HarryPotterOpenapiDataConnector.Book[]{};
        when(restTemplate.getForEntity("http://example.com/api/books", HarryPotterOpenapiDataConnector.Book[].class))
                .thenReturn(new ResponseEntity<>(books, HttpStatus.OK));
        when(restTemplate.getForEntity("http://example.com/api/characters", HarryPotterOpenapiDataConnector.Character[].class))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<TestConnectionResponse> response = dataConnector.testConnection(new TestConnectionRequest());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed to connect to /characters endpoint. Status: 500", response.getBody().getMessage());
    }

    private HarryPotterOpenapiDataConnector.Book createBook(String title, String author) {
        HarryPotterOpenapiDataConnector.Book book = new HarryPotterOpenapiDataConnector.Book();
        book.title = title;
        book.author = author;
        return book;
    }

    private HarryPotterOpenapiDataConnector.Character createCharacter(String name, String house) {
        HarryPotterOpenapiDataConnector.Character character = new HarryPotterOpenapiDataConnector.Character();
        character.name = name;
        character.house = house;
        return character;
    }
}