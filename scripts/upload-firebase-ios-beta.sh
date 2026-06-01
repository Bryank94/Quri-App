#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${FIREBASE_IOS_APP_ID:-}" ]]; then
  echo "Falta FIREBASE_IOS_APP_ID. Ejemplo: export FIREBASE_IOS_APP_ID=1:241399068305:ios:xxxxxxxx"
  exit 1
fi

IPA_PATH="${1:-iosApp/build/export/iosApp.ipa}"
GROUPS="${FIREBASE_GROUPS:-quri-testers}"
NOTES="${FIREBASE_RELEASE_NOTES:-firebase-app-distribution/release-notes.txt}"

firebase appdistribution:distribute "$IPA_PATH" \
  --app "$FIREBASE_IOS_APP_ID" \
  --groups "$GROUPS" \
  --release-notes-file "$NOTES"