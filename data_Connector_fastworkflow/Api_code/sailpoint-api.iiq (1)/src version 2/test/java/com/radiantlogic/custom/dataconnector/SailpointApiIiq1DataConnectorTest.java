package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.request.TestConnectionRequest;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SailpointApiIiq1DataConnectorTest {

    @Mock
    private AccountsApi accountsApi;
    @Mock
    private AlertsApi alertsApi;
    @Mock
    private ApplicationsApi applicationsApi;
    @Mock
    private CheckedPolicyViolationsApi cpvApi;
    @Mock
    private EntitlementsApi entitlementsApi;
    @Mock
    private LaunchedWorkflowsApi launchedWorkflowsApi;
    @Mock
    private LaunchedWorkflowApi launchedWorkflowApi;
    @Mock
    private ObjectConfigsApi objectConfigsApi;
    @Mock
    private ObjectConfigApi objectConfigApi;
    @Mock
    private PolicyViolationsApi policyViolationsApi;

    @InjectMocks
    private SailpointApiIiq1DataConnector connector;

    @BeforeEach
    void setUp() {
        // No additional setup required – Mockito injects the mocks.
    }

    @Test
    void testSearchAccounts() {
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getBaseDn()).thenReturn("ou=Accounts,dc=example,dc=com");
        when(accountsApi.search(request)).thenReturn(
                Collections.singletonList(Map.of("id", "acc-123", "displayName", "Demo Account")));

        LdapResponse response = connector.search(request);
        assertEquals(LdapResponse.ResponseStatus.OK, response.getStatus());
        assertEquals(1, response.getData().size());
        assertEquals("acc-123", ((Map<?, ?>) response.getData().get(0)).get("id"));
        verify(accountsApi, times(1)).search(request);
        verifyNoMoreInteractions(accountsApi);
    }

    @Test
    void testSearchUnsupportedBaseDn() {
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getBaseDn()).thenReturn("ou=Unknown,dc=example,dc=com");

        LdapResponse response = connector.search(request);
        assertEquals(LdapResponse.ResponseStatus.OK, response.getStatus());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testTestConnectionAllOk() {
        // All ping() methods return true by default (Mockito returns false for boolean unless stubbed)
        when(accountsApi.ping()).thenReturn(true);
        when(alertsApi.ping()).thenReturn(true);
        when(applicationsApi.ping()).thenReturn(true);
        when(cpvApi.ping()).thenReturn(true);
        when(entitlementsApi.ping()).thenReturn(true);
        when(launchedWorkflowsApi.ping()).thenReturn(true);
        when(launchedWorkflowApi.ping()).thenReturn(true);
        when(objectConfigsApi.ping()).thenReturn(true);
        when(objectConfigApi.ping()).thenReturn(true);
        when(policyViolationsApi.ping()).thenReturn(true);

        TestConnectionResponse response = connector.testConnection(new TestConnectionRequest());
        assertEquals(TestConnectionResponse.ResponseStatus.OK, response.getStatus());
    }

    @Test
    void testTestConnectionFailure() {
        when(accountsApi.ping()).thenReturn(true);
        when(alertsApi.ping()).thenReturn(false); // simulate failure
        // other clients return true
        when(applicationsApi.ping()).thenReturn(true);
        when(cpvApi.ping()).thenReturn(true);
        when(entitlementsApi.ping()).thenReturn(true);
        when(launchedWorkflowsApi.ping()).thenReturn(true);
        when(launchedWorkflowApi.ping()).thenReturn(true);
        when(objectConfigsApi.ping()).thenReturn(true);
        when(objectConfigApi.ping()).thenReturn(true);
        when(policyViolationsApi.ping()).thenReturn(true);

        TestConnectionResponse response = connector.testConnection(new TestConnectionRequest());
        assertEquals(TestConnectionResponse.ResponseStatus.ERROR, response.getStatus());
    }
}