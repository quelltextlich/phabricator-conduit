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
package at.quelltextlich.phabricator.conduit.results;

import com.google.gson.JsonElement;

/**
 * Models the generic wrapper for API calls
 * <p/>
 * JSON looks like:
 * <pre>
 * {
 *   "result": SOME JSON OBJECT,
 *   "error_code":null,
 *   "error_info":null
 * }
 * </pre>
 */
public class CallCapsule {
  private JsonElement result;
  private String error_code;
  private String error_info;

  public JsonElement getResult() {
    return result;
  }

  public String getErrorCode() {
    return error_code;
  }

  public String getErrorInfo() {
    return error_info;
  }
}
