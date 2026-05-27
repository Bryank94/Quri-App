param(
    [string]$Groups = "",
    [string]$Testers = ""
)

$ErrorActionPreference = "Stop"

$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

if (-not (Get-Command firebase -ErrorAction SilentlyContinue)) {
    throw "Firebase CLI no esta instalado. Ejecuta: npm install -g firebase-tools"
}

$loginList = firebase login:list 2>&1 | Out-String
if ($loginList -match "No authorized accounts") {
    throw "No hay sesion Firebase iniciada. Ejecuta primero: firebase login"
}

$argsList = @("assembleDebug", "appDistributionUploadDebug")

if ($Groups.Trim().Length -gt 0) {
    $argsList += "-PfirebaseGroups=$Groups"
}

if ($Testers.Trim().Length -gt 0) {
    $argsList += "-PfirebaseTesters=$Testers"
}

if ($Groups.Trim().Length -eq 0 -and $Testers.Trim().Length -eq 0) {
    throw "Indica un grupo o testers. Ejemplo: .\scripts\upload-firebase-beta.ps1 -Groups quri-testers"
}

Write-Host "Subiendo Quri beta a Firebase App Distribution..." -ForegroundColor Yellow
& .\gradlew.bat @argsList
