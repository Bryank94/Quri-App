#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${APPLE_TEAM_ID:-}" ]]; then
  echo "Falta APPLE_TEAM_ID. Ejemplo: export APPLE_TEAM_ID=ABCDE12345"
  exit 1
fi

cd "$(dirname "$0")/../iosApp"

xcodebuild archive \
  -project iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Release \
  -archivePath build/Quri.xcarchive \
  -destination generic/platform=iOS \
  DEVELOPMENT_TEAM="$APPLE_TEAM_ID"

xcodebuild -exportArchive \
  -archivePath build/Quri.xcarchive \
  -exportPath build/export \
  -exportOptionsPlist ExportOptions.plist

echo "IPA generado en: iosApp/build/export/iosApp.ipa"