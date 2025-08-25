package com.okta.myaccount.myaccountmanagement.api;

import com.okta.myaccount.myaccountmanagement.invoker.ApiClient;
import com.okta.myaccount.myaccountmanagement.invoker.BaseApi;

import com.okta.myaccount.myaccountmanagement.model.AppAuthenticatorEnrollment;
import com.okta.myaccount.myaccountmanagement.model.AppAuthenticatorEnrollmentRequest;
import com.okta.myaccount.myaccountmanagement.model.Error;
import com.okta.myaccount.myaccountmanagement.model.PushNotificationChallenge;
import com.okta.myaccount.myaccountmanagement.model.PushNotificationVerification;
import com.okta.myaccount.myaccountmanagement.model.UpdateAppAuthenticatorEnrollmentRequest;

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
public class AppAuthenticatorApi extends BaseApi {

    public AppAuthenticatorApi() {
        super(new ApiClient());
    }

    public AppAuthenticatorApi(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Create an App Authenticator Enrollment
     * Creates an app authenticator enrollment
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Access Denied
     * <p><b>404</b> - Resource Not Found
     * @param appAuthenticatorEnrollmentRequest  (optional)
     * @return AppAuthenticatorEnrollment
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public AppAuthenticatorEnrollment createAppAuthenticatorEnrollment(AppAuthenticatorEnrollmentRequest appAuthenticatorEnrollmentRequest) throws RestClientException {
        return createAppAuthenticatorEnrollmentWithHttpInfo(appAuthenticatorEnrollmentRequest).getBody();
    }

    /**
     * Create an App Authenticator Enrollment
     * Creates an app authenticator enrollment
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Access Denied
     * <p><b>404</b> - Resource Not Found
     * @param appAuthenticatorEnrollmentRequest  (optional)
     * @return ResponseEntity&lt;AppAuthenticatorEnrollment&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<AppAuthenticatorEnrollment> createAppAuthenticatorEnrollmentWithHttpInfo(AppAuthenticatorEnrollmentRequest appAuthenticatorEnrollmentRequest) throws RestClientException {
        Object localVarPostBody = appAuthenticatorEnrollmentRequest;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json;okta-version=1.0.0"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json, okta-version=1.0.0"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "oauth2" };

        ParameterizedTypeReference<AppAuthenticatorEnrollment> localReturnType = new ParameterizedTypeReference<AppAuthenticatorEnrollment>() {};
        return apiClient.invokeAPI("/idp/myaccount/app-authenticators", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Delete an App Authenticator Enrollment
     * Deletes an app authenticator enrollment
     * <p><b>204</b> - No Content
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Access Denied
     * <p><b>404</b> - Resource Not Found
     * @param enrollmentId Id of the user&#39;s app authenticator enrollment (required)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void deleteAppAuthenticatorEnrollment(String enrollmentId) throws RestClientException {
        deleteAppAuthenticatorEnrollmentWithHttpInfo(enrollmentId);
    }

    /**
     * Delete an App Authenticator Enrollment
     * Deletes an app authenticator enrollment
     * <p><b>204</b> - No Content
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Access Denied
     * <p><b>404</b> - Resource Not Found
     * @param enrollmentId Id of the user&#39;s app authenticator enrollment (required)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> deleteAppAuthenticatorEnrollmentWithHttpInfo(String enrollmentId) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'enrollmentId' is set
        if (enrollmentId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'enrollmentId' when calling deleteAppAuthenticatorEnrollment");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("enrollmentId", enrollmentId);

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
        return apiClient.invokeAPI("/idp/myaccount/app-authenticators/{enrollmentId}", HttpMethod.DELETE, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * List all pending Push Notification Challenges
     * Lists all pending push notification challenges
     * <p><b>200</b> - Success
     * <p><b>401</b> - Unauthorized
     * @param enrollmentId Id of the user&#39;s app authenticator enrollment (required)
     * @return List&lt;PushNotificationChallenge&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<PushNotificationChallenge> listAppAuthenticatorPendingPushNotificationChallenges(String enrollmentId) throws RestClientException {
        return listAppAuthenticatorPendingPushNotificationChallengesWithHttpInfo(enrollmentId).getBody();
    }

    /**
     * List all pending Push Notification Challenges
     * Lists all pending push notification challenges
     * <p><b>200</b> - Success
     * <p><b>401</b> - Unauthorized
     * @param enrollmentId Id of the user&#39;s app authenticator enrollment (required)
     * @return ResponseEntity&lt;List&lt;PushNotificationChallenge&gt;&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<List<PushNotificationChallenge>> listAppAuthenticatorPendingPushNotificationChallengesWithHttpInfo(String enrollmentId) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'enrollmentId' is set
        if (enrollmentId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'enrollmentId' when calling listAppAuthenticatorPendingPushNotificationChallenges");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("enrollmentId", enrollmentId);

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

        ParameterizedTypeReference<List<PushNotificationChallenge>> localReturnType = new ParameterizedTypeReference<List<PushNotificationChallenge>>() {};
        return apiClient.invokeAPI("/idp/myaccount/app-authenticators/{enrollmentId}/push/notifications", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Update an App Authenticator Enrollment
     * Updates an app authenticator enrollment  The following update operations are allowed: * Update the user verification key * Remove the user verification key * Update the push token * Update the push method transaction types  For more information, see [Access token management](https://developer.okta.com/docs/guides/authenticators-custom-authenticator/android/main/#access-token-management) in the Custom authenticator integration guide.  &gt; **Note:** The following higher risk update operations require a stronger &#x60;okta.myAccount.appAuthenticator.manage&#x60; scope: &gt; * Update the user verification key &gt; * Remove the user verification key
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Access Denied
     * <p><b>404</b> - Resource Not Found
     * @param enrollmentId Id of the user&#39;s app authenticator enrollment (required)
     * @param updateAppAuthenticatorEnrollmentRequest  (optional)
     * @return AppAuthenticatorEnrollment
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public AppAuthenticatorEnrollment updateAppAuthenticatorEnrollment(String enrollmentId, UpdateAppAuthenticatorEnrollmentRequest updateAppAuthenticatorEnrollmentRequest) throws RestClientException {
        return updateAppAuthenticatorEnrollmentWithHttpInfo(enrollmentId, updateAppAuthenticatorEnrollmentRequest).getBody();
    }

    /**
     * Update an App Authenticator Enrollment
     * Updates an app authenticator enrollment  The following update operations are allowed: * Update the user verification key * Remove the user verification key * Update the push token * Update the push method transaction types  For more information, see [Access token management](https://developer.okta.com/docs/guides/authenticators-custom-authenticator/android/main/#access-token-management) in the Custom authenticator integration guide.  &gt; **Note:** The following higher risk update operations require a stronger &#x60;okta.myAccount.appAuthenticator.manage&#x60; scope: &gt; * Update the user verification key &gt; * Remove the user verification key
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Access Denied
     * <p><b>404</b> - Resource Not Found
     * @param enrollmentId Id of the user&#39;s app authenticator enrollment (required)
     * @param updateAppAuthenticatorEnrollmentRequest  (optional)
     * @return ResponseEntity&lt;AppAuthenticatorEnrollment&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<AppAuthenticatorEnrollment> updateAppAuthenticatorEnrollmentWithHttpInfo(String enrollmentId, UpdateAppAuthenticatorEnrollmentRequest updateAppAuthenticatorEnrollmentRequest) throws RestClientException {
        Object localVarPostBody = updateAppAuthenticatorEnrollmentRequest;
        
        // verify the required parameter 'enrollmentId' is set
        if (enrollmentId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'enrollmentId' when calling updateAppAuthenticatorEnrollment");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
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

        ParameterizedTypeReference<AppAuthenticatorEnrollment> localReturnType = new ParameterizedTypeReference<AppAuthenticatorEnrollment>() {};
        return apiClient.invokeAPI("/idp/myaccount/app-authenticators/{enrollmentId}", HttpMethod.PATCH, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Verify a Push Notification Challenge Response from the App Authenticator
     * Verifies a push notification challenge from the app authenticator
     * <p><b>200</b> - Verification Success
     * <p><b>204</b> - User denied challenge attempt
     * <p><b>400</b> - Bad Request
     * @param challengeId Id of the challenge associated with the app authenticator (required)
     * @param pushNotificationVerification  (optional)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void verifyAppAuthenticatorPushNotificationChallenge(String challengeId, PushNotificationVerification pushNotificationVerification) throws RestClientException {
        verifyAppAuthenticatorPushNotificationChallengeWithHttpInfo(challengeId, pushNotificationVerification);
    }

    /**
     * Verify a Push Notification Challenge Response from the App Authenticator
     * Verifies a push notification challenge from the app authenticator
     * <p><b>200</b> - Verification Success
     * <p><b>204</b> - User denied challenge attempt
     * <p><b>400</b> - Bad Request
     * @param challengeId Id of the challenge associated with the app authenticator (required)
     * @param pushNotificationVerification  (optional)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> verifyAppAuthenticatorPushNotificationChallengeWithHttpInfo(String challengeId, PushNotificationVerification pushNotificationVerification) throws RestClientException {
        Object localVarPostBody = pushNotificationVerification;
        
        // verify the required parameter 'challengeId' is set
        if (challengeId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'challengeId' when calling verifyAppAuthenticatorPushNotificationChallenge");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("challengeId", challengeId);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = {  };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json;okta-version=1.0.0"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/idp/myaccount/app-authenticators/challenge/{challengeId}/verify", HttpMethod.POST, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
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

        final String[] localVarAccepts = {  };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json;okta-version=1.0.0"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        return apiClient.invokeAPI(localVarPath, method, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, returnType);
    }
}
