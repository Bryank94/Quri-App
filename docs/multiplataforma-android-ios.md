# Adaptacion multiplataforma Android e iOS

## Estado actual

QuriTFG es actualmente una aplicacion Android nativa creada con:

- Kotlin y Android Gradle Plugin.
- Jetpack Compose para la interfaz.
- Room para persistencia local.
- Firebase Auth, Analytics y App Distribution.
- Google Sign-In mediante Play Services.

Este tipo de proyecto no puede compilarse directamente para iPhone. iOS requiere un binario propio generado con Xcode y firmado con una cuenta de Apple Developer.

## Objetivo recomendado

Para que Quri pueda instalarse tanto en Android como en iPhone hay dos caminos viables:

1. Mantener Android nativo y crear una app iOS nativa en SwiftUI.
2. Migrar la app a una tecnologia multiplataforma como Flutter, React Native o Kotlin Multiplatform.

Para este proyecto, la opcion mas equilibrada es Kotlin Multiplatform con Compose Multiplatform si se quiere reutilizar parte de la logica Kotlin existente. La opcion mas rapida para una app comercial completa suele ser Flutter, aunque exige reescribir la UI y adaptar la logica.

## Reutilizable

Se puede reutilizar o portar con bajo riesgo:

- Modelos de dominio en `datos/modelo`.
- Reglas de negocio de ahorro, metas, recompensas y resumen financiero.
- Casos de prueba de logica si se separan de dependencias Android.
- Configuracion funcional de Firebase a nivel conceptual.
- Assets visuales, iconos y textos.

## No reutilizable directamente en iOS

Hay que reemplazar o abstraer:

- `MainActivity`, manifiesto Android y recursos Android.
- Pantallas Jetpack Compose Android si no se migra a Compose Multiplatform.
- Room, que en iOS deberia cambiarse por SQLDelight, Core Data, Realm o almacenamiento Firebase.
- Google Play Services.
- Notificaciones Android.
- Firebase App Distribution para Android; en iOS debe usarse TestFlight o Firebase App Distribution iOS con IPA firmado.

## Plan de migracion recomendado

### Fase 1: Separar logica compartible

Crear un modulo compartido para dominio:

- `shared/domain`: modelos puros.
- `shared/usecase`: calculos, validaciones y reglas de negocio.
- `shared/repository`: contratos de repositorio sin Android.

Evitar dependencias como `Context`, Room, Compose Android, Firebase Android SDK o Play Services en esta capa.

### Fase 2: Sustituir almacenamiento local

Elegir una solucion comun:

- SQLDelight si se usa Kotlin Multiplatform.
- Firebase Firestore si se quiere sincronizacion cloud.
- Core Data solo si se crea iOS nativo.

### Fase 3: Crear cliente iOS

Opciones:

- SwiftUI: app iOS nativa, mejor integracion con iPhone.
- Compose Multiplatform iOS: mayor reutilizacion de Kotlin.
- Flutter: una sola UI para Android/iOS, pero requiere reescritura.

### Fase 4: Autenticacion y Firebase

Crear una app iOS en Firebase con bundle id propio, por ejemplo:

`com.quri.app`

Configurar:

- `GoogleService-Info.plist` para iOS.
- Firebase Auth iOS.
- Analytics iOS.
- Sign in with Apple si se publica en App Store y se ofrece login social.

### Fase 5: Distribucion

Android:

- APK/AAB desde Android Studio o Gradle.
- Google Play Console para produccion.
- Firebase App Distribution para beta.

iPhone:

- Compilacion en macOS con Xcode.
- Cuenta Apple Developer.
- Certificados y provisioning profiles.
- TestFlight para beta.
- App Store Connect para produccion.

## Entregables minimos para decir que Quri es Android + iOS

- Proyecto Android compilando APK/AAB.
- Proyecto iOS compilando IPA.
- Misma identidad visual.
- Misma autenticacion.
- Misma persistencia/sincronizacion de datos.
- Flujo de onboarding, login, metas, gastos, historial, progreso y perfil disponible en ambas plataformas.
- Politica de privacidad y permisos revisados para Google Play y App Store.

## Siguiente paso tecnico

Estrategia escogida: Kotlin Multiplatform.

Primer avance aplicado:

- Nuevo modulo `:shared` con targets Android e iOS.
- Modelos comunes `MovimientoFinanciero` y `FondoAhorro`.
- Parser de fecha comun sin dependencia Android/JVM.
- Motor comun `RecompensasQuriComun`.
- Tests comunes para recompensas.
- La app Android conserva su API `RecompensasQuri`, pero ahora delega en el motor compartido.

Segundo avance aplicado:

- Motor comun `BancoDemoComun` en `:shared`.
- Reglas comunes `ReglasAutomaticasQuriComun`.
- Plan de reparto comun `PlanBancoDemoComun`.
- Estados comunes de fondos `EstadoFondoBancoComun`.
- Tests comunes para reparto automatico.
- La app Android conserva `BancoDemo`, `PlanBancoDemo` y `ReglasAutomaticasQuri`, pero delega en el motor compartido.

Siguientes pasos practicos:

- Extraer `MotorHabitoQuri` e insights financieros al modulo `:shared`.
- Sustituir dependencias directas de Room en la logica por modelos comunes.
- Elegir persistencia multiplataforma, preferiblemente SQLDelight o repositorios separados por plataforma.
- Crear el target visual iOS con SwiftUI o Compose Multiplatform.
