package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.response.ResponseStatus;
import com.radiantlogic.iddm.request.TestConnectionRequest;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OpenapiDataConnectorTest {

    @Mock
    private LoginApi loginApi;
    @Mock
    private InjectClientCertApi injectClientCertApi;
    @Mock
    private AuthenticateApi authenticateApi;
    @Mock
    private AuthnIamApi authnIamApi;
    @Mock
    private AuthnAzureApi authnAzureApi;
    @Mock
    private AuthnK8sApi authnK8sApi;
    @Mock
    private AuthnLdapApi authnLdapApi;
    @Mock
    private AuthnJwtApi authnJwtApi;
    @Mock
    private PasswordApi passwordApi;
    @Mock
    private ApiKeyApi apiKeyApi;

    @InjectMocks
    private OpenapiDataConnector connector;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this) is handled by @ExtendWith(MockitoExtension.class)
    }

    @Test
    void testSearchReturnsEmptyResult() {
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getFilter()).thenReturn("(objectClass=login)");

        LdapResponse response = connector.search(request);
        assertEquals(ResponseStatus.OK, response.getStatus());
        assertTrue(response.getData().isEmpty(), "Expected empty result set");
    }

    @Test
    void testTestConnectionSuccess() {
        // All pings succeed – no need to stub anything as default is doNothing()
        TestConnectionResponse response = connector.testConnection(new TestConnectionRequest());
        assertEquals(ResponseStatus.OK, response.getStatus());
        assertEquals("All endpoints reachable", response.getMessage());
    }

    @Test
    void testTestConnectionFailure() {
        doThrow(new RuntimeException("login ping failed")).when(loginApi).ping();
        TestConnectionResponse response = connector.testConnection(new TestConnectionRequest());
        assertEquals(ResponseStatus.ERROR, response.getStatus());
        assertTrue(response.getMessage().contains("login ping failed"));
    }
}