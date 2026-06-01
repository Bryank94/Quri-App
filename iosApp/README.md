# Quri iOS

Proyecto iOS SwiftUI preparado para consumir el modulo Kotlin Multiplatform `:shared`.

## Requisitos

- macOS con Xcode instalado.
- JDK accesible desde la terminal.
- Cuenta Apple Developer para instalar en iPhone fisico o generar IPA.
- App iOS creada en Firebase con bundle id `com.quri.app`.
- `GoogleService-Info.plist` descargado desde Firebase Console y copiado en `iosApp/iosApp/GoogleService-Info.plist` cuando se active Firebase iOS.

## Abrir en Xcode

1. Abre `iosApp/iosApp.xcodeproj`.
2. Selecciona el target `iosApp`.
3. En Signing & Capabilities, elige tu Team de Apple Developer.
4. Conecta un iPhone o selecciona un simulador.
5. Ejecuta Build/Run.

Xcode ejecuta esta fase antes de compilar Swift:

```sh
cd "$SRCROOT/.."
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

## Generar IPA para Firebase App Distribution

Desde macOS:

```sh
cd iosApp
xcodebuild archive \
  -project iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Release \
  -archivePath build/Quri.xcarchive \
  -destination generic/platform=iOS \
  DEVELOPMENT_TEAM=TU_TEAM_ID

xcodebuild -exportArchive \
  -archivePath build/Quri.xcarchive \
  -exportPath build/export \
  -exportOptionsPlist ExportOptions.plist
```

El IPA quedara en `iosApp/build/export/iosApp.ipa`.
## Scripts listos

Crear app iOS en Firebase desde macOS, con Firebase CLI autenticado:

```sh
./scripts/create-firebase-ios-app.sh
```

Generar IPA firmado desde macOS, con tu Apple Team ID:

```sh
export APPLE_TEAM_ID="TU_TEAM_ID"
./scripts/build-ios-ipa.sh
```

Subir IPA a Firebase App Distribution:

```sh
export FIREBASE_IOS_APP_ID="1:241399068305:ios:TU_APP_ID_IOS"
export FIREBASE_GROUPS="quri-testers"
./scripts/upload-firebase-ios-beta.sh iosApp/build/export/iosApp.ipa
```