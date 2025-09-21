package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.base.ResponseStatus;
import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.request.TestConnectionRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HarryPotterOpenapiDataConnectorTest {

    @Mock
    private BooksApi booksApi;

    @Mock
    private CharactersApi charactersApi;

    @InjectMocks
    private HarryPotterOpenapiDataConnector connector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchBooksDelegation() {
        // Arrange
        Map<String, String> attrs = new HashMap<>();
        attrs.put("max", "5");
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getFilter()).thenReturn("(objectClass=books)");
        when(request.getAttributes()).thenReturn(attrs);

        List<Map<String, Object>> fakeBooks = List.of(
                Map.of("title", "Harry Potter and the Sorcerer's Stone"),
                Map.of("title", "Harry Potter and the Chamber of Secrets")
        );
        when(booksApi.searchBooks(attrs)).thenReturn(fakeBooks);

        // Act
        var responseEntity = connector.search(request);
        LdapResponse ldapResponse = responseEntity.getBody();

        // Assert
        assertEquals(ResponseStatus.OK, responseEntity.getStatus());
        assertNotNull(ldapResponse);
        assertEquals(fakeBooks, ldapResponse.getData());
        verify(booksApi, times(1)).searchBooks(attrs);
        verifyNoInteractions(charactersApi);
    }

    @Test
    void testSearchCharactersDelegation() {
        // Arrange
        Map<String, String> attrs = new HashMap<>();
        attrs.put("search", "Harry");
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getFilter()).thenReturn("(objectClass=characters)");
        when(request.getAttributes()).thenReturn(attrs);

        List<Map<String, Object>> fakeChars = List.of(
                Map.of("name", "Harry Potter"),
                Map.of("name", "Hermione Granger")
        );
        when(charactersApi.searchCharacters(attrs)).thenReturn(fakeChars);

        // Act
        var responseEntity = connector.search(request);
        LdapResponse ldapResponse = responseEntity.getBody();

        // Assert
        assertEquals(ResponseStatus.OK, responseEntity.getStatus());
        assertEquals(fakeChars, ldapResponse.getData());
        verify(charactersApi, times(1)).searchCharacters(attrs);
        verifyNoInteractions(booksApi);
    }

    @Test
    void testSearchUnknownObjectReturnsEmpty() {
        // Arrange
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getFilter()).thenReturn("(objectClass=unknown)");
        when(request.getAttributes()).thenReturn(Collections.emptyMap());

        // Act
        var responseEntity = connector.search(request);
        LdapResponse ldapResponse = responseEntity.getBody();

        // Assert
        assertEquals(ResponseStatus.OK, responseEntity.getStatus());
        assertTrue(ldapResponse.getData().isEmpty());
        verifyNoInteractions(booksApi, charactersApi);
    }

    @Test
    void testConnectionSuccess() {
        // Arrange
        TestConnectionRequest request = mock(TestConnectionRequest.class);
        when(booksApi.searchBooks(anyMap())).thenReturn(Collections.emptyList());
        when(charactersApi.searchCharacters(anyMap())).thenReturn(Collections.emptyList());

        // Act
        var responseEntity = connector.testConnection(request);
        TestConnectionResponse tcResponse = responseEntity.getBody();

        // Assert
        assertEquals(ResponseStatus.OK, responseEntity.getStatus());
        assertEquals(ResponseStatus.OK, tcResponse.getStatus());
        verify(booksApi, times(1)).searchBooks(anyMap());
        verify(charactersApi, times(1)).searchCharacters(anyMap());
    }

    @Test
    void testConnectionFailure() {
        // Arrange
        TestConnectionRequest request = mock(TestConnectionRequest.class);
        when(booksApi.searchBooks(anyMap())).thenThrow(new RuntimeException("Books API down"));
        when(charactersApi.searchCharacters(anyMap())).thenReturn(Collections.emptyList());

        // Act
        var responseEntity = connector.testConnection(request);
        TestConnectionResponse tcResponse = responseEntity.getBody();

        // Assert
        assertEquals(ResponseStatus.ERROR, responseEntity.getStatus());
        assertEquals(ResponseStatus.ERROR, tcResponse.getStatus());
        assertTrue(tcResponse.getMessage().contains("Books API down"));
        verify(booksApi, times(1)).searchBooks(anyMap());
        // charactersApi may or may not be called depending on implementation order;
        // we only assert that the exception short‑circuits the response.
    }
}