package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.base.ResponseEntity;
import com.radiantlogic.iddm.base.ResponseStatus;
import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.request.TestConnectionRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import com.radiantlogic.iddm.base.ReadOnlyProperties;
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
    private CharactersApi charactersApiMock;

    @Mock
    private ReadOnlyProperties connectionPropsMock;

    @InjectMocks
    private HarryPotterOpenapiDataConnector connector;

    @BeforeEach
    void setUp() {
        // Ensure the mock for connection properties returns a dummy hostname
        when(connectionPropsMock.get("hostname")).thenReturn("api.harrypotter.com");
    }

    @Test
    void testSearchReturnsCharacters() {
        // Arrange – fake character data
        Map<String, Object> char1 = new HashMap<>();
        char1.put("name", "Harry Potter");
        char1.put("house", "Gryffindor");

        Map<String, Object> char2 = new HashMap<>();
        char2.put("name", "Hermione Granger");
        char2.put("house", "Gryffindor");

        List<Map<String, Object>> apiResult = Arrays.asList(char1, char2);
        when(charactersApiMock.getCharacters(anyInt(), anyInt(), anyInt(), anyString()))
                .thenReturn(apiResult);

        // Build a minimal LdapSearchRequest with parameters map
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        Map<String, String> params = new HashMap<>();
        params.put("index", "0");
        params.put("max", "10");
        params.put("page", "1");
        params.put("search", "Harry");
        when(request.getParameters()).thenReturn(params);

        // Act
        ResponseEntity<LdapResponse> responseEntity = connector.search(request);

        // Assert
        assertEquals(ResponseStatus.OK, responseEntity.getStatus());
        LdapResponse ldapResponse = responseEntity.getBody();
        assertNotNull(ldapResponse);
        assertEquals(2, ldapResponse.getEntries().size());
        assertEquals("Harry Potter", ldapResponse.getEntries().get(0).get("name"));
        verify(charactersApiMock, times(1))
                .getCharacters(eq(0), eq(10), eq(1), eq("Harry"));
    }

    @Test
    void testSearchHandlesEmptyResult() {
        when(charactersApiMock.getCharacters(anyInt(), anyInt(), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getParameters()).thenReturn(Collections.emptyMap());

        ResponseEntity<LdapResponse> response = connector.search(request);
        assertEquals(ResponseStatus.OK, response.getStatus());
        assertTrue(response.getBody().getEntries().isEmpty());
    }

    @Test
    void testConnectionSuccess() {
        when(charactersApiMock.ping()).thenReturn(true);

        TestConnectionRequest request = mock(TestConnectionRequest.class);
        ResponseEntity<TestConnectionResponse> resp = connector.testConnection(request);

        assertEquals(ResponseStatus.OK, resp.getStatus());
        assertTrue(resp.getBody().isSuccess());
        assertEquals("Connection successful", resp.getBody().getMessage());
        verify(charactersApiMock, times(1)).ping();
    }

    @Test
    void testConnectionFailure() {
        when(charactersApiMock.ping()).thenReturn(false);

        TestConnectionRequest request = mock(TestConnectionRequest.class);
        ResponseEntity<TestConnectionResponse> resp = connector.testConnection(request);

        assertEquals(ResponseStatus.OK, resp.getStatus());
        assertFalse(resp.getBody().isSuccess());
        assertEquals("Unable to reach Harry Potter API", resp.getBody().getMessage());
    }
}