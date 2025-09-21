/* -------------------------------------------------------------
 * HarryPotterOpenapiDataConnectorTest.java
 * -------------------------------------------------------------
 * Unit tests for HarryPotterOpenapiDataConnector.
 * ------------------------------------------------------------- */

package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.base.ReadOnlyProperties;
import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import com.radiantlogic.iddm.response.ResponseStatus;
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
    private HousesApi housesApi;

    @Mock
    private ReadOnlyProperties connectionProps;

    @InjectMocks
    private HarryPotterOpenapiDataConnector connector;

    @BeforeEach
    void setUp() {
        // Default mock behavior for connection properties
        when(connectionProps.get("hostname")).thenReturn("api.harrypotter.com");
    }

    @Test
    void testSearchReturnsHouses() {
        // Arrange – mock request parameters
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getParameterAsInt("index", 0)).thenReturn(0);
        when(request.getParameterAsInt("max", 10)).thenReturn(2);
        when(request.getParameterAsInt("page", 1)).thenReturn(1);
        when(request.getParameterAsString("search", null)).thenReturn(null);

        // Mock API response
        Map<String, Object> gryffindor = new HashMap<>();
        gryffindor.put("name", "Gryffindor");
        gryffindor.put("founder", "Godric Gryffindor");

        Map<String, Object> slytherin = new HashMap<>();
        slytherin.put("name", "Slytherin");
        slytherin.put("founder", "Salazar Slytherin");

        List<Map<String, Object>> apiResult = Arrays.asList(gryffindor, slytherin);
        when(housesApi.getHouses(0, 2, 1, null)).thenReturn(apiResult);

        // Act
        LdapResponse response = connector.search(request);

        // Assert
        assertEquals(ResponseStatus.OK, response.getStatus());
        assertTrue(response.getData() instanceof List);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) response.getData();
        assertEquals(2, resultList.size());
        assertEquals("Gryffindor", resultList.get(0).get("name"));
        verify(housesApi, times(1)).getHouses(0, 2, 1, null);
    }

    @Test
    void testTestConnectionSuccess() {
        // Arrange
        when(housesApi.ping()).thenReturn(true);

        // Act
        TestConnectionResponse response = connector.testConnection(mock(com.radiantlogic.iddm.request.TestConnectionRequest.class));

        // Assert
        assertEquals(ResponseStatus.OK, response.getStatus());
        assertEquals("Connection successful", response.getMessage());
        verify(housesApi, times(1)).ping();
    }

    @Test
    void testTestConnectionFailure() {
        // Arrange
        when(housesApi.ping()).thenReturn(false);

        // Act
        TestConnectionResponse response = connector.testConnection(mock(com.radiantlogic.iddm.request.TestConnectionRequest.class));

        // Assert
        assertEquals(ResponseStatus.ERROR, response.getStatus());
        assertEquals("Connection failed", response.getMessage());
        verify(housesApi, times(1)).ping();
    }
}