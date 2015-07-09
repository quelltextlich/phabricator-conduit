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
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import at.quelltextlich.phabricator.conduit.ConduitException;
import at.quelltextlich.phabricator.conduit.bare.Connection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

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
  public PingResult ping() throws ConduitException {
    final JsonElement callResult = connection.call("conduit.ping");
    final JsonObject callResultWrapper = new JsonObject();
    callResultWrapper.add("hostname", callResult);
    final PingResult result = gson
        .fromJson(callResultWrapper, PingResult.class);
    return result;
  }

  /**
   * Models the result for a call to conduit.ping
   * <p/>
   * JSON is just the hostname of the instance. We wrap it in a proper object to
   * make it a nicer Java citizen.
   */
  public static class PingResult {
    private final String hostname;

    public PingResult(final String hostname) {
      super();
      this.hostname = hostname;
    }

    public String getHostname() {
      return hostname;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final PingResult other = (PingResult) obj;
      if (hostname == null) {
        if (other.hostname != null) {
          return false;
        }
      } else if (!hostname.equals(other.hostname)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "PingResult [hostname=" + hostname + "]";
    }
  }

  /**
   * Runs the API's 'conduit.connect' method
   */
  public ConnectResult connect() throws ConduitException {
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

    final ConnectResult result = gson.fromJson(callResult, ConnectResult.class);
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
  public static class ConnectResult {
    private final int connectionID;
    private final String sessionKey;
    private final String userPHID;

    public ConnectResult(final int connectionID, final String sessionKey,
        final String userPHID) {
      super();
      this.connectionID = connectionID;
      this.sessionKey = sessionKey;
      this.userPHID = userPHID;
    }

    public int getConnectionId() {
      return connectionID;
    }

    public String getSessionKey() {
      return sessionKey;
    }

    public String getUserPhId() {
      return userPHID;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + connectionID;
      result = prime * result
          + ((sessionKey == null) ? 0 : sessionKey.hashCode());
      result = prime * result + ((userPHID == null) ? 0 : userPHID.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final ConnectResult other = (ConnectResult) obj;
      if (connectionID != other.connectionID) {
        return false;
      }
      if (sessionKey == null) {
        if (other.sessionKey != null) {
          return false;
        }
      } else if (!sessionKey.equals(other.sessionKey)) {
        return false;
      }
      if (userPHID == null) {
        if (other.userPHID != null) {
          return false;
        }
      } else if (!userPHID.equals(other.userPHID)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "ConnectResult [connectionID=" + connectionID + ", sessionKey="
          + sessionKey + ", userPHID=" + userPHID + "]";
    }
  }

  /**
   * Runs the API's 'conduit.getcapabilities' method
   */
  public GetCapabilitiesResult getCapabilities() throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();

    final JsonElement callResult = connection.call("conduit.getcapabilities",
        params);
    final GetCapabilitiesResult result = gson.fromJson(callResult,
        GetCapabilitiesResult.class);
    return result;
  }

  /**
   * Models the result for a call to conduit.getcapabilities
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "output": [
   *     "json",
   *     "human"
   *   ],
   *   "input": [
   *     "json",
   *     "urlencoded"
   *   ],
   *   "signatures": [
   *     "consign"
   *   ],
   *   "authentication": [
   *     "token",
   *     "asymmetric",
   *     "session",
   *     "sessionless",
   *     "oauth"
   *   ]
   * }
   * </pre>
   */
  public static class GetCapabilitiesResult extends
      HashMap<String, List<String>> {
    private static final long serialVersionUID = 1L;
  }

  /**
   * Runs the API's 'conduit.getcertificate' method
   *
   * @param token
   *          token to get the certificate for. This can be obtained from the
   *          '/conduit/token/' page on your Phabricator.
   * @param host
   *          The Phabricator host name
   */
  public GetCertificateResult getCertificate(final String token,
      final String host) throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    params.put("token", token);
    params.put("host", host);
    final JsonElement callResult = connection.call("conduit.getcertificate",
        params);
    final GetCertificateResult result = gson.fromJson(callResult,
        GetCertificateResult.class);
    return result;
  }

  /**
   * Models the result for a call to 'conduit.getcertificate'
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "username": "qchris",
   *   "certificate": "xn6ajw32425d5kzx26rgjt2h7g2yfbok5r7fqinmpeatj4viohnbu4ux2pzyrdz4wghlyokzr5ng4wg5gricz2k5cjruds5jg2fj363chrq3iobu7bm2nctfnauqdz5gpwxuz2n2jqaqyrxotw3uppspoho3wr27ob2xesjep7eeqcwudzwyc6o47x46yozspna4rvltr5wdoq2mmdqyfqigkrdxrcw47zoccrmutinwz7podil3h3er6g6hnq6"
   * }
   * </pre>
   */
  public static class GetCertificateResult {
    public GetCertificateResult(final String username, final String certificate) {
      super();
      this.username = username;
      this.certificate = certificate;
    }

    private final String username;
    private final String certificate;

    public String getUsername() {
      return username;
    }

    public String getCertificate() {
      return certificate;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
          + ((certificate == null) ? 0 : certificate.hashCode());
      result = prime * result + ((username == null) ? 0 : username.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final GetCertificateResult other = (GetCertificateResult) obj;
      if (certificate == null) {
        if (other.certificate != null) {
          return false;
        }
      } else if (!certificate.equals(other.certificate)) {
        return false;
      }
      if (username == null) {
        if (other.username != null) {
          return false;
        }
      } else if (!username.equals(other.username)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "GetCertificateResult [username=" + username + ", certificate="
          + certificate + "]";
    }
  }

  /**
   * Runs the API's 'conduit.query' method
   */
  public QueryResult query() throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);
    final JsonElement callResult = connection.call("conduit.query", params);
    final QueryResult result = gson.fromJson(callResult, QueryResult.class);
    return result;
  }

  /**
   * Models the result for a call to 'conduit.getcertificate'
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "almanac.querydevices": {
   *     "description": "Query Almanac devices.",
   *     "params": {
   *       "ids": "optional list<id>",
   *       "phids": "optional list<phid>",
   *       "names": "optional list<phid>",
   *       "before": "optional string",
   *       "after": "optional string",
   *       "limit": "optional int (default = 100)"
   *     },
   *     "return": "list<wild>"
   *   },
   *   "almanac.queryservices": {
   *     "description": "Query Almanac services.",
   *     "params": {
   *       "ids": "optional list<id>",
   *       "phids": "optional list<phid>",
   *       "names": "optional list<phid>",
   *       "devicePHIDs": "optional list<phid>",
   *       "serviceClasses": "optional list<string>",
   *       "before": "optional string",
   *       "after": "optional string",
   *       "limit": "optional int (default = 100)"
   *     },
   *     "return": "list<wild>"
   *   },
   *   [...]
   * }
   * </pre>
   */
  public static class QueryResult extends HashMap<String, SingleQueryResult> {
    private static final long serialVersionUID = 1L;
  }

  public static class SingleQueryResult {
    String description;
    Map<String, String> params;
    @SerializedName("return")
    String _return;

    public SingleQueryResult(final String description,
        final Map<String, String> params, final String _return) {
      super();
      this.description = description;
      this.params = params;
      this._return = _return;
    }

    public String getReturn() {
      return _return;
    }

    public String getDescription() {
      return description;
    }

    public Map<String, String> getParams() {
      return params;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_return == null) ? 0 : _return.hashCode());
      result = prime * result
          + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((params == null) ? 0 : params.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final SingleQueryResult other = (SingleQueryResult) obj;
      if (_return == null) {
        if (other._return != null) {
          return false;
        }
      } else if (!_return.equals(other._return)) {
        return false;
      }
      if (description == null) {
        if (other.description != null) {
          return false;
        }
      } else if (!description.equals(other.description)) {
        return false;
      }
      if (params == null) {
        if (other.params != null) {
          return false;
        }
      } else if (!params.equals(other.params)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "SingleQueryResult[description=" + description + ", params="
          + params + ", return=" + _return + "]";
    }
  }
}
