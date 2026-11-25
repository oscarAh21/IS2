<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM ----------------------------------------------------------------------------

@echo off
@IF "%__MVNW_CMD__Composite%"=="" (
  SETLOCAL EnableDelayedExpansion
  SET "WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain"
  for /F "usebackq tokens=1,2 delims==" %%A in ("%~dp0.mvn\wrapper\maven-wrapper.properties") do (
    if "%%A"=="distributionUrl" set "distributionUrl=%%B"
  )
  for /F "usebackq delims=" %%A in (`powershell -NoProfile -Command "& { $ErrorActionPreference='Stop'; $scriptDir='%~dp0'; $propsFile=Join-Path $scriptDir '.mvn\wrapper\maven-wrapper.properties'; $props=@{}; Get-Content $propsFile | Where-Object { $_ -match '^[^#].*=' } | ForEach-Object { $k,$v=$_.Split('=',2); $props[$k.Trim()]=$v.Trim() }; $url=$props['distributionUrl']; $fileName=[System.IO.Path]::GetFileName($url); $distName=[System.IO.Path]::GetFileNameWithoutExtension($fileName); $m2Home=if($env:MAVEN_USER_HOME){$env:MAVEN_USER_HOME}else{Join-Path $env:USERPROFILE '.m2'}; $destDir=Join-Path $m2Home ('wrapper\dists\'+$distName); $mvnCmd=Get-ChildItem -Path $destDir -Filter 'mvn.cmd' -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1; if (-not $mvnCmd) { New-Item -ItemType Directory -Force -Path $destDir | Out-Null; $zipPath=Join-Path $destDir $fileName; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri $url -OutFile $zipPath -UseBasicParsing; Expand-Archive -Path $zipPath -DestinationPath $destDir -Force; Remove-Item $zipPath -Force; $mvnCmd=Get-ChildItem -Path $destDir -Filter 'mvn.cmd' -Recurse | Select-Object -First 1 }; Write-Output $mvnCmd.FullName }"`) do (
    set "MVN_CMD=%%A"
  )
  if "!MVN_CMD!"=="" (
    echo Error: No se pudo localizar ni descargar Apache Maven Wrapper. 1>&2
    exit /b 1
  )
  call "!MVN_CMD!" %*
  exit /b !ERRORLEVEL!
)
: end batch / begin PowerShell #>
