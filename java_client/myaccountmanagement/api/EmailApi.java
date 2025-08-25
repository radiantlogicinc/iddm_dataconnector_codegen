package com.okta.myaccount.myaccountmanagement.api;

import com.okta.myaccount.myaccountmanagement.invoker.ApiClient;
import com.okta.myaccount.myaccountmanagement.invoker.BaseApi;

import com.okta.myaccount.myaccountmanagement.model.CreateEmailRequest;
import com.okta.myaccount.myaccountmanagement.model.Email;
import com.okta.myaccount.myaccountmanagement.model.Error;
import com.okta.myaccount.myaccountmanagement.model.PollChallengeForEmailMagicLink200Response;
import com.okta.myaccount.myaccountmanagement.model.SendEmailChallenge201Response;
import com.okta.myaccount.myaccountmanagement.model.SendEmailChallengeRequest;
import com.okta.myaccount.myaccountmanagement.model.VerifyEmailOtpRequest;

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
public class EmailApi extends BaseApi {

    public EmailApi() {
        super(new ApiClient());
    }

    public EmailApi(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Create an Email
     * Creates a primary or secondary email address for the user&#39;s account. The new email address has an &#x60;UNVERIFIED&#x60; status. 
     * <p><b>201</b> - Example response
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>409</b> - Conflict
     * @param createEmailRequest New email (optional)
     * @return Email
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Email createEmail(CreateEmailRequest createEmailRequest) throws RestClientException {
        return createEmailWithHttpInfo(createEmailRequest).getBody();
    }

    /**
     * Create an Email
     * Creates a primary or secondary email address for the user&#39;s account. The new email address has an &#x60;UNVERIFIED&#x60; status. 
     * <p><b>201</b> - Example response
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>409</b> - Conflict
     * @param createEmailRequest New email (optional)
     * @return ResponseEntity&lt;Email&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Email> createEmailWithHttpInfo(CreateEmailRequest createEmailRequest) throws RestClientException {
        Object localVarPostBody = createEmailRequest;
        

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

        ParameterizedTypeReference<Email> localReturnType = new ParameterizedTypeReference<Email>() {};
        return apiClient.invokeAPI("/idp/myaccount/emails", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Delete an Email
     * Deletes the current user&#39;s email information by ID. You can only delete unverified primary and secondary emails. 
     * <p><b>204</b> - No Content
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>404</b> - Not Found
     * @param id The email ID Use &#x60;GET /idp/myaccount/emails&#x60; or &#x60;POST /idp/myaccount/emails&#x60; operations to obtain the email ID when adding a new email address.  (required)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void deleteEmail(String id) throws RestClientException {
        deleteEmailWithHttpInfo(id);
    }

    /**
     * Delete an Email
     * Deletes the current user&#39;s email information by ID. You can only delete unverified primary and secondary emails. 
     * <p><b>204</b> - No Content
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>404</b> - Not Found
     * @param id The email ID Use &#x60;GET /idp/myaccount/emails&#x60; or &#x60;POST /idp/myaccount/emails&#x60; operations to obtain the email ID when adding a new email address.  (required)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> deleteEmailWithHttpInfo(String id) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling deleteEmail");
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
        return apiClient.invokeAPI("/idp/myaccount/emails/{id}", HttpMethod.DELETE, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Retrieve an Email
     * Retrieves the current user&#39;s email information by ID: a collection of links that describe the acceptable email operations
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * @param id The email ID Use &#x60;GET /idp/myaccount/emails&#x60; or &#x60;POST /idp/myaccount/emails&#x60; operations to obtain the email ID when adding a new email address.  (required)
     * @return Email
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Email getEmail(String id) throws RestClientException {
        return getEmailWithHttpInfo(id).getBody();
    }

    /**
     * Retrieve an Email
     * Retrieves the current user&#39;s email information by ID: a collection of links that describe the acceptable email operations
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * @param id The email ID Use &#x60;GET /idp/myaccount/emails&#x60; or &#x60;POST /idp/myaccount/emails&#x60; operations to obtain the email ID when adding a new email address.  (required)
     * @return ResponseEntity&lt;Email&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Email> getEmailWithHttpInfo(String id) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling getEmail");
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

        ParameterizedTypeReference<Email> localReturnType = new ParameterizedTypeReference<Email>() {};
        return apiClient.invokeAPI("/idp/myaccount/emails/{id}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * List all Emails
     * Lists all of the current user&#39;s email information: a collection of links for each email that describe the acceptable operations 
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * @return List&lt;Email&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<Email> listEmails() throws RestClientException {
        return listEmailsWithHttpInfo().getBody();
    }

    /**
     * List all Emails
     * Lists all of the current user&#39;s email information: a collection of links for each email that describe the acceptable operations 
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * @return ResponseEntity&lt;List&lt;Email&gt;&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<List<Email>> listEmailsWithHttpInfo() throws RestClientException {
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

        ParameterizedTypeReference<List<Email>> localReturnType = new ParameterizedTypeReference<List<Email>>() {};
        return apiClient.invokeAPI("/idp/myaccount/emails", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Poll the Challenge for Email Magic Link
     * Polls for the email challenge&#39;s status
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>404</b> - Not Found
     * @param id The email ID  Use the &#x60;GET /idp/myaccount/emails&#x60; or &#x60;POST /idp/myaccount/emails&#x60; operations to obtain the ID when adding a new email address. (required)
     * @param challengeId The &#x60;challengeId&#x60; of the email  Use the &#x60;POST /idp/myaccount/emails/{id}/challenge/&#x60; operation to obtain the &#x60;challengeId&#x60; when creating a new challenge. (required)
     * @return PollChallengeForEmailMagicLink200Response
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public PollChallengeForEmailMagicLink200Response pollChallengeForEmailMagicLink(String id, String challengeId) throws RestClientException {
        return pollChallengeForEmailMagicLinkWithHttpInfo(id, challengeId).getBody();
    }

    /**
     * Poll the Challenge for Email Magic Link
     * Polls for the email challenge&#39;s status
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>404</b> - Not Found
     * @param id The email ID  Use the &#x60;GET /idp/myaccount/emails&#x60; or &#x60;POST /idp/myaccount/emails&#x60; operations to obtain the ID when adding a new email address. (required)
     * @param challengeId The &#x60;challengeId&#x60; of the email  Use the &#x60;POST /idp/myaccount/emails/{id}/challenge/&#x60; operation to obtain the &#x60;challengeId&#x60; when creating a new challenge. (required)
     * @return ResponseEntity&lt;PollChallengeForEmailMagicLink200Response&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<PollChallengeForEmailMagicLink200Response> pollChallengeForEmailMagicLinkWithHttpInfo(String id, String challengeId) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling pollChallengeForEmailMagicLink");
        }
        
        // verify the required parameter 'challengeId' is set
        if (challengeId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'challengeId' when calling pollChallengeForEmailMagicLink");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("id", id);
        uriVariables.put("challengeId", challengeId);

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

        ParameterizedTypeReference<PollChallengeForEmailMagicLink200Response> localReturnType = new ParameterizedTypeReference<PollChallengeForEmailMagicLink200Response>() {};
        return apiClient.invokeAPI("/idp/myaccount/emails/{id}/challenge/{challengeId}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Send an Email Challenge
     * Sends a \\\&quot;Confirm email address change\\\&quot; email to the user with a one-time passcode for verification. Also, the user receives a \\\&quot;Notice of pending email address change\\\&quot; email. After the challenge is verified, the email becomes active.
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param id The email ID  Use the &#x60;GET /idp/myaccount/emails&#x60; or &#x60;POST /idp/myaccount/emails&#x60; operations when adding a new email address. (required)
     * @param sendEmailChallengeRequest  (optional)
     * @return SendEmailChallenge201Response
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public SendEmailChallenge201Response sendEmailChallenge(String id, SendEmailChallengeRequest sendEmailChallengeRequest) throws RestClientException {
        return sendEmailChallengeWithHttpInfo(id, sendEmailChallengeRequest).getBody();
    }

    /**
     * Send an Email Challenge
     * Sends a \\\&quot;Confirm email address change\\\&quot; email to the user with a one-time passcode for verification. Also, the user receives a \\\&quot;Notice of pending email address change\\\&quot; email. After the challenge is verified, the email becomes active.
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param id The email ID  Use the &#x60;GET /idp/myaccount/emails&#x60; or &#x60;POST /idp/myaccount/emails&#x60; operations when adding a new email address. (required)
     * @param sendEmailChallengeRequest  (optional)
     * @return ResponseEntity&lt;SendEmailChallenge201Response&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<SendEmailChallenge201Response> sendEmailChallengeWithHttpInfo(String id, SendEmailChallengeRequest sendEmailChallengeRequest) throws RestClientException {
        Object localVarPostBody = sendEmailChallengeRequest;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling sendEmailChallenge");
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

        ParameterizedTypeReference<SendEmailChallenge201Response> localReturnType = new ParameterizedTypeReference<SendEmailChallenge201Response>() {};
        return apiClient.invokeAPI("/idp/myaccount/emails/{id}/challenge", HttpMethod.POST, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Verify an Email OTP
     * Verifies the email challenge with the code that the user receives from the \\\&quot;Confirm email address change\\\&quot; email. Once verified, the email is active. 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param id The email ID Use &#x60;GET /idp/myaccount/emails&#x60; or &#x60;POST /idp/myaccount/emails&#x60; operations to obtain the email ID when adding a new email address.  (required)
     * @param challengeId The &#x60;challengeId&#x60; of the email Use the &#x60;POST /idp/myaccount/emails/{id}/challenge&#x60; operation to obtain the &#x60;challengeId&#x60; when creating a new challenge.  (required)
     * @param verifyEmailOtpRequest  (optional)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void verifyEmailOtp(String id, String challengeId, VerifyEmailOtpRequest verifyEmailOtpRequest) throws RestClientException {
        verifyEmailOtpWithHttpInfo(id, challengeId, verifyEmailOtpRequest);
    }

    /**
     * Verify an Email OTP
     * Verifies the email challenge with the code that the user receives from the \\\&quot;Confirm email address change\\\&quot; email. Once verified, the email is active. 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param id The email ID Use &#x60;GET /idp/myaccount/emails&#x60; or &#x60;POST /idp/myaccount/emails&#x60; operations to obtain the email ID when adding a new email address.  (required)
     * @param challengeId The &#x60;challengeId&#x60; of the email Use the &#x60;POST /idp/myaccount/emails/{id}/challenge&#x60; operation to obtain the &#x60;challengeId&#x60; when creating a new challenge.  (required)
     * @param verifyEmailOtpRequest  (optional)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> verifyEmailOtpWithHttpInfo(String id, String challengeId, VerifyEmailOtpRequest verifyEmailOtpRequest) throws RestClientException {
        Object localVarPostBody = verifyEmailOtpRequest;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling verifyEmailOtp");
        }
        
        // verify the required parameter 'challengeId' is set
        if (challengeId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'challengeId' when calling verifyEmailOtp");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("id", id);
        uriVariables.put("challengeId", challengeId);

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
        return apiClient.invokeAPI("/idp/myaccount/emails/{id}/challenge/{challengeId}/verify", HttpMethod.POST, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
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
