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

import at.quelltextlich.phabricator.conduit.bare.Connection;

import com.google.gson.Gson;

public class Module {
  protected Connection connection;
  protected SessionHandler sessionHandler;
  protected Gson gson;

  public Module(final Connection connection, final SessionHandler sessionHandler) {
    this.connection = connection;
    this.sessionHandler = sessionHandler;
    gson = new Gson();
  }
}
