package com.radiantlogic.custom.dataconnector.harrypotteropenapi;

import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.request.TestConnectionRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HarryPotterOpenapiDataConnectorTest {

    @Mock
    private BooksApi booksApi;

    @Mock
    private CharactersApi charactersApi;

    @Mock
    private SpellsApi spellsApi;

    @InjectMocks
    private HarryPotterOpenapiDataConnector dataConnector;

    @BeforeEach
    void setUp() {
        dataConnector = new HarryPotterOpenapiDataConnector("http://test.com", "testKey", null);
    }

    @Test
    void testSearchBooks() {
        LdapSearchRequest request = new LdapSearchRequest("books", "(objectClass=*)");
        when(booksApi.getBooks(anyString())).thenReturn(List.of(new Book("Harry Potter")));

        LdapResponse response = dataConnector.search(request);

        assertTrue(response.isSuccess());
        assertEquals(1, ((List<?>) response.getData()).size());
        verify(booksApi).getBooks("(objectClass=*)");
    }

    @Test
    void testSearchCharacters() {
        LdapSearchRequest request = new LdapSearchRequest("characters", "(objectClass=*)");
        when(charactersApi.getCharacters(anyString())).thenReturn(List.of(new Character("Harry Potter")));

        LdapResponse response = dataConnector.search(request);

        assertTrue(response.isSuccess());
        assertEquals(1, ((List<?>) response.getData()).size());
        verify(charactersApi).getCharacters("(objectClass=*)");
    }

    @Test
    void testSearchSpells() {
        LdapSearchRequest request = new LdapSearchRequest("spells", "(objectClass=*)");
        when(spellsApi.getSpells(anyString())).thenReturn(List.of(new Spell("Expelliarmus")));

        LdapResponse response = dataConnector.search(request);

        assertTrue(response.isSuccess());
        assertEquals(1, ((List<?>) response.getData()).size());
        verify(spellsApi).getSpells("(objectClass=*)");
    }

    @Test
    void testSearchInvalidBaseDn() {
        LdapSearchRequest request = new LdapSearchRequest("invalid", "(objectClass=*)");

        LdapResponse response = dataConnector.search(request);

        assertFalse(response.isSuccess());
        assertEquals("Invalid baseDn: invalid", response.getMessage());
    }

    @Test
    void testTestConnectionSuccess() throws Exception {
        doNothing().when(booksApi).testConnection();
        doNothing().when(charactersApi).testConnection();
        doNothing().when(spellsApi).testConnection();

        TestConnectionRequest request = new TestConnectionRequest();
        TestConnectionResponse response = dataConnector.testConnection(request);

        assertTrue(response.isSuccess());
        assertEquals("Connection to all APIs successful", response.getMessage());
    }

    @Test
    void testTestConnectionFailure() throws Exception {
        doNothing().when(booksApi).testConnection();
        doThrow(new RuntimeException("Connection failed")).when(charactersApi).testConnection();
        doNothing().when(spellsApi).testConnection();

        TestConnectionRequest request = new TestConnectionRequest();
        TestConnectionResponse response = dataConnector.testConnection(request);

        assertFalse(response.isSuccess());
        assertEquals("Connection failed", response.getMessage());
    }
}

class Book {
    private String title;

    public Book(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

class Character {
    private String name;

    public Character(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Spell {
    private String name;

    public Spell(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}