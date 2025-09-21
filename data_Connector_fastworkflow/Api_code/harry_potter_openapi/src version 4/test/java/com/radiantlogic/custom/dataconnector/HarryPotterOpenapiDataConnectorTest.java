package com.radiantlogic.custom.dataconnector.harry_potter_openapiapi;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HarryPotterOpenapiDataConnectorTest {

    @Mock
    private BooksApi booksApi;

    @Mock
    private SpellsApi spellsApi;

    @Mock
    private CharactersApi charactersApi;

    @Mock
    private ConnectionConfiguration connectionConfig;

    @InjectMocks
    private HarryPotterOpenapiDataConnector dataConnector;

    @BeforeEach
    void setUp() {
        // Initialize any necessary test data
    }

    @Test
    void testSearchBooks() {
        LdapSearchRequest request = new LdapSearchRequest();
        request.setBaseDn("ou=books,dc=harrypotter,dc=com");
        request.setFilter("(objectClass=*)");

        LdapResponse response = dataConnector.search(request);

        assertNotNull(response);
        assertEquals(0, response.getResultCode());
    }

    @Test
    void testSearchSpells() {
        LdapSearchRequest request = new LdapSearchRequest();
        request.setBaseDn("ou=spells,dc=harrypotter,dc=com");
        request.setFilter("(objectClass=*)");

        LdapResponse response = dataConnector.search(request);

        assertNotNull(response);
        assertEquals(0, response.getResultCode());
    }

    @Test
    void testSearchCharacters() {
        LdapSearchRequest request = new LdapSearchRequest();
        request.setBaseDn("ou=characters,dc=harrypotter,dc=com");
        request.setFilter("(objectClass=*)");

        LdapResponse response = dataConnector.search(request);

        assertNotNull(response);
        assertEquals(0, response.getResultCode());
    }

    @Test
    void testSearchInvalidBaseDn() {
        LdapSearchRequest request = new LdapSearchRequest();
        request.setBaseDn("ou=invalid,dc=harrypotter,dc=com");
        request.setFilter("(objectClass=*)");

        LdapResponse response = dataConnector.search(request);

        assertNotNull(response);
        assertEquals(1, response.getResultCode());
    }

    @Test
    void testTestConnectionSuccess() {
        TestConnectionRequest request = new TestConnectionRequest();

        TestConnectionResponse response = dataConnector.testConnection(request);

        assertNotNull(response);
        assertEquals(0, response.getResultCode());
    }

    @Test
    void testTestConnectionFailure() {
        // Mock the connection tests to fail
        when(dataConnector.testBooksConnection()).thenReturn(false);
        when(dataConnector.testSpellsConnection()).thenReturn(true);
        when(dataConnector.testCharactersConnection()).thenReturn(true);

        TestConnectionRequest request = new TestConnectionRequest();

        TestConnectionResponse response = dataConnector.testConnection(request);

        assertNotNull(response);
        assertEquals(1, response.getResultCode());
        assertTrue(response.getMessage().contains("Books API"));
    }
}