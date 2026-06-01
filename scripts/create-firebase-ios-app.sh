#!/usr/bin/env bash
set -euo pipefail

PROJECT_ID="${FIREBASE_PROJECT_ID:-quri2026}"
BUNDLE_ID="${IOS_BUNDLE_ID:-com.quri.app}"
APP_NAME="${IOS_APP_NAME:-Quri iOS}"

echo "Comprobando sesion Firebase..."
firebase login:list >/dev/null

echo "Creando app iOS en Firebase: $APP_NAME ($BUNDLE_ID) dentro de $PROJECT_ID"
firebase apps:create IOS "$APP_NAME" \
  --bundle-id "$BUNDLE_ID" \
  --project "$PROJECT_ID"

echo "Apps del proyecto:"
firebase apps:list --project "$PROJECT_ID"

echo "Copia el App ID iOS mostrado arriba y exportalo como FIREBASE_IOS_APP_ID."
echo "Despues descarga GoogleService-Info.plist desde Firebase Console y guardalo en iosApp/iosApp/GoogleService-Info.plist."