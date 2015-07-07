// Copyright (C) 2015 quelltextlich e.U.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package at.quelltextlich.phabricator.conduit.bare;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.quelltextlich.phabricator.conduit.ConduitErrorException;
import at.quelltextlich.phabricator.conduit.ConduitException;
import at.quelltextlich.phabricator.conduit.raw.Conduit;
import at.quelltextlich.phabricator.conduit.results.CallCapsule;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Abstracts the connection to Conduit API
 */
public class Connection {
  private static final Logger log = LoggerFactory.getLogger(Conduit.class);

  private final String apiUrlBase;
  private final Gson gson;

  private CloseableHttpClient client;

  public Connection(final String baseUrl) {
    apiUrlBase = baseUrl.replaceAll("/+$", "") + "/api/";
    gson = new Gson();
    client = null;
  }

  /**
   * Gives a cached HttpClient
   * <p/>
   * If no cached HttpClient exists, a new one is spawned.
   *
   * @return the cached CloseableHttpClient
   */
  private CloseableHttpClient getClient() {
    if (client == null) {
      log.trace("Creating new client connection");
      client = HttpClients.createDefault();
    }
    return client;
  }

  /**
   * Call the given Conduit method without parameters
   *
   * @param method
   *          The name of the method that should get called
   * @return The call's result, if there has been no error
   * @throws Exception
   */
  public JsonElement call(final String method) throws ConduitException {
    return call(method, new HashMap<String, Object>());
  }

  /**
   * Calls a conduit method with some parameters
   *
   * @param method
   *          The name of the method that should get called
   * @param params
   *          A map of parameters to pass to the call
   * @return The call's result, if there has been no error
   * @throws Exception
   */
  public JsonElement call(final String method, final Map<String, Object> params)
      throws ConduitException {
    final String methodUrl = apiUrlBase + method;

    final HttpPost httppost = new HttpPost(methodUrl);

    final String json = gson.toJson(params);

    log.trace("Calling phabricator method " + method + " with the parameters "
        + json);
    httppost.setEntity(new StringEntity("params=" + json,
        StandardCharsets.UTF_8));

    CloseableHttpResponse response;
    try {
      response = getClient().execute(httppost);
    } catch (final IOException e) {
      throw new ConduitException("Could not execute Phabricator API call", e);
    }
    try {
      log.trace("Phabricator HTTP response status: " + response.getStatusLine());
      final HttpEntity entity = response.getEntity();
      String entityString;
      try {
        entityString = EntityUtils.toString(entity);
      } catch (final IOException e) {
        throw new ConduitException("Could not read the API response", e);
      }

      log.trace("Phabricator response " + entityString);
      final CallCapsule callCapsule = gson.fromJson(entityString,
          CallCapsule.class);
      log.trace("callCapsule.result: " + callCapsule.getResult());
      log.trace("callCapsule.error_code: " + callCapsule.getErrorCode());
      log.trace("callCapsule.error_info: " + callCapsule.getErrorInfo());
      if (callCapsule.getErrorCode() != null
          || callCapsule.getErrorInfo() != null) {
        throw new ConduitErrorException(method, callCapsule.getErrorCode(),
            callCapsule.getErrorInfo());
      }
      return callCapsule.getResult();
    } finally {
      try {
        response.close();
      } catch (final IOException e) {
        throw new ConduitException("Could not close API response", e);
      }
    }
  }
}