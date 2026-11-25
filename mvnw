#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup script (only-script)

set -eu

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
PROPS_FILE="$SCRIPT_DIR/.mvn/wrapper/maven-wrapper.properties"

DIST_URL="$(grep '^distributionUrl=' "$PROPS_FILE" | head -n 1 | cut -d'=' -f2- | tr -d '\r')"
FILE_NAME="$(basename "$DIST_URL")"
DIST_NAME="${FILE_NAME%.zip}"
MAVEN_USER_HOME="${MAVEN_USER_HOME:-$HOME/.m2}"
DEST_DIR="$MAVEN_USER_HOME/wrapper/dists/$DIST_NAME"

MVN_BIN="$(find "$DEST_DIR" -type f -name mvn -path '*/bin/mvn' 2>/dev/null | head -n 1 || true)"

if [ -z "$MVN_BIN" ]; then
  mkdir -p "$DEST_DIR"
  ZIP_PATH="$DEST_DIR/$FILE_NAME"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$DIST_URL" -o "$ZIP_PATH"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$DIST_URL" -O "$ZIP_PATH"
  else
    echo "Error: curl or wget is required to download Maven." >&2
    exit 1
  fi
  unzip -q "$ZIP_PATH" -d "$DEST_DIR"
  rm -f "$ZIP_PATH"
  MVN_BIN="$(find "$DEST_DIR" -type f -name mvn -path '*/bin/mvn' | head -n 1)"
fi

exec "$MVN_BIN" "$@"
