# Firebase App Distribution - Quri

La app ya tiene configurado el plugin de Firebase App Distribution para la variante `debug`.

## Requisitos previos

1. Instala Firebase CLI si no lo tienes:
   `npm install -g firebase-tools`

2. Inicia sesion:
   `firebase login`

3. En Firebase Console, entra en App Distribution y crea un grupo de testers, por ejemplo:
   `quri-testers`

## Subir una beta a testers

Desde la raiz del proyecto:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat assembleDebug appDistributionUploadDebug -PfirebaseGroups=quri-testers
```

Tambien puedes enviar a emails concretos:

```powershell
.\gradlew.bat assembleDebug appDistributionUploadDebug -PfirebaseTesters="correo1@email.com,correo2@email.com"
```

## Donde editar las notas

Las notas que veran los testers estan en:

`firebase-app-distribution/release-notes.txt`

## Importante

- Sube el `versionCode` en `app/build.gradle.kts` antes de cada beta nueva.
- No subas archivos con emails reales de testers si el proyecto se va a compartir.
- Para subir desde CI se puede usar una cuenta de servicio y configurar credenciales, pero para tu caso ahora es mas simple usar `firebase login`.
