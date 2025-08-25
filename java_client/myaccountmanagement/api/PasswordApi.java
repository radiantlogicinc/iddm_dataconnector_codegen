package com.okta.myaccount.myaccountmanagement.api;

import com.okta.myaccount.myaccountmanagement.invoker.ApiClient;
import com.okta.myaccount.myaccountmanagement.invoker.BaseApi;

import com.okta.myaccount.myaccountmanagement.model.Error;
import com.okta.myaccount.myaccountmanagement.model.PasswordResponse;
import com.okta.myaccount.myaccountmanagement.model.ReplacePasswordRequest;

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

@javax.annotation.Generated(value = "com.radiantlogic.openapi.codegen.javaclient.generate.codegen.RadiantJavaClientCodegen", date = "2025-08-13T20:31:08.570419254Z[GMT]", comments = "Generator version: unset")
public class PasswordApi extends BaseApi {

    public PasswordApi() {
        super(new ApiClient());
    }

    public PasswordApi(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Create a Password
     * Creates and enrolls a password for the current user
     * <p><b>201</b> - Example response
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Conflict
     * @param replacePasswordRequest New password (optional)
     * @return PasswordResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public PasswordResponse createPassword(ReplacePasswordRequest replacePasswordRequest) throws RestClientException {
        return createPasswordWithHttpInfo(replacePasswordRequest).getBody();
    }

    /**
     * Create a Password
     * Creates and enrolls a password for the current user
     * <p><b>201</b> - Example response
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Conflict
     * @param replacePasswordRequest New password (optional)
     * @return ResponseEntity&lt;PasswordResponse&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<PasswordResponse> createPasswordWithHttpInfo(ReplacePasswordRequest replacePasswordRequest) throws RestClientException {
        Object localVarPostBody = replacePasswordRequest;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json;okta-version=1.0.0"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "oauth2" };

        ParameterizedTypeReference<PasswordResponse> localReturnType = new ParameterizedTypeReference<PasswordResponse>() {};
        return apiClient.invokeAPI("/idp/myaccount/password", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Delete a Password
     * Deletes the current user&#39;s enrolled password 
     * <p><b>204</b> - No Content
     * <p><b>401</b> - Unauthorized
     * <p><b>404</b> - Not Found
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void deletePassword() throws RestClientException {
        deletePasswordWithHttpInfo();
    }

    /**
     * Delete a Password
     * Deletes the current user&#39;s enrolled password 
     * <p><b>204</b> - No Content
     * <p><b>401</b> - Unauthorized
     * <p><b>404</b> - Not Found
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> deletePasswordWithHttpInfo() throws RestClientException {
        Object localVarPostBody = null;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json;okta-version=1.0.0"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "oauth2" };

        ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/idp/myaccount/password", HttpMethod.DELETE, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Retrieve a Password
     * Retrieves the current user&#39;s password status &gt; **Note:** This request only returns information about the password, not the password itself. 
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * @return PasswordResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public PasswordResponse getPassword() throws RestClientException {
        return getPasswordWithHttpInfo().getBody();
    }

    /**
     * Retrieve a Password
     * Retrieves the current user&#39;s password status &gt; **Note:** This request only returns information about the password, not the password itself. 
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * @return ResponseEntity&lt;PasswordResponse&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<PasswordResponse> getPasswordWithHttpInfo() throws RestClientException {
        Object localVarPostBody = null;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json;okta-version=1.0.0"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "oauth2" };

        ParameterizedTypeReference<PasswordResponse> localReturnType = new ParameterizedTypeReference<PasswordResponse>() {};
        return apiClient.invokeAPI("/idp/myaccount/password", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Replace a Password
     * Replaces the password for the current user 
     * <p><b>201</b> - Example response
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * @param replacePasswordRequest New password (optional)
     * @return PasswordResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public PasswordResponse replacePassword(ReplacePasswordRequest replacePasswordRequest) throws RestClientException {
        return replacePasswordWithHttpInfo(replacePasswordRequest).getBody();
    }

    /**
     * Replace a Password
     * Replaces the password for the current user 
     * <p><b>201</b> - Example response
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * @param replacePasswordRequest New password (optional)
     * @return ResponseEntity&lt;PasswordResponse&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<PasswordResponse> replacePasswordWithHttpInfo(ReplacePasswordRequest replacePasswordRequest) throws RestClientException {
        Object localVarPostBody = replacePasswordRequest;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json;okta-version=1.0.0"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "oauth2" };

        ParameterizedTypeReference<PasswordResponse> localReturnType = new ParameterizedTypeReference<PasswordResponse>() {};
        return apiClient.invokeAPI("/idp/myaccount/password", HttpMethod.PUT, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
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
            "application/json;okta-version=1.0.0"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "oauth2" };

        return apiClient.invokeAPI(localVarPath, method, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, returnType);
    }
}
