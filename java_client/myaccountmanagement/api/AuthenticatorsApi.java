package com.okta.myaccount.myaccountmanagement.api;

import com.okta.myaccount.myaccountmanagement.invoker.ApiClient;
import com.okta.myaccount.myaccountmanagement.invoker.BaseApi;

import com.okta.myaccount.myaccountmanagement.model.Authenticator;
import com.okta.myaccount.myaccountmanagement.model.AuthenticatorEnrollment;
import com.okta.myaccount.myaccountmanagement.model.Error;
import com.okta.myaccount.myaccountmanagement.model.UpdateAuthenticatorEnrollmentRequest;

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
public class AuthenticatorsApi extends BaseApi {

    public AuthenticatorsApi() {
        super(new ApiClient());
    }

    public AuthenticatorsApi(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Retrieve an Authenticator
     * Retrieves an authenticator by &#x60;authenticatorId&#x60; 
     * <p><b>200</b> - Authenticator
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * <p><b>429</b> - Too Many Requests
     * @param authenticatorId &#x60;id&#x60; of the authenticator (required)
     * @param expand Optional additional items to return in the &#x60;_embedded&#x60; object. Currently supports the value &#x60;enrollments&#x60;. (optional)
     * @return Authenticator
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Authenticator getAuthenticator(String authenticatorId, String expand) throws RestClientException {
        return getAuthenticatorWithHttpInfo(authenticatorId, expand).getBody();
    }

    /**
     * Retrieve an Authenticator
     * Retrieves an authenticator by &#x60;authenticatorId&#x60; 
     * <p><b>200</b> - Authenticator
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * <p><b>429</b> - Too Many Requests
     * @param authenticatorId &#x60;id&#x60; of the authenticator (required)
     * @param expand Optional additional items to return in the &#x60;_embedded&#x60; object. Currently supports the value &#x60;enrollments&#x60;. (optional)
     * @return ResponseEntity&lt;Authenticator&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Authenticator> getAuthenticatorWithHttpInfo(String authenticatorId, String expand) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'authenticatorId' is set
        if (authenticatorId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'authenticatorId' when calling getAuthenticator");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("authenticatorId", authenticatorId);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "expand", expand));
        

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "oauth2" };

        ParameterizedTypeReference<Authenticator> localReturnType = new ParameterizedTypeReference<Authenticator>() {};
        return apiClient.invokeAPI("/idp/myaccount/authenticators/{authenticatorId}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Retrieve an Enrollment
     * Retrieves an enrollment by &#x60;enrollmentId&#x60; 
     * <p><b>200</b> - Enrollment
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * <p><b>429</b> - Too Many Requests
     * @param authenticatorId &#x60;id&#x60; of the authenticator (required)
     * @param enrollmentId &#x60;id&#x60; of the authenticator enrollment (required)
     * @return AuthenticatorEnrollment
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public AuthenticatorEnrollment getEnrollment(String authenticatorId, String enrollmentId) throws RestClientException {
        return getEnrollmentWithHttpInfo(authenticatorId, enrollmentId).getBody();
    }

    /**
     * Retrieve an Enrollment
     * Retrieves an enrollment by &#x60;enrollmentId&#x60; 
     * <p><b>200</b> - Enrollment
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * <p><b>429</b> - Too Many Requests
     * @param authenticatorId &#x60;id&#x60; of the authenticator (required)
     * @param enrollmentId &#x60;id&#x60; of the authenticator enrollment (required)
     * @return ResponseEntity&lt;AuthenticatorEnrollment&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<AuthenticatorEnrollment> getEnrollmentWithHttpInfo(String authenticatorId, String enrollmentId) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'authenticatorId' is set
        if (authenticatorId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'authenticatorId' when calling getEnrollment");
        }
        
        // verify the required parameter 'enrollmentId' is set
        if (enrollmentId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'enrollmentId' when calling getEnrollment");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("authenticatorId", authenticatorId);
        uriVariables.put("enrollmentId", enrollmentId);

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

        String[] localVarAuthNames = new String[] { "oauth2" };

        ParameterizedTypeReference<AuthenticatorEnrollment> localReturnType = new ParameterizedTypeReference<AuthenticatorEnrollment>() {};
        return apiClient.invokeAPI("/idp/myaccount/authenticators/{authenticatorId}/enrollments/{enrollmentId}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * List all Authenticators
     * Lists all of the authenticators for the current user 
     * <p><b>200</b> - Authenticators
     * <p><b>403</b> - Forbidden
     * <p><b>429</b> - Too Many Requests
     * @param expand Optional additional items to return in the &#x60;_embedded&#x60; object. Currently supports the value &#x60;enrollments&#x60;. (optional)
     * @return List&lt;Authenticator&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<Authenticator> listAuthenticators(String expand) throws RestClientException {
        return listAuthenticatorsWithHttpInfo(expand).getBody();
    }

    /**
     * List all Authenticators
     * Lists all of the authenticators for the current user 
     * <p><b>200</b> - Authenticators
     * <p><b>403</b> - Forbidden
     * <p><b>429</b> - Too Many Requests
     * @param expand Optional additional items to return in the &#x60;_embedded&#x60; object. Currently supports the value &#x60;enrollments&#x60;. (optional)
     * @return ResponseEntity&lt;List&lt;Authenticator&gt;&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<List<Authenticator>> listAuthenticatorsWithHttpInfo(String expand) throws RestClientException {
        Object localVarPostBody = null;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "expand", expand));
        

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "oauth2" };

        ParameterizedTypeReference<List<Authenticator>> localReturnType = new ParameterizedTypeReference<List<Authenticator>>() {};
        return apiClient.invokeAPI("/idp/myaccount/authenticators", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * List all Enrollments
     * Lists all enrollments the current user has for an authenticator 
     * <p><b>200</b> - Enrollments
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * <p><b>429</b> - Too Many Requests
     * @param authenticatorId &#x60;id&#x60; of the authenticator (required)
     * @return List&lt;AuthenticatorEnrollment&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<AuthenticatorEnrollment> listEnrollments(String authenticatorId) throws RestClientException {
        return listEnrollmentsWithHttpInfo(authenticatorId).getBody();
    }

    /**
     * List all Enrollments
     * Lists all enrollments the current user has for an authenticator 
     * <p><b>200</b> - Enrollments
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * <p><b>429</b> - Too Many Requests
     * @param authenticatorId &#x60;id&#x60; of the authenticator (required)
     * @return ResponseEntity&lt;List&lt;AuthenticatorEnrollment&gt;&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<List<AuthenticatorEnrollment>> listEnrollmentsWithHttpInfo(String authenticatorId) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'authenticatorId' is set
        if (authenticatorId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'authenticatorId' when calling listEnrollments");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("authenticatorId", authenticatorId);

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

        String[] localVarAuthNames = new String[] { "oauth2" };

        ParameterizedTypeReference<List<AuthenticatorEnrollment>> localReturnType = new ParameterizedTypeReference<List<AuthenticatorEnrollment>>() {};
        return apiClient.invokeAPI("/idp/myaccount/authenticators/{authenticatorId}/enrollments", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Update an enrollment
     * Updates an authenticator enrollment by &#x60;enrollmentId&#x60;. The following update operations are allowed: * Update the enrollment nickname * Remove the enrollment nickname 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Access Denied
     * <p><b>404</b> - Resource Not Found
     * @param authenticatorId &#x60;id&#x60; of the authenticator (required)
     * @param enrollmentId &#x60;id&#x60; of the authenticator enrollment (required)
     * @param updateAuthenticatorEnrollmentRequest  (optional)
     * @return AuthenticatorEnrollment
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public AuthenticatorEnrollment updateEnrollment(String authenticatorId, String enrollmentId, UpdateAuthenticatorEnrollmentRequest updateAuthenticatorEnrollmentRequest) throws RestClientException {
        return updateEnrollmentWithHttpInfo(authenticatorId, enrollmentId, updateAuthenticatorEnrollmentRequest).getBody();
    }

    /**
     * Update an enrollment
     * Updates an authenticator enrollment by &#x60;enrollmentId&#x60;. The following update operations are allowed: * Update the enrollment nickname * Remove the enrollment nickname 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Access Denied
     * <p><b>404</b> - Resource Not Found
     * @param authenticatorId &#x60;id&#x60; of the authenticator (required)
     * @param enrollmentId &#x60;id&#x60; of the authenticator enrollment (required)
     * @param updateAuthenticatorEnrollmentRequest  (optional)
     * @return ResponseEntity&lt;AuthenticatorEnrollment&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<AuthenticatorEnrollment> updateEnrollmentWithHttpInfo(String authenticatorId, String enrollmentId, UpdateAuthenticatorEnrollmentRequest updateAuthenticatorEnrollmentRequest) throws RestClientException {
        Object localVarPostBody = updateAuthenticatorEnrollmentRequest;
        
        // verify the required parameter 'authenticatorId' is set
        if (authenticatorId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'authenticatorId' when calling updateEnrollment");
        }
        
        // verify the required parameter 'enrollmentId' is set
        if (enrollmentId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'enrollmentId' when calling updateEnrollment");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("authenticatorId", authenticatorId);
        uriVariables.put("enrollmentId", enrollmentId);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json;okta-version=1.0.0"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/merge-patch+json;okta-version=1.0.0"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "oauth2" };

        ParameterizedTypeReference<AuthenticatorEnrollment> localReturnType = new ParameterizedTypeReference<AuthenticatorEnrollment>() {};
        return apiClient.invokeAPI("/idp/myaccount/authenticators/{authenticatorId}/enrollments/{enrollmentId}", HttpMethod.PATCH, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
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
            "application/merge-patch+json;okta-version=1.0.0"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "oauth2" };

        return apiClient.invokeAPI(localVarPath, method, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, returnType);
    }
}
