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
import com.google.gson.annotations.SerializedName;

/**
 * Module for Conduit methods starting in 'project.'
 */
public class ProjectModule extends Module {
  public ProjectModule(final Connection connection,
      final SessionHandler sessionHandler) {
    super(connection, sessionHandler);
  }

  /**
   * Runs the API's 'project.create' method
   */
  public CreateResult create(final String name, final List<String> members,
      final String icon, final String color, final List<String> tags)
      throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);
    params.put("name", name);
    params.put("members", members);
    params.put("icon", icon);
    params.put("color", color);
    params.put("tags", tags);

    final JsonElement callResult = connection.call("project.create", params);
    final CreateResult result = gson.fromJson(callResult, CreateResult.class);
    return result;
  }

  /**
   * Models the result for a call to 'project.create'
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "id": 21,
   *   "phid": "PHID-PROJ-nwyto7vdzbt64o2oo5g4",
   *   "name": "qchris-test",
   *   "profileImagePHID": null,
   *   "icon": "briefcase",
   *   "color": "blue",
   *   "members": [
   *     "PHID-USER-3nphm6xkw2mpyfshq4dq"
   *   ],
   *   "slugs": [],
   *   "dateCreated": 1436622360,
   *   "dateModified": 1436622360
   * }
   * </pre>
   */
  public static class CreateResult extends ProjectResult {
    public CreateResult(final int id, final String phid, final String name,
        final String profileImagePhid, final String icon, final String color,
        final List<String> members, final List<String> slugs,
        final String dateCreated, final String dateModified) {
      super(id, phid, name, profileImagePhid, icon, color, members, slugs,
          dateCreated, dateModified);
    }
  }

  public static class ProjectResult {
    private final int id;
    private final String phid;
    private final String name;
    @SerializedName("profileImagePHID")
    private final String profileImagePhid;
    private final String icon;
    private final String color;
    private final List<String> members;
    private final List<String> slugs;
    private final String dateCreated;
    private final String dateModified;

    public ProjectResult(final int id, final String phid, final String name,
        final String profileImagePhid, final String icon, final String color,
        final List<String> members, final List<String> slugs,
        final String dateCreated, final String dateModified) {
      super();
      this.id = id;
      this.phid = phid;
      this.name = name;
      this.profileImagePhid = profileImagePhid;
      this.icon = icon;
      this.color = color;
      this.members = members;
      this.slugs = slugs;
      this.dateCreated = dateCreated;
      this.dateModified = dateModified;
    }

    public int getId() {
      return id;
    }

    public String getPhid() {
      return phid;
    }

    public String getName() {
      return name;
    }

    public String getProfileImagePhid() {
      return profileImagePhid;
    }

    public String getIcon() {
      return icon;
    }

    public String getColor() {
      return color;
    }

    public List<String> getMembers() {
      return members;
    }

    public List<String> getSlugs() {
      return slugs;
    }

    public String getDateCreated() {
      return dateCreated;
    }

    public String getDateModified() {
      return dateModified;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((color == null) ? 0 : color.hashCode());
      result = prime * result
          + ((dateCreated == null) ? 0 : dateCreated.hashCode());
      result = prime * result
          + ((dateModified == null) ? 0 : dateModified.hashCode());
      result = prime * result + ((icon == null) ? 0 : icon.hashCode());
      result = prime * result + id;
      result = prime * result + ((members == null) ? 0 : members.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((phid == null) ? 0 : phid.hashCode());
      result = prime * result
          + ((profileImagePhid == null) ? 0 : profileImagePhid.hashCode());
      result = prime * result + ((slugs == null) ? 0 : slugs.hashCode());
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
      final ProjectResult other = (ProjectResult) obj;
      if (color == null) {
        if (other.color != null) {
          return false;
        }
      } else if (!color.equals(other.color)) {
        return false;
      }
      if (dateCreated == null) {
        if (other.dateCreated != null) {
          return false;
        }
      } else if (!dateCreated.equals(other.dateCreated)) {
        return false;
      }
      if (dateModified == null) {
        if (other.dateModified != null) {
          return false;
        }
      } else if (!dateModified.equals(other.dateModified)) {
        return false;
      }
      if (icon == null) {
        if (other.icon != null) {
          return false;
        }
      } else if (!icon.equals(other.icon)) {
        return false;
      }
      if (id != other.id) {
        return false;
      }
      if (members == null) {
        if (other.members != null) {
          return false;
        }
      } else if (!members.equals(other.members)) {
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
      if (profileImagePhid == null) {
        if (other.profileImagePhid != null) {
          return false;
        }
      } else if (!profileImagePhid.equals(other.profileImagePhid)) {
        return false;
      }
      if (slugs == null) {
        if (other.slugs != null) {
          return false;
        }
      } else if (!slugs.equals(other.slugs)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "ProjectResult [id=" + id + ", phid=" + phid + ", name=" + name
          + ", profileImagePhid=" + profileImagePhid + ", icon=" + icon
          + ", color=" + color + ", members=" + members + ", slugs=" + slugs
          + ", dateCreated=" + dateCreated + ", dateModified=" + dateModified
          + "]";
    }
  }
}
