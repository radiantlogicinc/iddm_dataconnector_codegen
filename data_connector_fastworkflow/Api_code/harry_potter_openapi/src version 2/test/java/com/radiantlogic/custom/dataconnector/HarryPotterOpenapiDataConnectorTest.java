package com.radiantlogic.custom.dataconnector;

import com.radiantlogic.iddm.request.LdapSearchRequest;
import com.radiantlogic.iddm.response.LdapResponse;
import com.radiantlogic.iddm.request.TestConnectionRequest;
import com.radiantlogic.iddm.response.TestConnectionResponse;
import com.radiantlogic.iddm.response.ResponseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HarryPotterOpenapiDataConnectorTest {

    @Mock
    private BooksApi booksApi;

    @InjectMocks
    private HarryPotterOpenapiDataConnector connector;

    private Book sampleBook1;
    private Book sampleBook2;

    @BeforeEach
    void setUp() {
        sampleBook1 = new Book();
        sampleBook1.setId("1");
        sampleBook1.setTitle("Harry Potter and the Sorcerer's Stone");
        sampleBook1.setAuthor("J.K. Rowling");

        sampleBook2 = new Book();
        sampleBook2.setId("2");
        sampleBook2.setTitle("Harry Potter and the Chamber of Secrets");
        sampleBook2.setAuthor("J.K. Rowling");
    }

    @Test
    void testSearchReturnsBooks() {
        // Arrange
        when(booksApi.getBooks(0, 10, null)).thenReturn(Arrays.asList(sampleBook1, sampleBook2));
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getParameterAsInt("index", 0)).thenReturn(0);
        when(request.getParameterAsInt("max", 10)).thenReturn(10);
        when(request.getParameter("search")).thenReturn(null);

        // Act
        LdapResponse response = connector.search(request);

        // Assert
        assertEquals(ResponseStatus.OK, response.getStatus());
        List<?> payload = (List<?>) response.getPayload();
        assertEquals(2, payload.size());
        assertTrue(payload.containsAll(Arrays.asList(sampleBook1, sampleBook2)));
        verify(booksApi, times(1)).getBooks(0, 10, null);
    }

    @Test
    void testSearchHandlesException() {
        // Arrange
        when(booksApi.getBooks(anyInt(), anyInt(), any())).thenThrow(new RuntimeException("service down"));
        LdapSearchRequest request = mock(LdapSearchRequest.class);
        when(request.getParameterAsInt("index", 0)).thenReturn(0);
        when(request.getParameterAsInt("max", 10)).thenReturn(10);
        when(request.getParameter("search")).thenReturn(null);

        // Act
        LdapResponse response = connector.search(request);

        // Assert
        assertEquals(ResponseStatus.ERROR, response.getStatus());
        assertTrue(((List<?>) response.getPayload()).isEmpty());
    }

    @Test
    void testTestConnectionSuccess() {
        // Arrange
        doNothing().when(booksApi).getBooks(0, 1, null);
        TestConnectionRequest request = mock(TestConnectionRequest.class);

        // Act
        TestConnectionResponse response = connector.testConnection(request);

        // Assert
        assertEquals(ResponseStatus.OK, response.getStatus());
        assertEquals("Connection successful", response.getMessage());
        verify(booksApi, times(1)).getBooks(0, 1, null);
    }

    @Test
    void testTestConnectionFailure() {
        // Arrange
        doThrow(new RuntimeException("unreachable")).when(booksApi).getBooks(0, 1, null);
        TestConnectionRequest request = mock(TestConnectionRequest.class);

        // Act
        TestConnectionResponse response = connector.testConnection(request);

        // Assert
        assertEquals(ResponseStatus.ERROR, response.getStatus());
        assertEquals("unreachable", response.getMessage());
    }
}