package com.radiantlogic.openapi.generated.harrypotterapi.api;

import com.radiantlogic.openapi.generated.harrypotterapi.invoker.ApiClient;
import com.radiantlogic.openapi.generated.harrypotterapi.invoker.BaseApi;

import com.radiantlogic.openapi.generated.harrypotterapi.model.Book;
import com.radiantlogic.openapi.generated.harrypotterapi.model.InlineObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@javax.annotation.Generated(value = "com.radiantlogic.openapi.codegen.javaclient.generate.codegen.RadiantJavaClientCodegen", date = "2025-07-20T12:50:53.280565630Z[GMT]", comments = "Generator version: unset")
public class BooksApi extends BaseApi {

    public BooksApi() {
        super(new ApiClient());
    }

    public BooksApi(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * 
     * Returns all Harry Potter books.
     * <p><b>200</b> - A list of books.
     * <p><b>404</b> - Not Found.
     * @param index Returns only one item, the one that on the whole list has the index selected. (optional)
     * @param max Returns the whole list cropped by the number passed. (optional)
     * @param page If max is used, you can also use this param to indicate where to start cropping. (optional)
     * @param search Searches in all the items and returns the best matches. (optional)
     * @return List&lt;Book&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<Book> booksGet(Integer index, Integer max, Integer page, String search) throws RestClientException {
        return booksGetWithHttpInfo(index, max, page, search).getBody();
    }

    /**
     * 
     * Returns all Harry Potter books.
     * <p><b>200</b> - A list of books.
     * <p><b>404</b> - Not Found.
     * @param index Returns only one item, the one that on the whole list has the index selected. (optional)
     * @param max Returns the whole list cropped by the number passed. (optional)
     * @param page If max is used, you can also use this param to indicate where to start cropping. (optional)
     * @param search Searches in all the items and returns the best matches. (optional)
     * @return ResponseEntity&lt;List&lt;Book&gt;&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<List<Book>> booksGetWithHttpInfo(Integer index, Integer max, Integer page, String search) throws RestClientException {
        Object localVarPostBody = null;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "index", index));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "max", max));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "search", search));
        

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<List<Book>> localReturnType = new ParameterizedTypeReference<List<Book>>() {};
        return apiClient.invokeAPI("/books", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * 
     * Returns a random Harry Potter book.
     * <p><b>200</b> - A single random book.
     * @return Book
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Book booksRandomGet() throws RestClientException {
        return booksRandomGetWithHttpInfo().getBody();
    }

    /**
     * 
     * Returns a random Harry Potter book.
     * <p><b>200</b> - A single random book.
     * @return ResponseEntity&lt;Book&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Book> booksRandomGetWithHttpInfo() throws RestClientException {
        Object localVarPostBody = null;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Book> localReturnType = new ParameterizedTypeReference<Book>() {};
        return apiClient.invokeAPI("/books/random", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }

    @Override
    public <T> ResponseEntity<T> invokeAPI(String url, HttpMethod method, Object request, ParameterizedTypeReference<T> returnType) throws RestClientException {
        String localVarPath = url.replace(apiClient.getBasePath(), "");
        Object localVarPostBody = request;

        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        return apiClient.invokeAPI(localVarPath, method, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, returnType);
    }
}
