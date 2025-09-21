package com.radiantlogic.custom.dataconnector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.radiantlogic.iddm.base.Logger;
import com.radiantlogic.iddm.base.ReadOnlyProperties;
import com.radiantlogic.iddm.base.TestConnectionRequest;
import com.radiantlogic.iddm.base.TestConnectionResponse;
import com.radiantlogic.iddm.ldap.LdapSearchRequest;
import com.radiantlogic.iddm.ldap.LdapResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProgramsDataConnectorTest {

    @Mock
    private Logger mockLogger;
    
    @Mock
    private ReadOnlyProperties mockProperties;
    
    @Mock
    private TestConnectionRequest mockConnectionRequest;
    
    @Mock
    private LdapSearchRequest mockSearchRequest;

    private ProgramsDataConnector connector;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockProperties.getProperty("baseUrl", "")).thenReturn("https://api.example.com");
        connector = new ProgramsDataConnector(mockLogger, mockProperties);
    }

    @Test
    public void testConnectionSuccess() {
        TestConnectionResponse response = connector.testConnection(mockConnectionRequest);
        
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("ProgramsAPI", response.getSourceName());
    }

    @Test
    public void testConnectionFailure() {
        when(mockProperties.getProperty("baseUrl", "")).thenReturn("");
        
        TestConnectionResponse response = connector.testConnection(mockConnectionRequest);
        
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Base URL not configured"));
    }

    @Test
    public void testSearchSuccess() {
        when(mockSearchRequest.getFilter()).thenReturn(new Object() {
            @Override
            public String toString() {
                return "test-filter";
            }
        });
        
        LdapResponse response = connector.search(mockSearchRequest);
        
        assertNotNull(response);
        assertEquals(com.radiantlogic.iddm.ldap.LdapResultCode.SUCCESS, response.getStatus());
    }
}