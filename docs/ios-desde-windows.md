# Ejecutar iOS desde Windows

Windows no puede ejecutar el simulador oficial de iPhone porque Xcode solo existe en macOS. La alternativa mas eficaz para Quri es compilar iOS en un runner macOS de GitHub Actions.

## Opcion preparada: GitHub Actions macOS

Se ha anadido el workflow:

`.github/workflows/ios-simulator-build.yml`

Que hace lo siguiente:

1. Arranca un Mac en la nube.
2. Instala JDK 17.
3. Compila el modulo Kotlin Multiplatform `:shared` desde Xcode.
4. Compila `iosApp` para iPhone Simulator.
5. Sube un artefacto descargable: `quri-ios-simulator-app.zip`.

## Como lanzarlo

1. Sube estos cambios al repositorio.
2. En GitHub abre la pestana `Actions`.
3. Entra en `iOS simulator build`.
4. Pulsa `Run workflow`.
5. Cuando termine, abre la ejecucion y descarga `quri-ios-simulator-app`.

## Importante

El archivo generado es para simulador iOS, no para instalar por WhatsApp ni en un iPhone fisico.

Para instalar en iPhone real necesitas una IPA firmada con Apple Developer. Ese flujo ya esta documentado en `iosApp/README.md` y requiere:

- macOS con Xcode.
- Apple Developer Team ID.
- Certificados/perfiles de firma.
- Firebase App Distribution o TestFlight para repartirla.

## Alternativas

- GitHub Actions macOS: mejor para comprobar que iOS compila desde Windows.
- Codemagic/Bitrise: mejor si quieres CI movil especializado con firma iOS mas guiada.
- MacStadium/MacinCloud: mejor si quieres controlar un Mac remoto completo.
- BrowserStack/Appetize/Sauce Labs: mejor para probar en dispositivos remotos, normalmente despues de tener una build iOS generada.
