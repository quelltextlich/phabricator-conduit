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
 * Module for Conduit methods starting in 'maniphest.'
 */
public class ManiphestModule extends Module {
  public ManiphestModule(final Connection connection,
      final SessionHandler sessionHandler) {
    super(connection, sessionHandler);
  }

  /**
   * Runs the API's 'maniphest.Info' method
   */
  public InfoResult info(final int taskId) throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);
    params.put("task_id", taskId);

    final JsonElement callResult = connection.call("maniphest.info", params);
    final InfoResult result = gson.fromJson(callResult, InfoResult.class);
    return result;
  }

  /**
   * Models the result for a call to maniphest.info
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "id":"48",
   *   "phid":"PHID-TASK-pemd324eosnymq3tdkyo",
   *   "authorPHID":"PHID-USER-na3one2sht11aone",
   *   "ownerPHID":null,
   *   "ccPHIDs":[
   *     "PHID-USER-h4n62fq2kt2v3a2qjyqh"
   *   ],
   *   "status":"open",
   *   "statusName":"Open",
   *   "isClosed":false,
   *   "priority": "Needs Triage",
   *   "priorityColor":"violet",
   *   "title":"QChris test task",
   *   "description":"",
   *   "projectPHIDs":[],
   *   "uri":"https://phabricator.local/T47",
   *   "auxiliary":{
   *     "std:maniphest:security_topic":"default",
   *     "isdc:sprint:storypoints":null
   *   },
   *   "objectName":"T47",
   *   "dateCreated":"1413484594",
   *   "dateModified":1413549869,
   *   "dependsOnTaskPHIDs":[]
   * }
   * </pre>
   */
  public static class InfoResult extends TaskResult {
  }

  /**
   * Runs the API's 'maniphest.update' method
   */
  public UpdateResult update(final int taskId, final String comment)
      throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);
    params.put("id", taskId);
    params.put("comments", comment);

    final JsonElement callResult = connection.call("maniphest.update", params);
    final UpdateResult result = gson.fromJson(callResult,
        UpdateResult.class);
    return result;
  }

  /**
   * Models the result for a call to maniphest.update
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "id":"48",
   *   "phid":"PHID-TASK-pemd324eosnymq3tdkyo",
   *   "authorPHID":"PHID-USER-na3one2sht11aone",
   *   "ownerPHID":null,
   *   "ccPHIDs":[
   *     "PHID-USER-h4n62fq2kt2v3a2qjyqh"
   *   ],
   *   "status":"open",
   *   "statusName":"Open",
   *   "isClosed":false,
   *   "priority": "Needs Triage",
   *   "priorityColor":"violet",
   *   "title":"QChris test task",
   *   "description":"",
   *   "projectPHIDs":[],
   *   "uri":"https://phabricator.local/T47",
   *   "auxiliary":{
   *     "std:maniphest:security_topic":"default",
   *     "isdc:sprint:storypoints":null
   *   },
   *   "objectName":"T47",
   *   "dateCreated":"1413484594",
   *   "dateModified":1413549869,
   *   "dependsOnTaskPHIDs":[]
   * }
   * </pre>
   */
  public static class UpdateResult extends TaskResult {
  }

  /**
   * Models the result for API methods returning TaskResult information
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "id":"48",
   *   "phid":"PHID-TASK-pemd324eosnymq3tdkyo",
   *   "authorPHID":"PHID-USER-na3one2sht11aone",
   *   "ownerPHID":null,
   *   "ccPHIDs":[
   *     "PHID-USER-h4n62fq2kt2v3a2qjyqh"
   *   ],
   *   "status":"open",
   *   "statusName":"Open",
   *   "isClosed":false,
   *   "priority": "Needs Triage",
   *   "priorityColor":"violet",
   *   "title":"QChris test task",
   *   "description":"",
   *   "projectPHIDs":[],
   *   "uri":"https://phabricator.local/T47",
   *   "auxiliary":{
   *     "std:maniphest:security_topic":"default",
   *     "isdc:sprint:storypoints":null
   *   },
   *   "objectName":"T47",
   *   "dateCreated":"1413484594",
   *   "dateModified":1413549869,
   *   "dependsOnTaskPHIDs":[]
   * }
   * </pre>
   */
  public static class TaskResult {
    private int id;
    private String phid;
    private String authorPHID;
    private String ownerPHID;
    private JsonElement ccPHIDs;
    private String status;
    private String statusName;
    private Boolean isClosed;
    private String priority;
    private String priorityColor;
    private String title;
    private String description;
    private JsonElement projectPHIDs;
    private String uri;
    private JsonElement auxiliary;
    private String objectName;
    private String dateCreated;
    private String dateModified;
    private JsonElement dependsOnTaskPHIDs;

    public int getId() {
      return id;
    }

    public String getPhid() {
      return phid;
    }

    public String getAuthorPHID() {
      return authorPHID;
    }

    public String getOwnerPHID() {
      return ownerPHID;
    }

    public JsonElement getCcPHIDs() {
      return ccPHIDs;
    }

    public String getStatus() {
      return status;
    }

    public String getStatusName() {
      return statusName;
    }

    public Boolean getIsClosed() {
      return isClosed;
    }

    public String getPriority() {
      return priority;
    }

    public String getPriorityColor() {
      return priorityColor;
    }

    public String getTitle() {
      return title;
    }

    public String getDescription() {
      return description;
    }

    public JsonElement getProjectPHIDs() {
      return projectPHIDs;
    }

    public String getUri() {
      return uri;
    }

    public JsonElement getAuxiliary() {
      return auxiliary;
    }

    public String getObjectName() {
      return objectName;
    }

    public String getDateCreated() {
      return dateCreated;
    }

    public String getDateModified() {
      return dateModified;
    }

    public JsonElement getDependsOnTaskPHIDs() {
      return dependsOnTaskPHIDs;
    }
  }
}
