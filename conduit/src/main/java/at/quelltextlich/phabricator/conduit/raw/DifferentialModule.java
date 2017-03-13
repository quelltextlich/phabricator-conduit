package at.quelltextlich.phabricator.conduit.raw;

import at.quelltextlich.phabricator.conduit.ConduitException;
import at.quelltextlich.phabricator.conduit.bare.Connection;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * Module for Conduit methods starting in 'differential.'
 */
public class DifferentialModule extends Module {

  public DifferentialModule(final Connection connection,
                       final SessionHandler sessionHandler) {
    super(connection, sessionHandler);
  }

  /**
   * Runs the API's 'differential.query' method
   *
   * @param authors
   * @param ccs
   * @param reviewers
   * @param paths
   * @param commitHashes
   * @param status
   * @param order
   * @param limit
   * @param offset
   * @param ids
   * @param phids
   * @param subscribers
   * @param responsibleUsers
   * @param branches
   * @return
   * @throws ConduitException
   */
  public DifferentialResult query(
          final Iterator<String> authors,
          final Iterator<String> ccs,
          final Iterator<String> reviewers,
          final Iterator<Map<String, String>> paths,
          final Iterator<Map<String, String>> commitHashes,
          final String status,
          final String order,
          final Integer limit,
          final Integer offset,
          final Iterator<Integer> ids,
          final Iterator<String> phids,
          final Iterator<String> subscribers,
          final Iterator<String> responsibleUsers,
          final Iterator<String> branches) throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);
    params.put("authors", authors);
    params.put("ccs", ccs);
    params.put("reviewers", reviewers);
    params.put("paths", paths);
    params.put("commitHashes", commitHashes);
    params.put("status", status);
    params.put("order", order);
    params.put("limit", limit);
    params.put("offset", offset);
    params.put("ids", ids);
    params.put("phids", phids);
    params.put("subscribers", subscribers);
    params.put("responsibleUsers", responsibleUsers);
    params.put("branches", branches);

    final JsonElement callResult = connection.call("differential.query", params);
    DifferentialResult result = null;
    result = gson.fromJson(callResult, DifferentialResult.class);
    return result;
  }

  /**
   * Runs the API's 'differential.getrawdiff' method
   *
   * @param diffID
   * @return
   * @throws ConduitException
   */
  public GetRawDiffResult getRawDiff(final int diffID) throws ConduitException {
    final Map<String, Object> params = new HashMap<String, Object>();
    sessionHandler.fillInSession(params);
    params.put("diffID", diffID);

    final JsonElement callResult = connection.call("differential.getrawdiff", params);
    final JsonObject callResultWrapper = new JsonObject();
    callResultWrapper.add("result", callResult);
    GetRawDiffResult result = null;
    result = gson.fromJson(callResultWrapper, GetRawDiffResult.class);
    return result;
  }

  public static class DifferentialResult extends ArrayList<SingleDifferentialResult> {
    private static final long serialVersionUID = 1L;
  }

  /**
   * Models the result for a call to 'differential.query'
   * JSON looks like:
   */
//  {
//    "0": {
//    "id": "514",
//            "phid": "PHID-DREV-xikhmzdzfq2rvrisgs4e",
//            "title": "WARP-10213: Make up last fix",
//            "uri": "http:\/\/172.16.1.137\/D514",
//            "dateCreated": "1488886194",
//            "dateModified": "1488945523",
//            "authorPHID": "PHID-USER-g7c5ppepjoytf2zqftvg",
//            "status": "0",
//            "statusName": "Needs Review",
//            "branch": null,
//            "summary": "\u4e0a\u4e00\u6b21\u7684\u6539\u52a8\u6ca1\u6709\u4fee\u590d\u5b8c\u5168\uff0c\u5bfc\u81f4\u540e\u9762\u6709\u4e00\u4e2aNPE\u6ca1\u6709\u4fee\u590d\u3002\u7ecfdebug\u53d1\u73b0\u662f\u6f0f\u6539\u4e86ColumnValue\u76f8\u5173\u7684\u903b\u8f91\uff0c\u8fd9\u6b21\u8865\u4e0a\u3002\u4e0a\u6b21\u4fee\u6539\u56e0\u4e3a\u73af\u5883\u95ee\u9898\u6ca1\u6709\u6d4b\u8bd5\uff0c\u8fd9\u6b21\u4e0e\u674e\u5149\u8dc3\u5171\u540c\u6d4b\u8bd5\u3002",
//            "testPlan": "\u624b\u5de5\u6d4b\u8bd5",
//            "lineCount": "11",
//            "activeDiffPHID": "PHID-DIFF-yz7sivwbxwrqvhfx2cby",
//            "diffs": [
//    "1412"
//    ],
//    "commits": [],
//    "reviewers": [
//    "PHID-PROJ-wu7auwgazl6cexbyuuhu"
//    ],
//    "ccs": [
//    "PHID-USER-l4nk7mcwxhfh7t6g2hdj"
//    ],
//    "hashes": [],
//    "auxiliary": {
//      "phabricator:projects": [],
//      "phabricator:depends-on": [],
//      "jira.issues": [
//      "WARP-10213"
//      ]
//    },
//    "repositoryPHID": "PHID-REPO-fo5i2rg2eer56izfws6a"
//  }
//  }
  public static class SingleDifferentialResult {

    private final int id;
    private final String phid;
    private final String title;
    private final String uri;
    private final String dateCreated;
    private final String dateModified;
    private final String authorPHID;
    private final int status;
    private final String statusName;
    private final String branch;
    private final String summary;
    private final String testPlan;
    private final String lineCount;
    private final String activeDiffPHID;
    private final List<Integer> diffs;
    private final List<String> commits;
    private final List<String> reviewers;
    private final List<String> ccs;
    private final List<String> hashes;
    private final Map<String, List<String>> auxiliary;
    private final String repositoryPHID;

    public SingleDifferentialResult(final int id,
                                    final String phid,
                                    final String title,
                                    final String uri,
                                    final String dateCreated,
                                    final String dateModified,
                                    final String authorPHID,
                                    final int status,
                                    final String statusName,
                                    final String branch,
                                    final String summary,
                                    final String testPlan,
                                    final String lineCount,
                                    final String activeDiffPHID,
                                    final List<Integer> diffs,
                                    final List<String> commits,
                                    final List<String> reviewers,
                                    final List<String> ccs,
                                    final List<String> hashes,
                                    final Map<String, List<String>> auxiliary,
                                    final String repositoryPHID) {
      this.id = id;
      this.phid = phid;
      this.title = title;
      this.uri = uri;
      this.dateCreated = dateCreated;
      this.dateModified = dateModified;
      this.authorPHID = authorPHID;
      this.status = status;
      this.statusName = statusName;
      this.branch = branch;
      this.summary = summary;
      this.testPlan = testPlan;
      this.lineCount = lineCount;
      this.activeDiffPHID = activeDiffPHID;
      this.diffs = diffs;
      this.commits = commits;
      this.reviewers = reviewers;
      this.ccs = ccs;
      this.hashes = hashes;
      this.auxiliary = auxiliary;
      this.repositoryPHID = repositoryPHID;
    }

    public int getId() {
      return id;
    }

    public String getPhid() {
      return phid;
    }

    public String getTitle() {
      return title;
    }

    public String getUri() {
      return uri;
    }

    public String getDateCreated() {
      return dateCreated;
    }

    public String getDateModified() {
      return dateModified;
    }

    public String getAuthorPHID() {
      return authorPHID;
    }

    public int getStatus() {
      return status;
    }

    public String getStatusName() {
      return statusName;
    }

    public String getBranch() {
      return branch;
    }

    public String getSummary() {
      return summary;
    }

    public String getTestPlan() {
      return testPlan;
    }

    public String getLineCount() {
      return lineCount;
    }

    public String getActiveDiffPHID() {
      return activeDiffPHID;
    }

    public List<Integer> getDiffs() {
      return diffs;
    }

    /**
     * @return the result id of latest result file
     */
    public int getLatestDiff() {
      int latestDiff = 0;
      for (int diff : diffs) {
        if (diff > latestDiff) {
          latestDiff = diff;
        }
      }
      return latestDiff;
    }

    public List<String> getCommits() {
      return commits;
    }

    public List<String> getReviewers() {
      return reviewers;
    }

    public List<String> getCcs() {
      return ccs;
    }

    public List<String> getHashes() {
      return hashes;
    }

    public Map<String, List<String>> getAuxiliary() {
      return auxiliary;
    }

    public String getRepositoryPHID() {
      return repositoryPHID;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      result = prime * result + ((phid == null) ? 0 : phid.hashCode());
      result = prime * result + ((title == null) ? 0 : title.hashCode());
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
      result = prime * result + ((dateCreated == null) ? 0 : dateCreated.hashCode());
      result = prime * result + ((dateModified == null) ? 0 : dateModified.hashCode());
      result = prime * result + ((authorPHID == null) ? 0 : authorPHID.hashCode());
      result = prime * result + status;
      result = prime * result + ((statusName == null) ? 0 : statusName.hashCode());
      result = prime * result + ((branch == null) ? 0 : branch.hashCode());
      result = prime * result + ((summary == null) ? 0 : summary.hashCode());
      result = prime * result + ((testPlan == null) ? 0 : testPlan.hashCode());
      result = prime * result + ((lineCount == null) ? 0 : lineCount.hashCode());
      result = prime * result + ((activeDiffPHID == null) ? 0 : activeDiffPHID.hashCode());
      result = prime * result + ((diffs == null) ? 0 : diffs.hashCode());
      result = prime * result + ((commits == null) ? 0 : commits.hashCode());
      result = prime * result + ((reviewers == null) ? 0 : reviewers.hashCode());
      result = prime * result + ((ccs == null) ? 0 : ccs.hashCode());
      result = prime * result + ((hashes == null) ? 0 : hashes.hashCode());
      result = prime * result + ((auxiliary == null) ? 0 : auxiliary.hashCode());
      result = prime * result + ((repositoryPHID == null) ? 0 : repositoryPHID.hashCode());
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
      final SingleDifferentialResult other = (SingleDifferentialResult) obj;
      if (id != other.id) {
        return false;
      }
      if (phid == null) {
        if (other.phid != null) {
          return false;
        }
      } else if (!phid.equals(other.phid)) {
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
      if (authorPHID == null) {
        if (other.authorPHID != null) {
          return false;
        }
      } else if (!authorPHID.equals(other.authorPHID)) {
        return false;
      }
      if (status != other.status) {
        return false;
      }
      if (statusName == null) {
        if (other.statusName != null) {
          return false;
        }
      } else if (!statusName.equals(other.statusName)) {
        return false;
      }
      if (branch == null) {
        if (other.branch != null) {
          return false;
        }
      } else if (!branch.equals(other.branch)) {
        return false;
      }
      if (summary == null) {
        if (other.summary != null) {
          return false;
        }
      } else if (!summary.equals(other.summary)) {
        return false;
      }
      if (testPlan == null) {
        if (other.testPlan != null) {
          return false;
        }
      } else if (!testPlan.equals(other.testPlan)) {
        return false;
      }
      if (lineCount == null) {
        if (other.lineCount != null) {
          return false;
        }
      } else if (!lineCount.equals(other.lineCount)) {
        return false;
      }
      if (activeDiffPHID == null) {
        if (other.activeDiffPHID != null) {
          return false;
        }
      } else if (!activeDiffPHID.equals(other.activeDiffPHID)) {
        return false;
      }
      if (diffs == null) {
        if (other.diffs != null) {
          return false;
        }
      } else if (!diffs.equals(other.diffs)) {
        return false;
      }
      if (commits == null) {
        if (other.commits != null) {
          return false;
        }
      } else if (!commits.equals(other.commits)) {
        return false;
      }
      if (reviewers == null) {
        if (other.reviewers != null) {
          return false;
        }
      } else if (!reviewers.equals(other.reviewers)) {
        return false;
      }
      if (ccs == null) {
        if (other.ccs != null) {
          return false;
        }
      } else if (!ccs.equals(other.ccs)) {
        return false;
      }
      if (hashes == null) {
        if (other.hashes != null) {
          return false;
        }
      } else if (!hashes.equals(other.hashes)) {
        return false;
      }
      if (auxiliary == null) {
        if (other.auxiliary != null) {
          return false;
        }
      } else if (!auxiliary.equals(other.auxiliary)) {
        return false;
      }
      if (repositoryPHID == null) {
        if (other.repositoryPHID != null) {
          return false;
        }
      } else if (!repositoryPHID.equals(other.repositoryPHID)) {
        return false;
      }
      return true;
    }

  }

  /**
   * Models the result for a call to 'differential.getrawdiff'
   * JSON looks like:
   */
//  result
//  result --git a/service/src/java/org/apache/hive/service/cli/ColumnValue.java
//  --- a/service/src/java/org/apache/hive/service/cli/ColumnValue.java
//  +++ b/service/src/java/org/apache/hive/service/cli/ColumnValue.java
//  @@ -96,14 +96,30 @@
//          return TColumnValue.stringVal(tStringValue);
//  }
//
//  +  private static TColumnValue stringValue(HiveChar value) {
  public static class GetRawDiffResult {
    private final String result;

    public GetRawDiffResult(final String diff) {
      this.result = diff;
    }

    public String getResult() {
      return result;
    }

    @Override
    public int hashCode() {
      return result.hashCode();
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
      final GetRawDiffResult other = (GetRawDiffResult) obj;
      if (result == null) {
        if (other.result != null) {
          return false;
        }
      } else if (!result.equals(other.result)) {
        return false;
      }
      return true;
    }

  }

}
