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

package at.quelltextlich.phabricator.conduit;

/**
 * Exception thrown if Conduit response has error_code or error_info set
 */
public class ConduitErrorException extends ConduitException {

  private static final long serialVersionUID = 1L;

  private final String errorCode;
  private final String errorInfo;

  ConduitErrorException(String method, String errorCode, String errorInfo) {
    super("Method '" + method + "' gave: " +errorCode +
        ((errorInfo != null && !errorInfo.isEmpty())
          ? (", " + errorInfo)
          : ""));
    this.errorCode = errorCode;
    this.errorInfo = errorInfo;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getErrorInfo() {
    return errorInfo;
  }

}
