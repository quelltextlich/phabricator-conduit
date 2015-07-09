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
import java.util.Map;

import at.quelltextlich.phabricator.conduit.ConduitException;
import at.quelltextlich.phabricator.conduit.bare.Connection;

import com.google.gson.JsonElement;

/**
 * Module for Conduit methods starting in 'phid.'
 */
public class PhidModule extends Module {
  public PhidModule(final Connection connection,
      final SessionHandler sessionHandler) {
    super(connection, sessionHandler);
  }

  /**
   * Runs the API's 'phid.lookup' method
   */
  public LookupResult lookup(final Iterable<String> names)
      throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);
    params.put("names", names);

    final JsonElement callResult = connection.call("phid.lookup", params);
    final LookupResult result = gson.fromJson(callResult, LookupResult.class);
    return result;
  }

  /**
   * Models the result for a call to phid.lookup
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "T85": {
   *     "phid": "PHID-TASK-bto8xi3333rmlvrqdzr7",
   *     "uri": "https:\/\/phab.local\/T85",
   *     "typeName": "Task",
   *     "type": "TASK",
   *     "name": "T85",
   *     "fullName": "T85: qchris-test-task",
   *     "status": "open"
   *   },
   *   "T84": {
   *     "phid": "PHID-TASK-jpnuseiiujvw6f7vvnfp",
   *     "uri": "https:\/\/phab.local\/T84",
   *     "typeName": "Task",
   *     "type": "TASK",
   *     "name": "T84",
   *     "fullName": "T84: Test task for my testproject",
   *     "status": "closed"
   *   }
   * }
   * </pre>
   */
  public static class LookupResult extends PhidResult {
    private static final long serialVersionUID = 1L;
  }

  public static class PhidResult extends HashMap<String, SinglePhidResult> {
    private static final long serialVersionUID = 1L;
  }

  public static class SinglePhidResult {
    private final String phid;
    private final String uri;
    private final String typeName;
    private final String type;
    private final String name;
    private final String fullName;
    private final String status;

    public SinglePhidResult(final String phid, final String uri,
        final String typeName, final String type, final String name,
        final String fullName, final String status) {
      super();
      this.phid = phid;
      this.uri = uri;
      this.typeName = typeName;
      this.type = type;
      this.name = name;
      this.fullName = fullName;
      this.status = status;
    }

    public String getPhid() {
      return phid;
    }

    public String getUri() {
      return uri;
    }

    public String getTypeName() {
      return typeName;
    }

    public String getType() {
      return type;
    }

    public String getName() {
      return name;
    }

    public String getFullName() {
      return fullName;
    }

    public String getStatus() {
      return status;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((phid == null) ? 0 : phid.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
      final SinglePhidResult other = (SinglePhidResult) obj;
      if (fullName == null) {
        if (other.fullName != null) {
          return false;
        }
      } else if (!fullName.equals(other.fullName)) {
        return false;
      }
      if (name == null) {
        if (other.name != null) {
          return false;
        }
      } else if (!name.equals(other.name)) {
        return false;
      }
      if (phid == null) {
        if (other.phid != null) {
          return false;
        }
      } else if (!phid.equals(other.phid)) {
        return false;
      }
      if (status == null) {
        if (other.status != null) {
          return false;
        }
      } else if (!status.equals(other.status)) {
        return false;
      }
      if (type == null) {
        if (other.type != null) {
          return false;
        }
      } else if (!type.equals(other.type)) {
        return false;
      }
      if (typeName == null) {
        if (other.typeName != null) {
          return false;
        }
      } else if (!typeName.equals(other.typeName)) {
        return false;
      }
      if (uri == null) {
        if (other.uri != null) {
          return false;
        }
      } else if (!uri.equals(other.uri)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "SinglePhidResult [phid=" + phid + ", uri=" + uri + ", typeName="
          + typeName + ", type=" + type + ", name=" + name + ", fullName="
          + fullName + ", status=" + status + "]";
    }
  }

  /**
   * Runs the API's 'phid.query' method
   */
  public QueryResult query(final Iterable<String> phids)
      throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);
    params.put("phids", phids);

    final JsonElement callResult = connection.call("phid.query", params);
    final QueryResult result = gson.fromJson(callResult, QueryResult.class);
    return result;
  }

  /**
   * Models the result for a call to phid.query
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "PHID-TASK-bto8xi3333rmlvrqdzr7": {
   *     "phid": "PHID-TASK-bto8xi3333rmlvrqdzr7",
   *     "uri": "https:\/\/phab.local\/T85",
   *     "typeName": "Task",
   *     "type": "TASK",
   *     "name": "T85",
   *     "fullName": "T85: qchris-test-task",
   *     "status": "open"
   *   },
   *   "PHID-TASK-jpnuseiiujvw6f7vvnfp": {
   *     "phid": "PHID-TASK-jpnuseiiujvw6f7vvnfp",
   *     "uri": "https:\/\/phab.local\/T84",
   *     "typeName": "Task",
   *     "type": "TASK",
   *     "name": "T84",
   *     "fullName": "T84: Test task for my testproject",
   *     "status": "closed"
   *   }
   * }
   * </pre>
   */
  public static class QueryResult extends PhidResult {
    private static final long serialVersionUID = 1L;
  }
}
