$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
$jdk = Get-ChildItem "$PSScriptRoot/.tools/java" -Directory | Select-Object -First 1
if (-not $jdk) { throw 'Lokales Java 21 fehlt unter .tools/java.' }
$env:JAVA_HOME = $jdk.FullName
$env:GRADLE_USER_HOME = "$PSScriptRoot/.gradle/user-home"
& "$PSScriptRoot/.tools/gradle-8.10.2/bin/gradle.bat" --console=plain @args
exit $LASTEXITCODE
