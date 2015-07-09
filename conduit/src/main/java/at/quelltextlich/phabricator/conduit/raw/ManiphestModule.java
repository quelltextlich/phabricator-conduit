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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.quelltextlich.phabricator.conduit.ConduitException;
import at.quelltextlich.phabricator.conduit.bare.Connection;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Module for Conduit methods starting in 'maniphest.'
 */
public class ManiphestModule extends Module {
  public ManiphestModule(final Connection connection,
      final SessionHandler sessionHandler) {
    super(connection, sessionHandler);
  }

  /**
   * Runs the API's 'maniphest.createtask' method
   */
  public CreateTaskResult createTask(final String title,
      final String description, final String ownerPhid,
      final String viewPolicy, final String editPolicy,
      final List<String> ccPhids, final Integer priority,
      final List<String> projectPhids, final Map<String, String> auxiliary)
      throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);
    params.put("title", title);
    params.put("description", description);
    params.put("ownerPHID", ownerPhid);
    params.put("viewPolicy", viewPolicy);
    params.put("editPolicy", editPolicy);
    params.put("ccPHIDs", ccPhids);
    params.put("priority", priority);
    params.put("projectPHIDs", projectPhids);
    params.put("auxiliary", auxiliary);

    final JsonElement callResult = connection.call("maniphest.createtask",
        params);
    final CreateTaskResult result = gson.fromJson(callResult,
        CreateTaskResult.class);
    return result;
  }

  /**
   * Models the result for a call to 'maniphest.createtask'
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
  public static class CreateTaskResult extends TaskResult {
    public CreateTaskResult(final int id, final String phid,
        final String authorPHID, final String ownerPHID,
        final List<String> ccPHIDs, final String status,
        final String statusName, final Boolean isClosed, final String priority,
        final String priorityColor, final String title,
        final String description, final List<String> projectPHIDs,
        final String uri, final Map<String, String> auxiliary,
        final String objectName, final String dateCreated,
        final String dateModified, final List<String> dependsOnTaskPHIDs) {
      super(id, phid, authorPHID, ownerPHID, ccPHIDs, status, statusName,
          isClosed, priority, priorityColor, title, description, projectPHIDs,
          uri, auxiliary, objectName, dateCreated, dateModified,
          dependsOnTaskPHIDs);
    }
  }

  /**
   * Runs the API's 'maniphest.getTaskTransactions' method
   */
  public GetTaskTransactionsResult getTaskTransactions(final List<Integer> ids)
      throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);
    params.put("ids", ids);

    final JsonElement callResult = connection.call(
        "maniphest.gettasktransactions", params);
    final GetTaskTransactionsResult result = gson.fromJson(callResult,
        GetTaskTransactionsResult.class);
    return result;
  }

  /**
   * Models the result for a call to 'maniphest.gettasktransactions'
   * <p/>
   * JSON looks like:
   *
   * <pre>
   * {
   *   "85": [
   *     {
   *       "taskID": "85",
   *       "transactionPHID": "PHID-XACT-TASK-o3ofscpf5cs3wj3",
   *       "transactionType": "core:comment",
   *       "oldValue": null,
   *       "newValue": null,
   *       "comments": "Test comment",
   *       "authorPHID": "PHID-USER-3nphm6xkw2mpyfshq4dq",
   *       "dateCreated": "1436473965"
   *     },
   *     {
   *       "taskID": "85",
   *       "transactionPHID": "PHID-XACT-TASK-7o4g3dpn6izhslq",
   *       "transactionType": "priority",
   *       "oldValue": 25,
   *       "newValue": 80,
   *       "comments": null,
   *       "authorPHID": "PHID-USER-3nphm6xkw2mpyfshq4dq",
   *       "dateCreated": "1436473961"
   *     }
   *   ],
   *   "86": [
   *     {
   *       "taskID": "86",
   *       "transactionPHID": "PHID-XACT-TASK-xhbr4gj7ca224b2",
   *       "transactionType": "priority",
   *       "oldValue": null,
   *       "newValue": 25,
   *       "comments": null,
   *       "authorPHID": "PHID-USER-3nphm6xkw2mpyfshq4dq",
   *       "dateCreated": "1436473263"
   *     },
   *     {
   *       "taskID": "86",
   *       "transactionPHID": "PHID-XACT-TASK-ghyh4ue3p4m3yqz",
   *       "transactionType": "core:subscribers",
   *       "oldValue": [],
   *       "newValue": [
   *         "PHID-USER-3nphm6xkw2mpyfshq4dq"
   *       ],
   *       "comments": null,
   *       "authorPHID": "PHID-USER-3nphm6xkw2mpyfshq4dq",
   *       "dateCreated": "1436473263"
   *     },
   *     {
   *       "taskID": "86",
   *       "transactionPHID": "PHID-XACT-TASK-icdkynz2de3wuf2",
   *       "transactionType": "status",
   *       "oldValue": null,
   *       "newValue": "open",
   *       "comments": null,
   *       "authorPHID": "PHID-USER-3nphm6xkw2mpyfshq4dq",
   *       "dateCreated": "1436473263"
   *     }
   *   ],
   * }
   * </pre>
   */
  public static class GetTaskTransactionsResult extends
      HashMap<String, SingleGetTaskTransactionsResult> {
    private static final long serialVersionUID = 1L;
  }

  public static class SingleGetTaskTransactionsResult extends
      ArrayList<TaskTransaction> {
    private static final long serialVersionUID = 1L;
  }

  public static class TaskTransaction {
    @SerializedName("taskID")
    private final String taskId;
    @SerializedName("transactionPHID")
    private final String transactionPhid;
    private final String transactionType;
    private final Object oldValue;
    private final Object newValue;
    private final String comments;
    @SerializedName("authorPHID")
    private final String authorPhid;
    private final String dateCreated;

    public TaskTransaction(final String taskId, final String transactionPhid,
        final String transactionType, final Object oldValue,
        final Object newValue, final String comments, final String authorPhid,
        final String dateCreated) {
      super();
      this.taskId = taskId;
      this.transactionPhid = transactionPhid;
      this.transactionType = transactionType;
      this.oldValue = oldValue;
      this.newValue = newValue;
      this.comments = comments;
      this.authorPhid = authorPhid;
      this.dateCreated = dateCreated;
    }

    public String getTaskId() {
      return taskId;
    }

    public String getTransactionPhid() {
      return transactionPhid;
    }

    public String getTransactionType() {
      return transactionType;
    }

    public Object getOldValue() {
      return oldValue;
    }

    public Object getNewValue() {
      return newValue;
    }

    public String getComments() {
      return comments;
    }

    public String getAuthorPhid() {
      return authorPhid;
    }

    public String getDateCreated() {
      return dateCreated;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
          + ((authorPhid == null) ? 0 : authorPhid.hashCode());
      result = prime * result + ((comments == null) ? 0 : comments.hashCode());
      result = prime * result
          + ((dateCreated == null) ? 0 : dateCreated.hashCode());
      result = prime * result + ((newValue == null) ? 0 : newValue.hashCode());
      result = prime * result + ((oldValue == null) ? 0 : oldValue.hashCode());
      result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
      result = prime * result
          + ((transactionPhid == null) ? 0 : transactionPhid.hashCode());
      result = prime * result
          + ((transactionType == null) ? 0 : transactionType.hashCode());
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
      final TaskTransaction other = (TaskTransaction) obj;
      if (authorPhid == null) {
        if (other.authorPhid != null) {
          return false;
        }
      } else if (!authorPhid.equals(other.authorPhid)) {
        return false;
      }
      if (comments == null) {
        if (other.comments != null) {
          return false;
        }
      } else if (!comments.equals(other.comments)) {
        return false;
      }
      if (dateCreated == null) {
        if (other.dateCreated != null) {
          return false;
        }
      } else if (!dateCreated.equals(other.dateCreated)) {
        return false;
      }
      if (newValue == null) {
        if (other.newValue != null) {
          return false;
        }
      } else if (!newValue.equals(other.newValue)) {
        return false;
      }
      if (oldValue == null) {
        if (other.oldValue != null) {
          return false;
        }
      } else if (!oldValue.equals(other.oldValue)) {
        return false;
      }
      if (taskId == null) {
        if (other.taskId != null) {
          return false;
        }
      } else if (!taskId.equals(other.taskId)) {
        return false;
      }
      if (transactionPhid == null) {
        if (other.transactionPhid != null) {
          return false;
        }
      } else if (!transactionPhid.equals(other.transactionPhid)) {
        return false;
      }
      if (transactionType == null) {
        if (other.transactionType != null) {
          return false;
        }
      } else if (!transactionType.equals(other.transactionType)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "TaskTransactionResult [taskId=" + taskId + ", transactionPhid="
          + transactionPhid + ", transactionType=" + transactionType
          + ", oldValue=" + oldValue + ", newValue=" + newValue + ", comments="
          + comments + ", authorPhid=" + authorPhid + ", dateCreated="
          + dateCreated + "]";
    }
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
    public InfoResult(final int id, final String phid, final String authorPHID,
        final String ownerPHID, final List<String> ccPHIDs,
        final String status, final String statusName, final Boolean isClosed,
        final String priority, final String priorityColor, final String title,
        final String description, final List<String> projectPHIDs,
        final String uri, final Map<String, String> auxiliary,
        final String objectName, final String dateCreated,
        final String dateModified, final List<String> dependsOnTaskPHIDs) {
      super(id, phid, authorPHID, ownerPHID, ccPHIDs, status, statusName,
          isClosed, priority, priorityColor, title, description, projectPHIDs,
          uri, auxiliary, objectName, dateCreated, dateModified,
          dependsOnTaskPHIDs);
    }
  }

  /**
   * Runs the API's 'maniphest.update' method
   */
  public UpdateResult update(final Integer id, final String phid,
      final String title, final String description, final String ownerPhid,
      final String viewPolicy, final String editPolicy,
      final List<String> ccPhids, final Integer priority,
      final List<String> projectPhids, final Map<String, String> auxiliary,
      final String status, final String comments) throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);
    params.put("id", id);
    params.put("phid", phid);
    params.put("title", title);
    params.put("description", description);
    params.put("ownerPHID", ownerPhid);
    params.put("viewPolicy", viewPolicy);
    params.put("editPolicy", editPolicy);
    params.put("ccPHIDs", ccPhids);
    params.put("priority", priority);
    params.put("projectPHIDs", projectPhids);
    params.put("auxiliary", auxiliary);
    params.put("status", status);
    params.put("comments", comments);

    final JsonElement callResult = connection.call("maniphest.update", params);
    final UpdateResult result = gson.fromJson(callResult, UpdateResult.class);
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
    public UpdateResult(final int id, final String phid,
        final String authorPHID, final String ownerPHID,
        final List<String> ccPHIDs, final String status,
        final String statusName, final Boolean isClosed, final String priority,
        final String priorityColor, final String title,
        final String description, final List<String> projectPHIDs,
        final String uri, final Map<String, String> auxiliary,
        final String objectName, final String dateCreated,
        final String dateModified, final List<String> dependsOnTaskPHIDs) {
      super(id, phid, authorPHID, ownerPHID, ccPHIDs, status, statusName,
          isClosed, priority, priorityColor, title, description, projectPHIDs,
          uri, auxiliary, objectName, dateCreated, dateModified,
          dependsOnTaskPHIDs);
    }
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
    private final int id;
    private final String phid;
    private final String authorPHID;
    private final String ownerPHID;
    private final List<String> ccPHIDs;
    private final String status;
    private final String statusName;
    private final Boolean isClosed;
    private final String priority;
    private final String priorityColor;
    private final String title;
    private final String description;
    private final List<String> projectPHIDs;
    private final String uri;
    private final Map<String, String> auxiliary;
    private final String objectName;
    private final String dateCreated;
    private final String dateModified;
    private final List<String> dependsOnTaskPHIDs;

    public TaskResult(final int id, final String phid, final String authorPHID,
        final String ownerPHID, final List<String> ccPHIDs,
        final String status, final String statusName, final Boolean isClosed,
        final String priority, final String priorityColor, final String title,
        final String description, final List<String> projectPHIDs,
        final String uri, final Map<String, String> auxiliary,
        final String objectName, final String dateCreated,
        final String dateModified, final List<String> dependsOnTaskPHIDs) {
      super();
      this.id = id;
      this.phid = phid;
      this.authorPHID = authorPHID;
      this.ownerPHID = ownerPHID;
      this.ccPHIDs = ccPHIDs;
      this.status = status;
      this.statusName = statusName;
      this.isClosed = isClosed;
      this.priority = priority;
      this.priorityColor = priorityColor;
      this.title = title;
      this.description = description;
      this.projectPHIDs = projectPHIDs;
      this.uri = uri;
      this.auxiliary = auxiliary;
      this.objectName = objectName;
      this.dateCreated = dateCreated;
      this.dateModified = dateModified;
      this.dependsOnTaskPHIDs = dependsOnTaskPHIDs;
    }

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

    public List<String> getCcPHIDs() {
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

    public List<String> getProjectPHIDs() {
      return projectPHIDs;
    }

    public String getUri() {
      return uri;
    }

    public Map<String, String> getAuxiliary() {
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

    public List<String> getDependsOnTaskPHIDs() {
      return dependsOnTaskPHIDs;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
          + ((authorPHID == null) ? 0 : authorPHID.hashCode());
      result = prime * result
          + ((auxiliary == null) ? 0 : auxiliary.hashCode());
      result = prime * result + ((ccPHIDs == null) ? 0 : ccPHIDs.hashCode());
      result = prime * result
          + ((dateCreated == null) ? 0 : dateCreated.hashCode());
      result = prime * result
          + ((dateModified == null) ? 0 : dateModified.hashCode());
      result = prime * result
          + ((dependsOnTaskPHIDs == null) ? 0 : dependsOnTaskPHIDs.hashCode());
      result = prime * result
          + ((description == null) ? 0 : description.hashCode());
      result = prime * result + id;
      result = prime * result + ((isClosed == null) ? 0 : isClosed.hashCode());
      result = prime * result
          + ((objectName == null) ? 0 : objectName.hashCode());
      result = prime * result
          + ((ownerPHID == null) ? 0 : ownerPHID.hashCode());
      result = prime * result + ((phid == null) ? 0 : phid.hashCode());
      result = prime * result + ((priority == null) ? 0 : priority.hashCode());
      result = prime * result
          + ((priorityColor == null) ? 0 : priorityColor.hashCode());
      result = prime * result
          + ((projectPHIDs == null) ? 0 : projectPHIDs.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result
          + ((statusName == null) ? 0 : statusName.hashCode());
      result = prime * result + ((title == null) ? 0 : title.hashCode());
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
      final TaskResult other = (TaskResult) obj;
      if (authorPHID == null) {
        if (other.authorPHID != null) {
          return false;
        }
      } else if (!authorPHID.equals(other.authorPHID)) {
        return false;
      }
      if (auxiliary == null) {
        if (other.auxiliary != null) {
          return false;
        }
      } else if (!auxiliary.equals(other.auxiliary)) {
        return false;
      }
      if (ccPHIDs == null) {
        if (other.ccPHIDs != null) {
          return false;
        }
      } else if (!ccPHIDs.equals(other.ccPHIDs)) {
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
      if (dependsOnTaskPHIDs == null) {
        if (other.dependsOnTaskPHIDs != null) {
          return false;
        }
      } else if (!dependsOnTaskPHIDs.equals(other.dependsOnTaskPHIDs)) {
        return false;
      }
      if (description == null) {
        if (other.description != null) {
          return false;
        }
      } else if (!description.equals(other.description)) {
        return false;
      }
      if (id != other.id) {
        return false;
      }
      if (isClosed == null) {
        if (other.isClosed != null) {
          return false;
        }
      } else if (!isClosed.equals(other.isClosed)) {
        return false;
      }
      if (objectName == null) {
        if (other.objectName != null) {
          return false;
        }
      } else if (!objectName.equals(other.objectName)) {
        return false;
      }
      if (ownerPHID == null) {
        if (other.ownerPHID != null) {
          return false;
        }
      } else if (!ownerPHID.equals(other.ownerPHID)) {
        return false;
      }
      if (phid == null) {
        if (other.phid != null) {
          return false;
        }
      } else if (!phid.equals(other.phid)) {
        return false;
      }
      if (priority == null) {
        if (other.priority != null) {
          return false;
        }
      } else if (!priority.equals(other.priority)) {
        return false;
      }
      if (priorityColor == null) {
        if (other.priorityColor != null) {
          return false;
        }
      } else if (!priorityColor.equals(other.priorityColor)) {
        return false;
      }
      if (projectPHIDs == null) {
        if (other.projectPHIDs != null) {
          return false;
        }
      } else if (!projectPHIDs.equals(other.projectPHIDs)) {
        return false;
      }
      if (status == null) {
        if (other.status != null) {
          return false;
        }
      } else if (!status.equals(other.status)) {
        return false;
      }
      if (statusName == null) {
        if (other.statusName != null) {
          return false;
        }
      } else if (!statusName.equals(other.statusName)) {
        return false;
      }
      if (title == null) {
        if (other.title != null) {
          return false;
        }
      } else if (!title.equals(other.title)) {
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
      return "TaskResult [id=" + id + ", phid=" + phid + ", authorPHID="
          + authorPHID + ", ownerPHID=" + ownerPHID + ", ccPHIDs=" + ccPHIDs
          + ", status=" + status + ", statusName=" + statusName + ", isClosed="
          + isClosed + ", priority=" + priority + ", priorityColor="
          + priorityColor + ", title=" + title + ", description=" + description
          + ", projectPHIDs=" + projectPHIDs + ", uri=" + uri + ", auxiliary="
          + auxiliary + ", objectName=" + objectName + ", dateCreated="
          + dateCreated + ", dateModified=" + dateModified
          + ", dependsOnTaskPHIDs=" + dependsOnTaskPHIDs + "]";
    }
  }
}
