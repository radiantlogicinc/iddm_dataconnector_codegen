package com.radiantlogic.custom.dataconnector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class V2DataConnectorTest {

    @Mock
    private V2ApiClient apiClient;

    @InjectMocks
    private V2DataConnector connector;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this); // Not needed with @ExtendWith
    }

    @Test
    void testSearchClusters() {
        // Arrange
        List<Map<String, Object>> mockClusters = List.of(Collections.singletonMap("clusterName", "TestCluster"));
        when(apiClient.listClusterDetails()).thenReturn(mockClusters);
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getFilter()).thenReturn("(objectType=clusters)");

        // Act
        LdapResponse response = connector.search(request);

        // Assert
        assertEquals(LdapResponse.Status.OK, response.getStatus());
        assertEquals(mockClusters, response.getData());
        verify(apiClient).listClusterDetails();
    }

    @Test
    void testSearchUnknownObject() {
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getFilter()).thenReturn("(objectType=unknown)");
        LdapResponse response = connector.search(request);
        assertEquals(LdapResponse.Status.OK, response.getStatus());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testTestConnectionSuccess() {
        // Arrange – all client methods return without throwing.
        when(apiClient.getSystemStatus()).thenReturn(Collections.singletonMap("status", "OK"));
        when(apiClient.listAlertConfigMatcherFieldNames()).thenReturn(List.of("FIELD"));
        when(apiClient.listClusterDetails()).thenReturn(List.of());
        when(apiClient.listEventTypes()).thenReturn(List.of());
        when(apiClient.getOrgFederationSettings()).thenReturn(Collections.emptyMap());
        when(apiClient.listFederationSettingConnectedOrgConfigs()).thenReturn(List.of());
        when(apiClient.listRoleMappings()).thenReturn(List.of());
        when(apiClient.listIdentityProviders()).thenReturn(List.of());
        when(apiClient.getJwks()).thenReturn(List.of());
        when(apiClient.getIdentityProviderMetadata()).thenReturn("<metadata/>");

        TestConnectionRequest request = mock(TestConnectionRequest.class);

        // Act
        TestConnectionResponse response = connector.testConnection(request);

        // Assert
        assertEquals(TestConnectionResponse.Status.OK, response.getStatus());
        assertEquals("All endpoints reachable", response.getMessage());
    }
}