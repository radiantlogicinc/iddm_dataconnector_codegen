package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.base.annotation.Property;
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
    private HousesApi housesApi;

    @Mock
    private SpellsApi spellsApi;

    @InjectMocks
    private HarryPotterOpenapiDataConnector connector;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this); // not needed with @ExtendWith
    }

    @Test
    void searchBooks_returnsBooksFromApi() {
        // Arrange
        List<Map<String, Object>> mockBooks = new ArrayList<>();
        mockBooks.add(Map.of("title", "Harry Potter and the Sorcerer's Stone"));
        when(booksApi.getAllBooks()).thenReturn(mockBooks);

        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getBaseDn()).thenReturn("ou=books,dc=example,dc=com");

        // Act
        LdapResponse response = connector.search(request);

        // Assert
        assertEquals(LdapResponse.Status.OK, response.getStatus());
        assertEquals(mockBooks, response.getData());
        verify(booksApi, times(1)).getAllBooks();
        verifyNoInteractions(charactersApi, housesApi, spellsApi);
    }

    @Test
    void searchCharacters_returnsCharactersFromApi() {
        List<Map<String, Object>> mockChars = List.of(Map.of("name", "Hermione Granger"));
        when(charactersApi.getAllCharacters()).thenReturn(mockChars);

        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getBaseDn()).thenReturn("ou=characters,dc=example,dc=com");

        LdapResponse response = connector.search(request);

        assertEquals(LdapResponse.Status.OK, response.getStatus());
        assertEquals(mockChars, response.getData());
        verify(charactersApi, times(1)).getAllCharacters();
    }

    @Test
    void testConnection_allEndpointsHealthy_returnsOk() {
        when(booksApi.ping()).thenReturn(true);
        when(charactersApi.ping()).thenReturn(true);
        when(housesApi.ping()).thenReturn(true);
        when(spellsApi.ping()).thenReturn(true);

        TestConnectionRequest request = mock(TestConnectionRequest.class);
        TestConnectionResponse response = connector.testConnection(request);

        assertEquals(TestConnectionResponse.Status.OK, response.getStatus());
    }

    @Test
    void testConnection_oneEndpointFails_returnsError() {
        when(booksApi.ping()).thenReturn(true);
        when(charactersApi.ping()).thenReturn(false); // simulate failure
        when(housesApi.ping()).thenReturn(true);
        when(spellsApi.ping()).thenReturn(true);

        TestConnectionRequest request = mock(TestConnectionRequest.class);
        TestConnectionResponse response = connector.testConnection(request);

        assertEquals(TestConnectionResponse.Status.ERROR, response.getStatus());
    }
}