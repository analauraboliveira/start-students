@echo off
setlocal
set "MVN_VERSION=3.9.11"
set "MVN_HOME=%~dp0.mvn\apache-maven-%MVN_VERSION%"
set "MVN_CMD=%MVN_HOME%\bin\mvn.cmd"
if not exist "%MVN_CMD%" (
  echo Maven nao encontrado localmente. Baixando Maven %MVN_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $root='%~dp0.mvn'; New-Item -ItemType Directory -Force -Path $root | Out-Null; $zip=Join-Path $root 'maven.zip'; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MVN_VERSION%/apache-maven-%MVN_VERSION%-bin.zip' -OutFile $zip; Expand-Archive -Path $zip -DestinationPath $root -Force; Remove-Item $zip"
  if errorlevel 1 (
    echo Nao foi possivel baixar o Maven. Verifique a rede ou as regras do computador.
    exit /b 1
  )
)
call "%MVN_CMD%" %*
endlocal
