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
package at.quelltextlich.phabricator.conduit.raw;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import at.quelltextlich.phabricator.conduit.ConduitException;
import at.quelltextlich.phabricator.conduit.bare.Connection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Module for Conduit methods starting in 'conduit.'
 */
public class ConduitModule extends Module {
  public static final int CONDUIT_VERSION = 1;

  private final String username;
  private final String certificate;

  public ConduitModule(final Connection connection,
      final SessionHandler sessionHandler, final String username,
      final String certificate) {
    super(connection, sessionHandler);
    this.username = username;
    this.certificate = certificate;
  }

  /**
   * Runs the API's 'conduit.ping' method
   */
  public ConduitPing ping() throws ConduitException {
    final JsonElement callResult = connection.call("conduit.ping");
    final JsonObject callResultWrapper = new JsonObject();
    callResultWrapper.add("hostname", callResult);
    final ConduitPing result = gson.fromJson(callResultWrapper,
        ConduitPing.class);
    return result;
  }

  /**
   * Models the result for a call to conduit.ping
   * <p/>
   * JSON is just the hostname of the instance. We wrap it in a proper object to
   * make it a nicer Java citizen.
   */
  public static class ConduitPing {
    private String hostname;

    public String getHostname() {
      return hostname;
    }
  }

  /**
   * Runs the API's 'conduit.connect' method
   */
  public ConduitConnect connect() throws ConduitException {
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
    return result;
  }

  /**
   * Models the result for a call to conduit.connect
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "connectionID":4,
   *   "sessionKey":"5jhpmsb3xgm1eupp7snzym7mtebndd7v4vv4ub6n",
   *   "userPHID":"PHID-USER-h4n52fq2kt2v3a2qjyqh"
   * }
   * </pre>
   */
  public static class ConduitConnect {
    private int connectionID;
    private String sessionKey;
    private String userPHID;

    public int getConnectionId() {
      return connectionID;
    }

    public String getSessionKey() {
      return sessionKey;
    }

    public void setSessionKey(final String sessionKey) {
      this.sessionKey = sessionKey;
    }

    public String getUserPhId() {
      return userPHID;
    }
  }
}
