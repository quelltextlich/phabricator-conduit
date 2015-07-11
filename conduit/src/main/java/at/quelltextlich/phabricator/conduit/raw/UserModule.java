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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.quelltextlich.phabricator.conduit.ConduitException;
import at.quelltextlich.phabricator.conduit.bare.Connection;

import com.google.gson.JsonElement;

/**
 * Module for Conduit methods starting in 'project.'
 */
public class UserModule extends Module {
  public UserModule(final Connection connection,
      final SessionHandler sessionHandler) {
    super(connection, sessionHandler);
  }

  /**
   * Runs the API's 'user.whoami' method
   */
  public WhoAmIResult whoAmI() throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);

    final JsonElement callResult = connection.call("user.whoami", params);
    final WhoAmIResult result = gson.fromJson(callResult, WhoAmIResult.class);
    return result;
  }

  /**
   * Models the result for a call to 'user.whoami'
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "phid": "PHID-USER-3nphm6xkw2mpyfshq4dq",
   *   "userName": "qchris",
   *   "realName": "",
   *   "image": "https:\/\/phabricator.local\/res\/phabricator\/3eb28cd9\/rsrc\/image\/avatar.png",
   *   "uri": "https:\/\/phabricator.local\/p\/qchris\/",
   *   "roles": [
   *     "unverified",
   *     "approved",
   *     "activated"
   *   ],
   *   "primaryEmail": null
   * }
   * </pre>
   */
  public static class WhoAmIResult {
    private final String phid;
    private final String userName;
    private final String realName;
    private final String image;
    private final String uri;
    private final List<String> roles;

    public WhoAmIResult(final String phid, final String userName,
        final String realName, final String image, final String uri,
        final List<String> roles) {
      super();
      this.phid = phid;
      this.userName = userName;
      this.realName = realName;
      this.image = image;
      this.uri = uri;
      this.roles = roles;
    }

    public String getPhid() {
      return phid;
    }

    public String getUserName() {
      return userName;
    }

    public String getRealName() {
      return realName;
    }

    public String getImage() {
      return image;
    }

    public String getUri() {
      return uri;
    }

    public List<String> getRoles() {
      return roles;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((image == null) ? 0 : image.hashCode());
      result = prime * result + ((phid == null) ? 0 : phid.hashCode());
      result = prime * result + ((realName == null) ? 0 : realName.hashCode());
      result = prime * result + ((roles == null) ? 0 : roles.hashCode());
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
      result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
      final WhoAmIResult other = (WhoAmIResult) obj;
      if (image == null) {
        if (other.image != null) {
          return false;
        }
      } else if (!image.equals(other.image)) {
        return false;
      }
      if (phid == null) {
        if (other.phid != null) {
          return false;
        }
      } else if (!phid.equals(other.phid)) {
        return false;
      }
      if (realName == null) {
        if (other.realName != null) {
          return false;
        }
      } else if (!realName.equals(other.realName)) {
        return false;
      }
      if (roles == null) {
        if (other.roles != null) {
          return false;
        }
      } else if (!roles.equals(other.roles)) {
        return false;
      }
      if (uri == null) {
        if (other.uri != null) {
          return false;
        }
      } else if (!uri.equals(other.uri)) {
        return false;
      }
      if (userName == null) {
        if (other.userName != null) {
          return false;
        }
      } else if (!userName.equals(other.userName)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "WhoAmIResult [phid=" + phid + ", userName=" + userName
          + ", realName=" + realName + ", image=" + image + ", uri=" + uri
          + ", roles=" + roles + "]";
    }
  }
}
