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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.easymock.Capture;

import at.quelltextlich.phabricator.conduit.ConduitException;
import at.quelltextlich.phabricator.conduit.raw.ManiphestModule.SingleGetTaskTransactionsResult;
import at.quelltextlich.phabricator.conduit.raw.ManiphestModule.TaskTransaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ManiphestModuleTest extends ModuleTestCase {
  public void testCreateTaskPass() throws Exception {
    final JsonObject ret = new JsonObject();
    final JsonArray userArrayRet = new JsonArray();
    userArrayRet.add(new JsonPrimitive("PHID-USER-3nphm6xkw2mpyfshq4dq"));
    final JsonObject auxiliaryRet = new JsonObject();
    auxiliaryRet.add("std:maniphest:security_topic", null);
    auxiliaryRet.add("isdc:sprint:storypoints", null);
    ret.addProperty("id", 42);
    ret.addProperty("phid", "PHID-TASK-btorxi3333rmlvrqdzr7");
    ret.addProperty("authorPHID", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    ret.add("ownerPHID", null);
    ret.add("ccPHIDs", userArrayRet);
    ret.addProperty("status", "open");
    ret.addProperty("statusName", "Open");
    ret.addProperty("isClosed", false);
    ret.addProperty("priority", "Needs Triage");
    ret.addProperty("priorityColor", "violet");
    ret.addProperty("title", "qchris-test-task");
    ret.addProperty("description", "foo");
    ret.add("projectPHIDs", new JsonArray());
    ret.addProperty("uri", "https://phabricator.local/T42");
    ret.add("auxiliary", auxiliaryRet);
    ret.addProperty("objectName", "T42");
    ret.addProperty("dateCreated", "1436304454");
    ret.addProperty("dateModified", "1436304469");
    ret.add("dependsOnTaskPHIDs", new JsonArray());

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("maniphest.createtask"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ManiphestModule module = getModule();
    final Map<String, String> auxiliary = new HashMap<String, String>();
    auxiliary.put("foo", "fooValue");
    auxiliary.put("bar", "barValue");
    final ManiphestModule.CreateTaskResult result = module.createTask(
        "titleFoo", "descriptionBar", "ownerBaz", "viewPolicyFoo",
        "editPolicyBar", Arrays.asList("cc1", "cc2"), 85,
        Arrays.asList("project1", "project2"), auxiliary);

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);
    assertEquals("'title' does not match in params", "titleFoo",
        params.get("title"));
    assertEquals("'description' does not match in params", "descriptionBar",
        params.get("description"));
    assertEquals("'owner' does not match in params", "ownerBaz",
        params.get("ownerPHID"));
    assertEquals("'viewPolicy' does not match in params", "viewPolicyFoo",
        params.get("viewPolicy"));
    assertEquals("'editPolicy' does not match in params", "editPolicyBar",
        params.get("editPolicy"));
    assertEquals("'ccPHIDs' does not match in params",
        Arrays.asList("cc1", "cc2"), params.get("ccPHIDs"));
    assertEquals("'priority' does not match in params", 85,
        params.get("priority"));
    assertEquals("'projectPHIDs' does not match in params",
        Arrays.asList("project1", "project2"), params.get("projectPHIDs"));
    final Map<String, String> auxiliaryParam = new HashMap<String, String>();
    auxiliaryParam.put("foo", "fooValue");
    auxiliaryParam.put("bar", "barValue");
    assertEquals("'auxiliary' does not match in params", auxiliaryParam,
        params.get("auxiliary"));

    final Map<String, String> auxiliaryExp = new HashMap<String, String>();
    auxiliaryExp.put("std:maniphest:security_topic", null);
    auxiliaryExp.put("isdc:sprint:storypoints", null);
    final ManiphestModule.CreateTaskResult expected = new ManiphestModule.CreateTaskResult(
        42, "PHID-TASK-btorxi3333rmlvrqdzr7", "PHID-USER-3nphm6xkw2mpyfshq4dq",
        null, Arrays.asList("PHID-USER-3nphm6xkw2mpyfshq4dq"), "open", "Open",
        false, "Needs Triage", "violet", "qchris-test-task", "foo",
        new ArrayList<String>(), "https://phabricator.local/T42", auxiliaryExp,
        "T42", "1436304454", "1436304469", new ArrayList<String>());

    assertEquals("Results do not match", expected, result);
  }

  public void testGetTaskTransactionsPass() throws Exception {
    JsonObject transaction;

    final JsonObject ret = new JsonObject();

    final JsonArray ret84 = new JsonArray();
    transaction = new JsonObject();
    transaction.addProperty("taskID", "84");
    transaction
        .addProperty("transactionPHID", "PHID-XACT-TASK-xhbr4gj7ca224b2");
    transaction.addProperty("transactionType", "priority");
    transaction.add("oldValue", null);
    transaction.addProperty("newValue", 25);
    transaction.add("comments", null);
    transaction.addProperty("authorPHID", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    transaction.addProperty("dateCreated", "1436473263");
    ret84.add(transaction);

    transaction = new JsonObject();
    transaction.addProperty("taskID", "84");
    transaction
        .addProperty("transactionPHID", "PHID-XACT-TASK-xhbr4gj7ca224b3");
    transaction.addProperty("transactionType", "title");
    transaction.addProperty("oldValue", "foo");
    transaction.addProperty("newValue", "bar");
    transaction.addProperty("comments", "commentBar");
    transaction.addProperty("authorPHID", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    transaction.addProperty("dateCreated", "1436473263");
    ret84.add(transaction);

    ret.add("84", ret84);

    final JsonArray ret85 = new JsonArray();
    transaction = new JsonObject();
    transaction.addProperty("taskID", "85");
    transaction
        .addProperty("transactionPHID", "PHID-XACT-TASK-xhbr4gj7ca224b4");
    transaction.addProperty("transactionType", "core:subscribers");
    transaction.add("oldValue", new JsonArray());
    final JsonArray userArray = new JsonArray();
    userArray.add(new JsonPrimitive("user1"));
    userArray.add(new JsonPrimitive("user2"));
    transaction.add("newValue", userArray);
    transaction.addProperty("comments", "commentFoo");
    transaction.addProperty("authorPHID", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    transaction.addProperty("dateCreated", "1436473264");
    ret85.add(transaction);

    ret.add("85", ret85);

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(
        connection.call(eq("maniphest.gettasktransactions"),
            capture(paramsCapture))).andReturn(ret).once();

    replayMocks();

    final ManiphestModule module = getModule();
    final ManiphestModule.GetTaskTransactionsResult result = module
        .getTaskTransactions(Arrays.asList(84, 85));

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);
    assertEquals("'ids' does not match in params", Arrays.asList(84, 85),
        params.get("ids"));

    final ManiphestModule.GetTaskTransactionsResult expected = new ManiphestModule.GetTaskTransactionsResult();
    SingleGetTaskTransactionsResult transactions = new SingleGetTaskTransactionsResult();
    transactions.add(new TaskTransaction("84",
        "PHID-XACT-TASK-xhbr4gj7ca224b2", "priority", null, 25.0, null,
        "PHID-USER-3nphm6xkw2mpyfshq4dq", "1436473263"));
    transactions.add(new TaskTransaction("84",
        "PHID-XACT-TASK-xhbr4gj7ca224b3", "title", "foo", "bar", "commentBar",
        "PHID-USER-3nphm6xkw2mpyfshq4dq", "1436473263"));
    expected.put("84", transactions);
    transactions = new SingleGetTaskTransactionsResult();
    transactions.add(new TaskTransaction("85",
        "PHID-XACT-TASK-xhbr4gj7ca224b4", "core:subscribers", Arrays.asList(),
        Arrays.asList("user1", "user2"), "commentFoo",
        "PHID-USER-3nphm6xkw2mpyfshq4dq", "1436473264"));
    expected.put("85", transactions);

    assertEquals("Results do not match", expected, result);
  }

  public void testInfoPass() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject ret = new JsonObject();
    final JsonArray userArrayRet = new JsonArray();
    userArrayRet.add(new JsonPrimitive("PHID-USER-3nphm6xkw2mpyfshq4dq"));
    final JsonObject auxiliaryRet = new JsonObject();
    auxiliaryRet.add("std:maniphest:security_topic", null);
    auxiliaryRet.add("isdc:sprint:storypoints", null);
    ret.addProperty("id", 42);
    ret.addProperty("phid", "PHID-TASK-btorxi3333rmlvrqdzr7");
    ret.addProperty("authorPHID", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    ret.add("ownerPHID", null);
    ret.add("ccPHIDs", userArrayRet);
    ret.addProperty("status", "open");
    ret.addProperty("statusName", "Open");
    ret.addProperty("isClosed", false);
    ret.addProperty("priority", "Needs Triage");
    ret.addProperty("priorityColor", "violet");
    ret.addProperty("title", "qchris-test-task");
    ret.addProperty("description", "foo");
    ret.add("projectPHIDs", new JsonArray());
    ret.addProperty("uri", "https://phabricator.local/T42");
    ret.add("auxiliary", auxiliaryRet);
    ret.addProperty("objectName", "T42");
    ret.addProperty("dateCreated", "1436304454");
    ret.addProperty("dateModified", "1436304469");
    ret.add("dependsOnTaskPHIDs", new JsonArray());

    expect(connection.call(eq("maniphest.info"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ManiphestModule module = getModule();
    final ManiphestModule.InfoResult result = module.info(42);

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("TaskResult id is not set", 42, params.get("task_id"));
    assertHasSessionKey(params);

    final Map<String, String> auxiliary = new HashMap<String, String>();
    auxiliary.put("std:maniphest:security_topic", null);
    auxiliary.put("isdc:sprint:storypoints", null);
    final ManiphestModule.InfoResult expected = new ManiphestModule.InfoResult(
        42, "PHID-TASK-btorxi3333rmlvrqdzr7", "PHID-USER-3nphm6xkw2mpyfshq4dq",
        null, Arrays.asList("PHID-USER-3nphm6xkw2mpyfshq4dq"), "open", "Open",
        false, "Needs Triage", "violet", "qchris-test-task", "foo",
        new ArrayList<String>(), "https://phabricator.local/T42", auxiliary,
        "T42", "1436304454", "1436304469", new ArrayList<String>());

    assertEquals("Results do not match", expected, result);
  }

  public void testInfoFailSession() throws Exception {
    final ConduitException conduitException = new ConduitException();

    sessionHandler.failNextFillingIn(conduitException);

    replayMocks();

    final ManiphestModule module = getModule();
    try {
      module.info(42);
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }
  }

  public void testInfoFailCall() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject retRelevant = new JsonObject();
    retRelevant.add("id", new JsonPrimitive(42));

    final ConduitException conduitException = new ConduitException();
    expect(connection.call(eq("maniphest.info"), capture(paramsCapture)))
        .andThrow(conduitException).once();

    replayMocks();

    final ManiphestModule module = getModule();
    try {
      module.info(42);
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("TaskResult id is not set", 42, params.get("task_id"));
    assertHasSessionKey(params);
  }

  public void testUpdatePassId() throws Exception {
    final JsonObject retConnect = new JsonObject();
    retConnect.add("sessionKey", new JsonPrimitive("KeyFoo"));

    final JsonObject ret = new JsonObject();
    final JsonArray userArrayRet = new JsonArray();
    userArrayRet.add(new JsonPrimitive("PHID-USER-3nphm6xkw2mpyfshq4dq"));
    final JsonObject auxiliaryRet = new JsonObject();
    auxiliaryRet.add("std:maniphest:security_topic", null);
    auxiliaryRet.add("isdc:sprint:storypoints", null);
    ret.addProperty("id", 42);
    ret.addProperty("phid", "PHID-TASK-btorxi3333rmlvrqdzr7");
    ret.addProperty("authorPHID", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    ret.add("ownerPHID", null);
    ret.add("ccPHIDs", userArrayRet);
    ret.addProperty("status", "open");
    ret.addProperty("statusName", "Open");
    ret.addProperty("isClosed", false);
    ret.addProperty("priority", "Needs Triage");
    ret.addProperty("priorityColor", "violet");
    ret.addProperty("title", "qchris-test-task");
    ret.addProperty("description", "foo");
    ret.add("projectPHIDs", new JsonArray());
    ret.addProperty("uri", "https://phabricator.local/T42");
    ret.add("auxiliary", auxiliaryRet);
    ret.addProperty("objectName", "T42");
    ret.addProperty("dateCreated", "1436304454");
    ret.addProperty("dateModified", "1436304469");
    ret.add("dependsOnTaskPHIDs", new JsonArray());

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("maniphest.update"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ManiphestModule module = getModule();
    final Map<String, String> auxiliary = new HashMap<String, String>();
    auxiliary.put("foo", "fooValue");
    auxiliary.put("bar", "barValue");
    final ManiphestModule.UpdateResult result = module.update(42, null,
        "titleFoo", "descriptionBar", "ownerBaz", "viewPolicyFoo",
        "editPolicyBar", Arrays.asList("cc1", "cc2"), 85,
        Arrays.asList("project1", "project2"), auxiliary, "statusFoo",
        "commentsBar");

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("TaskResult id is not set", 42, params.get("id"));
    assertHasSessionKey(params);
    assertEquals("'id' does not match in params", 42, params.get("id"));
    assertNull("'phid' is not null", params.get("phid"));
    assertEquals("'title' does not match in params", "titleFoo",
        params.get("title"));
    assertEquals("'description' does not match in params", "descriptionBar",
        params.get("description"));
    assertEquals("'owner' does not match in params", "ownerBaz",
        params.get("ownerPHID"));
    assertEquals("'viewPolicy' does not match in params", "viewPolicyFoo",
        params.get("viewPolicy"));
    assertEquals("'editPolicy' does not match in params", "editPolicyBar",
        params.get("editPolicy"));
    assertEquals("'ccPHIDs' does not match in params",
        Arrays.asList("cc1", "cc2"), params.get("ccPHIDs"));
    assertEquals("'priority' does not match in params", 85,
        params.get("priority"));
    assertEquals("'projectPHIDs' does not match in params",
        Arrays.asList("project1", "project2"), params.get("projectPHIDs"));
    final Map<String, String> auxiliaryParam = new HashMap<String, String>();
    auxiliaryParam.put("foo", "fooValue");
    auxiliaryParam.put("bar", "barValue");
    assertEquals("'auxiliary' does not match in params", auxiliaryParam,
        params.get("auxiliary"));
    assertEquals("'status' does not match in params", "statusFoo",
        params.get("status"));
    assertEquals("'comments' does not match in params", "commentsBar",
        params.get("comments"));

    final Map<String, String> auxiliaryExp = new HashMap<String, String>();
    auxiliaryExp.put("std:maniphest:security_topic", null);
    auxiliaryExp.put("isdc:sprint:storypoints", null);
    final ManiphestModule.UpdateResult expected = new ManiphestModule.UpdateResult(
        42, "PHID-TASK-btorxi3333rmlvrqdzr7", "PHID-USER-3nphm6xkw2mpyfshq4dq",
        null, Arrays.asList("PHID-USER-3nphm6xkw2mpyfshq4dq"), "open", "Open",
        false, "Needs Triage", "violet", "qchris-test-task", "foo",
        new ArrayList<String>(), "https://phabricator.local/T42", auxiliaryExp,
        "T42", "1436304454", "1436304469", new ArrayList<String>());

    assertEquals("Results do not match", expected, result);
  }

  public void testUpdatePassPhid() throws Exception {
    final JsonObject retConnect = new JsonObject();
    retConnect.add("sessionKey", new JsonPrimitive("KeyFoo"));

    final JsonObject ret = new JsonObject();
    final JsonArray userArrayRet = new JsonArray();
    userArrayRet.add(new JsonPrimitive("PHID-USER-3nphm6xkw2mpyfshq4dq"));
    final JsonObject auxiliaryRet = new JsonObject();
    auxiliaryRet.add("std:maniphest:security_topic", null);
    auxiliaryRet.add("isdc:sprint:storypoints", null);
    ret.addProperty("id", 42);
    ret.addProperty("phid", "PHID-TASK-btorxi3333rmlvrqdzr7");
    ret.addProperty("authorPHID", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    ret.add("ownerPHID", null);
    ret.add("ccPHIDs", userArrayRet);
    ret.addProperty("status", "open");
    ret.addProperty("statusName", "Open");
    ret.addProperty("isClosed", false);
    ret.addProperty("priority", "Needs Triage");
    ret.addProperty("priorityColor", "violet");
    ret.addProperty("title", "qchris-test-task");
    ret.addProperty("description", "foo");
    ret.add("projectPHIDs", new JsonArray());
    ret.addProperty("uri", "https://phabricator.local/T42");
    ret.add("auxiliary", auxiliaryRet);
    ret.addProperty("objectName", "T42");
    ret.addProperty("dateCreated", "1436304454");
    ret.addProperty("dateModified", "1436304469");
    ret.add("dependsOnTaskPHIDs", new JsonArray());

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("maniphest.update"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ManiphestModule module = getModule();
    final Map<String, String> auxiliary = new HashMap<String, String>();
    auxiliary.put("foo", "fooValue");
    auxiliary.put("bar", "barValue");
    final ManiphestModule.UpdateResult result = module.update(null,
        "PHID-TASK-btorxi3333rmlvrqdzr7", "titleFoo", "descriptionBar",
        "ownerBaz", "viewPolicyFoo", "editPolicyBar",
        Arrays.asList("cc1", "cc2"), 85, Arrays.asList("project1", "project2"),
        auxiliary, "statusFoo", "commentsBar");

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);
    assertNull("'id' does not match in params", params.get("id"));
    assertEquals("'phid' does not match in params",
        "PHID-TASK-btorxi3333rmlvrqdzr7", params.get("phid"));
    assertEquals("'title' does not match in params", "titleFoo",
        params.get("title"));
    assertEquals("'description' does not match in params", "descriptionBar",
        params.get("description"));
    assertEquals("'owner' does not match in params", "ownerBaz",
        params.get("ownerPHID"));
    assertEquals("'viewPolicy' does not match in params", "viewPolicyFoo",
        params.get("viewPolicy"));
    assertEquals("'editPolicy' does not match in params", "editPolicyBar",
        params.get("editPolicy"));
    assertEquals("'ccPHIDs' does not match in params",
        Arrays.asList("cc1", "cc2"), params.get("ccPHIDs"));
    assertEquals("'priority' does not match in params", 85,
        params.get("priority"));
    assertEquals("'projectPHIDs' does not match in params",
        Arrays.asList("project1", "project2"), params.get("projectPHIDs"));
    final Map<String, String> auxiliaryParam = new HashMap<String, String>();
    auxiliaryParam.put("foo", "fooValue");
    auxiliaryParam.put("bar", "barValue");
    assertEquals("'auxiliary' does not match in params", auxiliaryParam,
        params.get("auxiliary"));
    assertEquals("'status' does not match in params", "statusFoo",
        params.get("status"));
    assertEquals("'comments' does not match in params", "commentsBar",
        params.get("comments"));

    final Map<String, String> auxiliaryExp = new HashMap<String, String>();
    auxiliaryExp.put("std:maniphest:security_topic", null);
    auxiliaryExp.put("isdc:sprint:storypoints", null);
    final ManiphestModule.UpdateResult expected = new ManiphestModule.UpdateResult(
        42, "PHID-TASK-btorxi3333rmlvrqdzr7", "PHID-USER-3nphm6xkw2mpyfshq4dq",
        null, Arrays.asList("PHID-USER-3nphm6xkw2mpyfshq4dq"), "open", "Open",
        false, "Needs Triage", "violet", "qchris-test-task", "foo",
        new ArrayList<String>(), "https://phabricator.local/T42", auxiliaryExp,
        "T42", "1436304454", "1436304469", new ArrayList<String>());

    assertEquals("Results do not match", expected, result);
  }

  public void testUpdateFailSession() throws Exception {
    final ConduitException conduitException = new ConduitException();

    sessionHandler.failNextFillingIn(conduitException);

    replayMocks();

    final ManiphestModule module = getModule();
    final Map<String, String> auxiliary = new HashMap<String, String>();
    auxiliary.put("foo", "fooValue");
    auxiliary.put("bar", "barValue");
    try {
      module.update(42, null, "titleFoo", "descriptionBar", "ownerBaz",
          "viewPolicyFoo", "editPolicyBar", Arrays.asList("cc1", "cc2"), 85,
          Arrays.asList("project1", "project2"), auxiliary, "statusFoo",
          "commentsBar");
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }
  }

  public void testUpdateFailCall() throws Exception {
    final ConduitException conduitException = new ConduitException();

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("maniphest.update"), capture(paramsCapture)))
        .andThrow(conduitException).once();

    replayMocks();

    final ManiphestModule module = getModule();
    final Map<String, String> auxiliary = new HashMap<String, String>();
    auxiliary.put("foo", "fooValue");
    auxiliary.put("bar", "barValue");
    try {
      module.update(42, null, "titleFoo", "descriptionBar", "ownerBaz",
          "viewPolicyFoo", "editPolicyBar", Arrays.asList("cc1", "cc2"), 85,
          Arrays.asList("project1", "project2"), auxiliary, "statusFoo",
          "commentsBar");
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }

    final Map<String, Object> paramsRelevant = paramsCapture.getValue();
    assertEquals("TaskResult id is not set", 42, paramsRelevant.get("id"));
  }

  public void testQueryPass() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject ret = new JsonObject();

    JsonObject retTask = new JsonObject();
    JsonArray ccRet = new JsonArray();
    ccRet.add(new JsonPrimitive("cc1"));
    ccRet.add(new JsonPrimitive("cc2"));
    JsonObject auxiliaryRet = new JsonObject();
    auxiliaryRet.add("std:maniphest:security_topic", null);
    auxiliaryRet.add("isdc:sprint:storypoints", null);
    retTask.addProperty("id", 84);
    retTask.addProperty("phid", "PHID-TASK-btorxi3333rmlvrqdzr7");
    retTask.addProperty("authorPHID", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    retTask.addProperty("ownerPHID", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    retTask.add("ccPHIDs", ccRet);
    retTask.addProperty("status", "open");
    retTask.addProperty("statusName", "Open");
    retTask.addProperty("isClosed", false);
    retTask.addProperty("priority", "Needs Triage");
    retTask.addProperty("priorityColor", "violet");
    retTask.addProperty("title", "qchris-test-task");
    retTask.addProperty("description", "foo");
    retTask.add("projectPHIDs", new JsonArray());
    retTask.addProperty("uri", "https://phabricator.local/T84");
    retTask.add("auxiliary", auxiliaryRet);
    retTask.addProperty("objectName", "T84");
    retTask.addProperty("dateCreated", "1436304454");
    retTask.addProperty("dateModified", "1436304469");
    retTask.add("dependsOnTaskPHIDs", new JsonArray());
    ret.add("PHID-TASK-btorxi3333rmlvrqdzr7", retTask);

    retTask = new JsonObject();
    ccRet = new JsonArray();
    ccRet.add(new JsonPrimitive("cc1"));
    ccRet.add(new JsonPrimitive("cc3"));
    auxiliaryRet = new JsonObject();
    auxiliaryRet.add("std:maniphest:security_topic2", null);
    auxiliaryRet.add("isdc:sprint:storypoints2", null);
    retTask.addProperty("id", 85);
    retTask.addProperty("phid", "PHID-TASK-btorxi3333rmlvrqdzr8");
    retTask.addProperty("authorPHID", "PHID-USER-3nphm6xkw2mpyfshq4dr");
    retTask.addProperty("ownerPHID", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    retTask.add("ccPHIDs", ccRet);
    retTask.addProperty("status", "open");
    retTask.addProperty("statusName", "Open");
    retTask.addProperty("isClosed", false);
    retTask.addProperty("priority", "Needs Triage");
    retTask.addProperty("priorityColor", "violet");
    retTask.addProperty("title", "qchris-test-task");
    retTask.addProperty("description", "foo");
    retTask.add("projectPHIDs", new JsonArray());
    retTask.addProperty("uri", "https://phabricator.local/T85");
    retTask.add("auxiliary", auxiliaryRet);
    retTask.addProperty("objectName", "T85");
    retTask.addProperty("dateCreated", "1436304455");
    retTask.addProperty("dateModified", "1436304470");
    retTask.add("dependsOnTaskPHIDs", new JsonArray());
    ret.add("PHID-TASK-btorxi3333rmlvrqdzr8", retTask);

    expect(connection.call(eq("maniphest.query"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ManiphestModule module = getModule();
    final ManiphestModule.QueryResult result = module.query(Arrays.asList(84,
        85, 86), Arrays.asList("PHID-TASK-btorxi3333rmlvrqdzr7",
        "PHID-TASK-btorxi3333rmlvrqdzr8", "PHID-TASK-btorxi3333rmlvrqdzr9"),
        Arrays.asList("PHID-USER-3nphm6xkw2mpyfshq4dq",
            "PHID-USER-3nphm6xkw2mpyfshq4dr"),
        Arrays.asList("PHID-USER-3nphm6xkw2mpyfshq4dq",
            "PHID-USER-3nphm6xkw2mpyfshq4dr"), Arrays.asList("PHID-PROJ-foo",
            "PHID-PROJ-bar", "PHID-PROJ-baz"), Arrays.asList("cc1", "cc2",
            "cc3"), "Lorem ipsum", "status-any", "order-created", 3, 10);

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);
    assertEquals("'ids' do not match in params", Arrays.asList(84, 85, 86),
        params.get("ids"));
    assertEquals("'phids' do not match in params", Arrays.asList(
        "PHID-TASK-btorxi3333rmlvrqdzr7", "PHID-TASK-btorxi3333rmlvrqdzr8",
        "PHID-TASK-btorxi3333rmlvrqdzr9"), params.get("phids"));
    assertEquals("'ownerPHIDs' do not match in params", Arrays.asList(
        "PHID-USER-3nphm6xkw2mpyfshq4dq", "PHID-USER-3nphm6xkw2mpyfshq4dr"),
        params.get("ownerPHIDs"));
    assertEquals("'authorPHIDs' do not match in params", Arrays.asList(
        "PHID-USER-3nphm6xkw2mpyfshq4dq", "PHID-USER-3nphm6xkw2mpyfshq4dr"),
        params.get("authorPHIDs"));
    assertEquals("'projectPHIDs' do not match in params",
        Arrays.asList("PHID-PROJ-foo", "PHID-PROJ-bar", "PHID-PROJ-baz"),
        params.get("projectPHIDs"));
    assertEquals("'ccPHIDs' do not match in params",
        Arrays.asList("cc1", "cc2", "cc3"), params.get("ccPHIDs"));
    assertEquals("'fullText' does not match in params", "Lorem ipsum",
        params.get("fullText"));
    assertEquals("'status' does not match in params", "status-any",
        params.get("status"));
    assertEquals("'order' does not match in params", "order-created",
        params.get("order"));
    assertEquals("'limit' does not match in params", 3, params.get("limit"));
    assertEquals("'offset' does not match in params", 10, params.get("offset"));

    Map<String, String> auxiliary = new HashMap<String, String>();
    auxiliary.put("std:maniphest:security_topic", null);
    auxiliary.put("isdc:sprint:storypoints", null);
    final ManiphestModule.TaskResult expected84 = new ManiphestModule.TaskResult(
        84, "PHID-TASK-btorxi3333rmlvrqdzr7", "PHID-USER-3nphm6xkw2mpyfshq4dq",
        "PHID-USER-3nphm6xkw2mpyfshq4dq", Arrays.asList("cc1", "cc2"), "open",
        "Open", false, "Needs Triage", "violet", "qchris-test-task", "foo",
        new ArrayList<String>(), "https://phabricator.local/T84", auxiliary,
        "T84", "1436304454", "1436304469", new ArrayList<String>());
    assertEquals("Results for T84 do not match", expected84,
        result.get("PHID-TASK-btorxi3333rmlvrqdzr7"));

    auxiliary = new HashMap<String, String>();
    auxiliary.put("std:maniphest:security_topic2", null);
    auxiliary.put("isdc:sprint:storypoints2", null);
    final ManiphestModule.TaskResult expected85 = new ManiphestModule.TaskResult(
        85, "PHID-TASK-btorxi3333rmlvrqdzr8", "PHID-USER-3nphm6xkw2mpyfshq4dr",
        "PHID-USER-3nphm6xkw2mpyfshq4dq", Arrays.asList("cc1", "cc3"), "open",
        "Open", false, "Needs Triage", "violet", "qchris-test-task", "foo",
        new ArrayList<String>(), "https://phabricator.local/T85", auxiliary,
        "T85", "1436304455", "1436304470", new ArrayList<String>());
    assertEquals("Results for T85 do not match", expected85,
        result.get("PHID-TASK-btorxi3333rmlvrqdzr8"));

    final ManiphestModule.QueryResult expected = new ManiphestModule.QueryResult();
    expected.put("PHID-TASK-btorxi3333rmlvrqdzr7", expected84);
    expected.put("PHID-TASK-btorxi3333rmlvrqdzr8", expected85);

    assertEquals("Results do not match", expected, result);
  }

  public void testQueryStatusesPass() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject ret = new JsonObject();
    ret.addProperty("defaultStatus", "defStatus");
    ret.addProperty("defaultClosedStatus", "defClosedStatus");
    ret.addProperty("duplicateStatus", "dupe");

    final JsonArray openStatuses = new JsonArray();
    openStatuses.add(new JsonPrimitive("open1"));
    openStatuses.add(new JsonPrimitive("open2"));
    ret.add("openStatuses", openStatuses);

    final JsonObject closedStatuses = new JsonObject();
    closedStatuses.addProperty("1", "closed1");
    closedStatuses.addProperty("2", "closed2");
    ret.add("closedStatuses", closedStatuses);

    final JsonArray allStatuses = new JsonArray();
    allStatuses.add(new JsonPrimitive("open1"));
    allStatuses.add(new JsonPrimitive("closed2"));
    ret.add("allStatuses", allStatuses);

    final JsonObject statusMap = new JsonObject();
    statusMap.addProperty("foo", "bar");
    statusMap.addProperty("cowa", "bunga");
    ret.add("statusMap", statusMap);

    expect(
        connection.call(eq("maniphest.querystatuses"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ManiphestModule module = getModule();
    final ManiphestModule.QueryStatusesResult result = module.queryStatuses();

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);

    final Map<String, String> expClosedStatuses = new HashMap<String, String>();
    expClosedStatuses.put("1", "closed1");
    expClosedStatuses.put("2", "closed2");

    final Map<String, String> expStatusMap = new HashMap<String, String>();
    expStatusMap.put("foo", "bar");
    expStatusMap.put("cowa", "bunga");
    final ManiphestModule.QueryStatusesResult expected = new ManiphestModule.QueryStatusesResult(
        "defStatus", "defClosedStatus", "dupe",
        Arrays.asList("open1", "open2"), expClosedStatuses, Arrays.asList(
            "open1", "closed2"), expStatusMap);

    assertEquals("Results do not match", expected, result);
  }

  @Override
  protected ManiphestModule getModule() {
    return new ManiphestModule(connection, sessionHandler);
  }
}
