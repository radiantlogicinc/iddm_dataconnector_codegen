package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.base.logging.Logger;
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
import com.radiantlogic.iddm.base.ReadOnlyProperties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BackgroundcheckDataConnectorTest {

    @Mock
    private BackgroundcheckDataConnector.OrderConfirmationApi orderConfirmationApi;

    @Mock
    private BackgroundcheckDataConnector.ResultApi resultApi;

    @Mock
    private Logger logger;

    @Mock
    private ReadOnlyProperties connectionProps;

    @InjectMocks
    private BackgroundcheckDataConnector connector;

    @BeforeEach
    void setUp() {
        when(connectionProps.get("hostname")).thenReturn("test-host");
    }

    @Test
    void search_returnsEmptyResponse() {
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        LdapResponse response = connector.search(request);
        assertNotNull(response, "Search should never return null");
        // The placeholder LdapResponse has no data; just ensure the object exists.
    }

    @Test
    void testConnection_successWhenBothApisPing() {
        when(orderConfirmationApi.ping()).thenReturn(true);
        when(resultApi.ping()).thenReturn(true);

        TestConnectionRequest request = mock(TestConnectionRequest.class);
        TestConnectionResponse response = connector.testConnection(request);

        assertTrue(response.isSuccessful(), "Overall test connection should be successful");
        verify(orderConfirmationApi).ping();
        verify(resultApi).ping();
    }

    @Test
    void testConnection_failsWhenOneApiFails() {
        when(orderConfirmationApi.ping()).thenReturn(true);
        when(resultApi.ping()).thenReturn(false);

        TestConnectionRequest request = mock(TestConnectionRequest.class);
        TestConnectionResponse response = connector.testConnection(request);

        assertFalse(response.isSuccessful(), "Overall test connection should fail if any API is unreachable");
        verify(orderConfirmationApi).ping();
        verify(resultApi).ping();
    }
}