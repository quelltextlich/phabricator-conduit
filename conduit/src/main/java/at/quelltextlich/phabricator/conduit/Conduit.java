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

package at.quelltextlich.phabricator.conduit;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.quelltextlich.phabricator.conduit.results.ConduitConnect;
import at.quelltextlich.phabricator.conduit.results.ConduitPing;
import at.quelltextlich.phabricator.conduit.results.ManiphestInfo;
import at.quelltextlich.phabricator.conduit.results.ManiphestUpdate;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Bindings for Phabricator's Conduit API
 * <p/>
 * This class is not thread-safe.
 */
public class Conduit implements SessionHandler {

  private static final Logger log = LoggerFactory.getLogger(Conduit.class);

  public static final int CONDUIT_VERSION = 1;

  private final Connection connection;
  private final Gson gson;

  private String username;
  private String certificate;

  private String sessionKey;

  public Conduit(final String baseUrl) {
    this(baseUrl, null, null);
  }

  public Conduit(final String baseUrl, final String username,
      final String certificate) {
    connection = new Connection(baseUrl);
    this.username = username;
    this.certificate = certificate;
    gson = new Gson();
    resetSession();
  }

  private void resetSession() {
    sessionKey = null;
  }

  public void setUsername(final String username) {
    this.username = username;
    resetSession();
  }

  public void setCertificate(final String certificate) {
    this.certificate = certificate;
    resetSession();
  }

  /**
   * Adds session parameters to a Map of parameters
   * <p/>
   * If there is no active session, a new one is opened
   * <p/>
   * This method overrides the params' __conduit__ value.
   *
   * @param params
   *          The Map to add session paramaters to
   */
  @Override
  public void fillInSession(final Map<String, Object> params)
      throws ConduitException {
    if (sessionKey == null) {
      log.debug("Trying to start new session");
      conduitConnect();
    }
    final Map<String, Object> conduitParams = new HashMap<String, Object>();
    conduitParams.put("sessionKey", sessionKey);
    params.put("__conduit__", conduitParams);
  }

  /**
   * Runs the API's 'conduit.ping' method
   */
  public ConduitPing conduitPing() throws ConduitException {
    final JsonElement callResult = connection.call("conduit.ping");
    final JsonObject callResultWrapper = new JsonObject();
    callResultWrapper.add("hostname", callResult);
    final ConduitPing result = gson.fromJson(callResultWrapper,
        ConduitPing.class);
    return result;
  }

  /**
   * Runs the API's 'conduit.connect' method
   */
  public ConduitConnect conduitConnect() throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    params.put("client", "at.quelltextlich.phabricator:phabricator-conduit");
    params.put("clientVersion", CONDUIT_VERSION);
    params.put("user", username);

    // According to
    // phabricator/src/applications/conduit/method/ConduitConnectConduitAPIMethod.php,
    // the authToken needs to be an integer that is within 15 minutes of the
    // server's current timestamp.
    final long authToken = System.currentTimeMillis() / 1000;
    params.put("authToken", authToken);

    // According to
    // phabricator/src/applications/conduit/method/ConduitConnectConduitAPIMethod.php,
    // The signature is the SHA1 of the concatenation of the authToken (as
    // string) and the certificate (The long sequence of digits and
    // lowercase
    // hat get written into ~/.arcrc after "arc install-certificate").
    final String authSignatureInput = Long.toString(authToken) + certificate;

    MessageDigest sha1;
    try {
      sha1 = MessageDigest.getInstance("SHA-1");
    } catch (final NoSuchAlgorithmException e) {
      throw new ConduitException("Failed to compute authSignature, as no "
          + "SHA-1 algorithm implementation was found", e);
    }
    byte[] authSignatureRaw;
    try {
      authSignatureRaw = sha1.digest(authSignatureInput.getBytes("UTF-8"));
    } catch (final UnsupportedEncodingException e) {
      throw new ConduitException("Failed to convert authSignature input to "
          + "UTF-8 String", e);
    }
    final String authSignatureUC = DatatypeConverter
        .printHexBinary(authSignatureRaw);
    final String authSignature = authSignatureUC.toLowerCase();
    params.put("authSignature", authSignature);

    final JsonElement callResult = connection.call("conduit.connect", params);

    final ConduitConnect result = gson.fromJson(callResult,
        ConduitConnect.class);
    sessionKey = result.getSessionKey();
    return result;
  }

  /**
   * Runs the API's 'maniphest.Info' method
   */
  public ManiphestInfo maniphestInfo(final int taskId) throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    fillInSession(params);
    params.put("task_id", taskId);

    final JsonElement callResult = connection.call("maniphest.info", params);
    final ManiphestInfo result = gson.fromJson(callResult, ManiphestInfo.class);
    return result;
  }

  /**
   * Runs the API's 'maniphest.update' method
   */
  public ManiphestUpdate maniphestUpdate(final int taskId, final String comment)
      throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    fillInSession(params);
    params.put("id", taskId);
    params.put("comments", comment);

    final JsonElement callResult = connection.call("maniphest.update", params);
    final ManiphestUpdate result = gson.fromJson(callResult,
        ManiphestUpdate.class);
    return result;
  }
}
