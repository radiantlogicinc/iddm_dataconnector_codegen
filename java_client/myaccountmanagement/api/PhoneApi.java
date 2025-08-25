package com.okta.myaccount.myaccountmanagement.api;

import com.okta.myaccount.myaccountmanagement.invoker.ApiClient;
import com.okta.myaccount.myaccountmanagement.invoker.BaseApi;

import com.okta.myaccount.myaccountmanagement.model.CreatePhoneRequest;
import com.okta.myaccount.myaccountmanagement.model.Error;
import com.okta.myaccount.myaccountmanagement.model.InlineObject;
import com.okta.myaccount.myaccountmanagement.model.Phone;
import com.okta.myaccount.myaccountmanagement.model.SendPhoneChallengeRequest;
import com.okta.myaccount.myaccountmanagement.model.VerifyPhoneChallengeRequest;

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
public class PhoneApi extends BaseApi {

    public PhoneApi() {
        super(new ApiClient());
    }

    public PhoneApi(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Create a Phone
     * Creates an &#x60;UNVERIFIED&#x60; status phone for either the SMS or CALL method to the user&#39;s MyAccount setting
     * <p><b>201</b> - Example response
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>409</b> - Conflict
     * <p><b>500</b> - Internal Server Error
     * @param createPhoneRequest  (optional)
     * @return Phone
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Phone createPhone(CreatePhoneRequest createPhoneRequest) throws RestClientException {
        return createPhoneWithHttpInfo(createPhoneRequest).getBody();
    }

    /**
     * Create a Phone
     * Creates an &#x60;UNVERIFIED&#x60; status phone for either the SMS or CALL method to the user&#39;s MyAccount setting
     * <p><b>201</b> - Example response
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>409</b> - Conflict
     * <p><b>500</b> - Internal Server Error
     * @param createPhoneRequest  (optional)
     * @return ResponseEntity&lt;Phone&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Phone> createPhoneWithHttpInfo(CreatePhoneRequest createPhoneRequest) throws RestClientException {
        Object localVarPostBody = createPhoneRequest;
        

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

        ParameterizedTypeReference<Phone> localReturnType = new ParameterizedTypeReference<Phone>() {};
        return apiClient.invokeAPI("/idp/myaccount/phones", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Delete a Phone
     * Deletes the current user&#39;s phone information by ID
     * <p><b>204</b> - No Content
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param id The ID of the phone. Obtain the ID of the phone through &#x60;GET /idp/myaccount/phones&#x60; or &#x60;POST /idp/myaccount/phones&#x60; when adding a new phone. (required)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void deletePhone(String id) throws RestClientException {
        deletePhoneWithHttpInfo(id);
    }

    /**
     * Delete a Phone
     * Deletes the current user&#39;s phone information by ID
     * <p><b>204</b> - No Content
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param id The ID of the phone. Obtain the ID of the phone through &#x60;GET /idp/myaccount/phones&#x60; or &#x60;POST /idp/myaccount/phones&#x60; when adding a new phone. (required)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> deletePhoneWithHttpInfo(String id) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling deletePhone");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("id", id);

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
        return apiClient.invokeAPI("/idp/myaccount/phones/{id}", HttpMethod.DELETE, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Retrieve a Phone
     * Retrieves the current user&#39;s phone information by ID. Along with a collection of links describing the operations that can be performed to the phone.
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * <p><b>404</b> - Not Found
     * @param id The ID of the phone. Obtain the ID of the phone through &#x60;GET /idp/myaccount/phones&#x60; or &#x60;POST /idp/myaccount/phones&#x60; when adding a new phone. (required)
     * @return Phone
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Phone getPhone(String id) throws RestClientException {
        return getPhoneWithHttpInfo(id).getBody();
    }

    /**
     * Retrieve a Phone
     * Retrieves the current user&#39;s phone information by ID. Along with a collection of links describing the operations that can be performed to the phone.
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * <p><b>404</b> - Not Found
     * @param id The ID of the phone. Obtain the ID of the phone through &#x60;GET /idp/myaccount/phones&#x60; or &#x60;POST /idp/myaccount/phones&#x60; when adding a new phone. (required)
     * @return ResponseEntity&lt;Phone&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Phone> getPhoneWithHttpInfo(String id) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling getPhone");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("id", id);

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

        ParameterizedTypeReference<Phone> localReturnType = new ParameterizedTypeReference<Phone>() {};
        return apiClient.invokeAPI("/idp/myaccount/phones/{id}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * List all Phones
     * Lists the current user&#39;s phone information for all phones. Includes a collection of links for each phone describing the acceptable operations.
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * @return List&lt;Phone&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<Phone> listPhones() throws RestClientException {
        return listPhonesWithHttpInfo().getBody();
    }

    /**
     * List all Phones
     * Lists the current user&#39;s phone information for all phones. Includes a collection of links for each phone describing the acceptable operations.
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * @return ResponseEntity&lt;List&lt;Phone&gt;&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<List<Phone>> listPhonesWithHttpInfo() throws RestClientException {
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

        ParameterizedTypeReference<List<Phone>> localReturnType = new ParameterizedTypeReference<List<Phone>>() {};
        return apiClient.invokeAPI("/idp/myaccount/phones", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Send a Phone Challenge
     * Sends a phone challenge using one of two methods: &#x60;SMS&#x60; or &#x60;CALL&#x60;. This request can also handle a resend challenge (retry).  Upon a successful challenge, the user receives a verification code by &#x60;SMS&#x60; or &#x60;CALL&#x60;. Send a &#x60;POST&#x60; request to the &#x60;/idp/myaccount/phones/{id}/verify&#x60; endpoint to use the verification code to verify the phone number. The verification code expires in five minutes.  &gt; **Notes:** &gt; * Sending requests to the &#x60;/idp/myaccount/phones/{id}/challenge&#x60; endpoint more often than once every 30 seconds, or at a rate that exceeds the rate limit rule configured by the admin, returns a 429 (Too Many Requests) error.
     * <p><b>200</b> - Example response after challenging a phone
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * <p><b>500</b> - Internal Server Error
     * @param id  (required)
     * @param sendPhoneChallengeRequest  (optional)
     * @return InlineObject
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public InlineObject sendPhoneChallenge(String id, SendPhoneChallengeRequest sendPhoneChallengeRequest) throws RestClientException {
        return sendPhoneChallengeWithHttpInfo(id, sendPhoneChallengeRequest).getBody();
    }

    /**
     * Send a Phone Challenge
     * Sends a phone challenge using one of two methods: &#x60;SMS&#x60; or &#x60;CALL&#x60;. This request can also handle a resend challenge (retry).  Upon a successful challenge, the user receives a verification code by &#x60;SMS&#x60; or &#x60;CALL&#x60;. Send a &#x60;POST&#x60; request to the &#x60;/idp/myaccount/phones/{id}/verify&#x60; endpoint to use the verification code to verify the phone number. The verification code expires in five minutes.  &gt; **Notes:** &gt; * Sending requests to the &#x60;/idp/myaccount/phones/{id}/challenge&#x60; endpoint more often than once every 30 seconds, or at a rate that exceeds the rate limit rule configured by the admin, returns a 429 (Too Many Requests) error.
     * <p><b>200</b> - Example response after challenging a phone
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * <p><b>500</b> - Internal Server Error
     * @param id  (required)
     * @param sendPhoneChallengeRequest  (optional)
     * @return ResponseEntity&lt;InlineObject&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<InlineObject> sendPhoneChallengeWithHttpInfo(String id, SendPhoneChallengeRequest sendPhoneChallengeRequest) throws RestClientException {
        Object localVarPostBody = sendPhoneChallengeRequest;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling sendPhoneChallenge");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("id", id);

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

        ParameterizedTypeReference<InlineObject> localReturnType = new ParameterizedTypeReference<InlineObject>() {};
        return apiClient.invokeAPI("/idp/myaccount/phones/{id}/challenge", HttpMethod.POST, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Verify a Phone Challenge
     * Verifies the phone number with the verification code that the user receives through &#x60;SMS&#x60; or &#x60;CALL&#x60;. The phone number is active upon a successful verification.  &gt; **Notes:** &gt; * Sending requests to the &#x60;/idp/myaccount/phones/{id}/verify&#x60; endpoint at a rate that exceeds the rate limit rule configured by the admin returns a 429 (Too Many Requests) error.
     * <p><b>204</b> - No Content
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * <p><b>409</b> - Conflict
     * @param id  (required)
     * @param verifyPhoneChallengeRequest  (optional)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void verifyPhoneChallenge(String id, VerifyPhoneChallengeRequest verifyPhoneChallengeRequest) throws RestClientException {
        verifyPhoneChallengeWithHttpInfo(id, verifyPhoneChallengeRequest);
    }

    /**
     * Verify a Phone Challenge
     * Verifies the phone number with the verification code that the user receives through &#x60;SMS&#x60; or &#x60;CALL&#x60;. The phone number is active upon a successful verification.  &gt; **Notes:** &gt; * Sending requests to the &#x60;/idp/myaccount/phones/{id}/verify&#x60; endpoint at a rate that exceeds the rate limit rule configured by the admin returns a 429 (Too Many Requests) error.
     * <p><b>204</b> - No Content
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * <p><b>409</b> - Conflict
     * @param id  (required)
     * @param verifyPhoneChallengeRequest  (optional)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> verifyPhoneChallengeWithHttpInfo(String id, VerifyPhoneChallengeRequest verifyPhoneChallengeRequest) throws RestClientException {
        Object localVarPostBody = verifyPhoneChallengeRequest;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling verifyPhoneChallenge");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("id", id);

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

        ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/idp/myaccount/phones/{id}/verify", HttpMethod.POST, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
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
