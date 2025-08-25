package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.ldap.LdapSearchRequest;
import com.radiantlogic.iddm.base.TestConnectionRequest;
import com.radiantlogic.iddm.ldap.LdapResponse;
import com.radiantlogic.iddm.base.TestConnectionResponse;
import com.radiantlogic.iddm.base.ReadOnlyProperties;
import com.radiantlogic.iddm.base.InjectableProperties;
import com.radiantlogic.iddm.ldap.LdapResultCode;
import com.radiantlogic.iddm.base.SearchFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.radiantlogic.iddm.base.Logger;
import com.radiantlogic.openapi.generated.harrypotterapi.api.BooksApi;
import com.radiantlogic.openapi.generated.harrypotterapi.api.CharactersApi;
import com.radiantlogic.openapi.generated.harrypotterapi.api.HousesApi;
import com.radiantlogic.openapi.generated.harrypotterapi.api.SpellsApi;
import com.radiantlogic.openapi.generated.harrypotterapi.model.Book;
import com.radiantlogic.openapi.generated.harrypotterapi.model.Character;
import com.radiantlogic.openapi.generated.harrypotterapi.model.House;
import com.radiantlogic.openapi.generated.harrypotterapi.model.Spell;

import java.util.Collections;
import java.util.List;
import java.lang.reflect.Field;

class HarryPotterDataConnectorTest {

    @Mock
    private Logger log;

    @Mock
    private ReadOnlyProperties connectionProperties;

    @Mock
    private BooksApi booksApi;

    @Mock
    private CharactersApi charactersApi;

    @Mock
    private HousesApi housesApi;

    @Mock
    private SpellsApi spellsApi;

    private HarryPotterDataConnector connector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock connection properties
        when(connectionProperties.get("baseUrl")).thenReturn("https://api.potterdb.com");
        
        // Create a new connector instance with mocked dependencies
        connector = new HarryPotterDataConnector(log, connectionProperties);
        
        // Use reflection to inject mocked dependencies
        try {
            Field charactersApiField = HarryPotterDataConnector.class.getDeclaredField("charactersApi");
            charactersApiField.setAccessible(true);
            charactersApiField.set(connector, charactersApi);
            
            Field spellsApiField = HarryPotterDataConnector.class.getDeclaredField("spellsApi");
            spellsApiField.setAccessible(true);
            spellsApiField.set(connector, spellsApi);
            
            Field housesApiField = HarryPotterDataConnector.class.getDeclaredField("housesApi");
            housesApiField.setAccessible(true);
            housesApiField.set(connector, housesApi);
            
            Field booksApiField = HarryPotterDataConnector.class.getDeclaredField("booksApi");
            booksApiField.setAccessible(true);
            booksApiField.set(connector, booksApi);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocked dependencies", e);
        }
    }

    @Test
    void testTestConnectionSuccess() {
        TestConnectionRequest request = mock(TestConnectionRequest.class);
        when(booksApi.booksGet(1, 1, null, null)).thenReturn(Collections.emptyList());

        TestConnectionResponse response = connector.testConnection(request);

        assertTrue(response.isSuccessful());
        assertTrue(response.getDetails().contains("Connection successful"));
    }

    @Test
    void testTestConnectionFailure() {
        TestConnectionRequest request = mock(TestConnectionRequest.class);
        when(booksApi.booksGet(1, 1, null, null)).thenThrow(new RestClientException("Connection failed"));
        when(charactersApi.charactersGet(1, 1, null, null)).thenThrow(new RestClientException("Fallback also failed"));

        TestConnectionResponse response = connector.testConnection(request);

        assertFalse(response.isSuccessful());
        assertTrue(response.getDetails().contains("Connection failed"));
    }
} 