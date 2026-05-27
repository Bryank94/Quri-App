$ErrorActionPreference = "Stop"

$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

Write-Host "Preparando beta Quri..." -ForegroundColor Yellow
& .\gradlew.bat testDebugUnitTest assembleDebug

$source = "app\build\outputs\apk\debug\app-debug.apk"
$destDir = "dist\beta"
$versionName = (Select-String -LiteralPath "app\build.gradle.kts" -Pattern 'versionName = "(.+)"').Matches.Groups[1].Value
$dest = Join-Path $destDir "quri-$versionName-debug.apk"

New-Item -ItemType Directory -Force -Path $destDir | Out-Null
Copy-Item -LiteralPath $source -Destination $dest -Force

Write-Host "Beta preparada correctamente:" -ForegroundColor Green
Get-Item -LiteralPath $dest | Select-Object FullName, Length, LastWriteTime
Write-Host ""
Write-Host "Siguiente paso si quieres distribuir por Firebase:" -ForegroundColor Yellow
Write-Host '.\scripts\upload-firebase-beta.ps1 -Groups "quri-testers"'
