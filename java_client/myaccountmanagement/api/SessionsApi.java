package com.okta.myaccount.myaccountmanagement.api;

import com.okta.myaccount.myaccountmanagement.invoker.ApiClient;
import com.okta.myaccount.myaccountmanagement.invoker.BaseApi;

import com.okta.myaccount.myaccountmanagement.model.Error;

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
public class SessionsApi extends BaseApi {

    public SessionsApi() {
        super(new ApiClient());
    }

    public SessionsApi(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Delete all Sessions
     * Deletes all sessions. Revokes all active identity provider sessions of the user. This forces the user to authenticate on the next operation. Also revokes OpenID Connect and OAuth refresh and access tokens issued to the user. 
     * <p><b>204</b> - No Content
     * <p><b>401</b> - Unauthorized
     * <p><b>404</b> - Resource Not Found
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void deleteSessions() throws RestClientException {
        deleteSessionsWithHttpInfo();
    }

    /**
     * Delete all Sessions
     * Deletes all sessions. Revokes all active identity provider sessions of the user. This forces the user to authenticate on the next operation. Also revokes OpenID Connect and OAuth refresh and access tokens issued to the user. 
     * <p><b>204</b> - No Content
     * <p><b>401</b> - Unauthorized
     * <p><b>404</b> - Resource Not Found
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> deleteSessionsWithHttpInfo() throws RestClientException {
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
        return apiClient.invokeAPI("/idp/myaccount/sessions", HttpMethod.DELETE, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
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
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "oauth2" };

        return apiClient.invokeAPI(localVarPath, method, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, returnType);
    }
}
