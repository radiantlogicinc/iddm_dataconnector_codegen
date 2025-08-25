package com.okta.myaccount.myaccountmanagement.api;

import com.okta.myaccount.myaccountmanagement.invoker.ApiClient;
import com.okta.myaccount.myaccountmanagement.invoker.BaseApi;

import com.okta.myaccount.myaccountmanagement.model.Error;
import com.okta.myaccount.myaccountmanagement.model.Profile;
import com.okta.myaccount.myaccountmanagement.model.ReplaceProfileRequest;
import io.swagger.v3.oas.annotations.media.Schema;

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
public class ProfileApi extends BaseApi {

    public ProfileApi() {
        super(new ApiClient());
    }

    public ProfileApi(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Retrieve my Profile
     * Retrieves the caller&#39;s Okta user profile, without attributes excluded by the [Get my user profile schema](/openapi/okta-myaccount/myaccount/tag/Profile/#tag/Profile/operation/getProfileSchema)
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * @return Profile
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Profile getProfile() throws RestClientException {
        return getProfileWithHttpInfo().getBody();
    }

    /**
     * Retrieve my Profile
     * Retrieves the caller&#39;s Okta user profile, without attributes excluded by the [Get my user profile schema](/openapi/okta-myaccount/myaccount/tag/Profile/#tag/Profile/operation/getProfileSchema)
     * <p><b>200</b> - Example response
     * <p><b>401</b> - Unauthorized
     * @return ResponseEntity&lt;Profile&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Profile> getProfileWithHttpInfo() throws RestClientException {
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

        ParameterizedTypeReference<Profile> localReturnType = new ParameterizedTypeReference<Profile>() {};
        return apiClient.invokeAPI("/idp/myaccount/profile", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Retrieve my Profile Schema
     * Retrieves the appropriate user profile schema for the caller&#39;s [user type](https://developer.okta.com/docs/api/openapi/okta-management/management/tag/UserType/)  &gt; **Note:** If a property&#39;s value isn&#39;t visible to an end user (because it&#39;s hidden or [sensitive](https://help.okta.com/okta_help.htm?id&#x3D;ext-hide-sensitive-attributes)), then the property&#39;s definition is also hidden in the output of the MyAccount API.
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * @return Schema
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Schema getProfileSchema() throws RestClientException {
        return getProfileSchemaWithHttpInfo().getBody();
    }

    /**
     * Retrieve my Profile Schema
     * Retrieves the appropriate user profile schema for the caller&#39;s [user type](https://developer.okta.com/docs/api/openapi/okta-management/management/tag/UserType/)  &gt; **Note:** If a property&#39;s value isn&#39;t visible to an end user (because it&#39;s hidden or [sensitive](https://help.okta.com/okta_help.htm?id&#x3D;ext-hide-sensitive-attributes)), then the property&#39;s definition is also hidden in the output of the MyAccount API.
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * @return ResponseEntity&lt;Schema&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Schema> getProfileSchemaWithHttpInfo() throws RestClientException {
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

        ParameterizedTypeReference<Schema> localReturnType = new ParameterizedTypeReference<Schema>() {};
        return apiClient.invokeAPI("/idp/myaccount/profile/schema", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Replace my User Profile
     * Replaces the caller&#39;s user profile  &gt; **Note:** This API differs from the the existing [Users API](https://developer.okta.com/docs/reference/api/users/) in that only the PUT operation is supported.  Partial updates (PATCH requests) aren&#39;t available. All values returned by fetching a user profile must pass to the MyAccount API, or the update doesn&#39;t pass validation. This applies even if the omitted schema property is optional. To ensure an optional property passes, enter a value of &#39;null&#39;.
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * @param replaceProfileRequest  (optional)
     * @return Profile
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Profile replaceProfile(ReplaceProfileRequest replaceProfileRequest) throws RestClientException {
        return replaceProfileWithHttpInfo(replaceProfileRequest).getBody();
    }

    /**
     * Replace my User Profile
     * Replaces the caller&#39;s user profile  &gt; **Note:** This API differs from the the existing [Users API](https://developer.okta.com/docs/reference/api/users/) in that only the PUT operation is supported.  Partial updates (PATCH requests) aren&#39;t available. All values returned by fetching a user profile must pass to the MyAccount API, or the update doesn&#39;t pass validation. This applies even if the omitted schema property is optional. To ensure an optional property passes, enter a value of &#39;null&#39;.
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * @param replaceProfileRequest  (optional)
     * @return ResponseEntity&lt;Profile&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Profile> replaceProfileWithHttpInfo(ReplaceProfileRequest replaceProfileRequest) throws RestClientException {
        Object localVarPostBody = replaceProfileRequest;
        

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

        ParameterizedTypeReference<Profile> localReturnType = new ParameterizedTypeReference<Profile>() {};
        return apiClient.invokeAPI("/idp/myaccount/profile", HttpMethod.PUT, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
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
