package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.base.logging.Logger;
import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.request.TestConnectionRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import com.radiantlogic.iddm.base.ReadOnlyProperties;
import com.radiantlogic.iddm.base.InjectableProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HarryPotterOpenapiDataConnectorTest {

    @Mock
    private BooksApi booksApi;

    @Mock
    private SpellsApi spellsApi;

    @Mock
    private ReadOnlyProperties connectionProps;

    @InjectMocks
    private HarryPotterOpenapiDataConnector connector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* -------------------------------------------------
     *  Search – Books
     * ------------------------------------------------- */
    @Test
    void testSearchBooks() {
        // Arrange
        List<Object> mockBooks = Arrays.asList(
                new Object(), new Object()
        );
        when(booksApi.getAllBooks(any(InjectableProperties.class))).thenReturn(mockBooks);

        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getBaseDn()).thenReturn("ou=books,dc=harrypotter,dc=org");
        when(request.getFilter()).thenReturn("(objectClass=*)");
        when(request.getParameters()).thenReturn(mock(InjectableProperties.class));

        // Act
        LdapResponse response = connector.search(request);

        // Assert
        assertEquals("OK", response.getStatus());
        assertEquals(mockBooks, response.getEntries());
        verify(booksApi, times(1)).getAllBooks(any());
        verifyNoInteractions(spellsApi);
    }

    /* -------------------------------------------------
     *  Search – Spells
     * ------------------------------------------------- */
    @Test
    void testSearchSpells() {
        // Arrange
        List<Object> mockSpells = Arrays.asList(
                new Object()
        );
        when(spellsApi.getAllSpells(any(InjectableProperties.class))).thenReturn(mockSpells);

        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getBaseDn()).thenReturn("ou=spells,dc=harrypotter,dc=org");
        when(request.getFilter()).thenReturn("(objectClass=*)");
        when(request.getParameters()).thenReturn(mock(InjectableProperties.class));

        // Act
        LdapResponse response = connector.search(request);

        // Assert
        assertEquals("OK", response.getStatus());
        assertEquals(mockSpells, response.getEntries());
        verify(spellsApi, times(1)).getAllSpells(any());
        verifyNoInteractions(booksApi);
    }

    /* -------------------------------------------------
     *  Test Connection – all endpoints healthy
     * ------------------------------------------------- */
    @Test
    void testConnectionAllHealthy() {
        // Arrange
        when(booksApi.ping()).thenReturn(true);
        when(spellsApi.ping()).thenReturn(true);

        TestConnectionRequest request = mock(TestConnectionRequest.class);

        // Act
        TestConnectionResponse response = connector.testConnection(request);

        // Assert
        assertEquals("OK", response.getStatus());
        assertEquals("All endpoints reachable", response.getMessage());
        verify(booksApi).ping();
        verify(spellsApi).ping();
    }

    /* -------------------------------------------------
     *  Test Connection – one endpoint fails
     * ------------------------------------------------- */
    @Test
    void testConnectionPartialFailure() {
        // Arrange
        when(booksApi.ping()).thenReturn(true);
        when(spellsApi.ping()).thenReturn(false);

        TestConnectionRequest request = mock(TestConnectionRequest.class);

        // Act
        TestConnectionResponse response = connector.testConnection(request);

        // Assert
        assertEquals("FAILED", response.getStatus());
        assertEquals("One or more endpoints unreachable", response.getMessage());
        verify(booksApi).ping();
        verify(spellsApi).ping();
    }
}