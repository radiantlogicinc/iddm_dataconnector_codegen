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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

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
    private HarryPotterOpenapiDataConnector connector;

    private LdapSearchRequest booksRequest;
    private LdapSearchRequest charactersRequest;
    private LdapSearchRequest spellsRequest;

    @BeforeEach
    void setUp() {
        // Minimal stub of LdapSearchRequest – only getBaseDn() is used by the connector
        booksRequest = mock(LdapSearchRequest.class);
        when(booksRequest.getBaseDn()).thenReturn("ou=books,dc=example,dc=com");

        charactersRequest = mock(LdapSearchRequest.class);
        when(charactersRequest.getBaseDn()).thenReturn("ou=characters,dc=example,dc=com");

        spellsRequest = mock(LdapSearchRequest.class);
        when(spellsRequest.getBaseDn()).thenReturn("ou=spells,dc=example,dc=com");
    }

    @Test
    void searchBooksRoutesToBooksApi() {
        List<Map<String, Object>> fakeResult = Collections.singletonList(
                Collections.singletonMap("title", "Harry Potter and the Sorcerer's Stone"));
        when(booksApi.getAllBooks(booksRequest)).thenReturn(fakeResult);

        LdapResponse response = connector.search(booksRequest);
        assertEquals(LdapResponse.Status.OK, response.getStatus());
        assertEquals(fakeResult, response.getData());

        verify(booksApi, times(1)).getAllBooks(booksRequest);
        verifyNoInteractions(charactersApi, spellsApi);
    }

    @Test
    void searchCharactersRoutesToCharactersApi() {
        List<Map<String, Object>> fakeResult = Collections.singletonList(
                Collections.singletonMap("name", "Hermione Granger"));
        when(charactersApi.getAllCharacters(charactersRequest)).thenReturn(fakeResult);

        LdapResponse response = connector.search(charactersRequest);
        assertEquals(LdapResponse.Status.OK, response.getStatus());
        assertEquals(fakeResult, response.getData());

        verify(charactersApi, times(1)).getAllCharacters(charactersRequest);
        verifyNoInteractions(booksApi, spellsApi);
    }

    @Test
    void searchSpellsRoutesToSpellsApi() {
        List<Map<String, Object>> fakeResult = Collections.singletonList(
                Collections.singletonMap("spell", "Expelliarmus"));
        when(spellsApi.getAllSpells(spellsRequest)).thenReturn(fakeResult);

        LdapResponse response = connector.search(spellsRequest);
        assertEquals(LdapResponse.Status.OK, response.getStatus());
        assertEquals(fakeResult, response.getData());

        verify(spellsApi, times(1)).getAllSpells(spellsRequest);
        verifyNoInteractions(booksApi, charactersApi);
    }

    @Test
    void testConnectionAggregatesAllPings() {
        when(booksApi.ping()).thenReturn(true);
        when(charactersApi.ping()).thenReturn(true);
        when(spellsApi.ping()).thenReturn(true);

        TestConnectionResponse response = connector.testConnection(new TestConnectionRequest());
        assertEquals(TestConnectionResponse.Status.OK, response.getStatus());

        verify(booksApi).ping();
        verify(charactersApi).ping();
        verify(spellsApi).ping();
    }

    @Test
    void testConnectionFailsIfAnyPingFails() {
        when(booksApi.ping()).thenReturn(true);
        when(charactersApi.ping()).thenReturn(false); // simulate failure
        when(spellsApi.ping()).thenReturn(true);

        TestConnectionResponse response = connector.testConnection(new TestConnectionRequest());
        assertEquals(TestConnectionResponse.Status.ERROR, response.getStatus());

        verify(booksApi).ping();
        verify(charactersApi).ping();
        verify(spellsApi).ping();
    }
}