# Entrega beta 0.1.0

## APK

Ruta del APK interno:

`dist/beta/quri-0.1.0-beta-debug.apk`

## Version

- `versionName`: `0.1.0-beta`
- `versionCode`: `2`

## Verificacion

Ejecutado correctamente:

- `testDebugUnitTest`
- `assembleDebug`

## Como probar

1. Instalar el APK en un dispositivo Android.
2. Abrir la app sin explicar Quri previamente.
3. Usar el guion:

`docs/guion_test_beta_0_1_0.md`

## Eventos a revisar

En Logcat buscar:

`QuriAnalytics`

Eventos esperados:

- `onboarding_completed`
- `first_salary_simulated`
- `allocation_confirmed`
- `finance_summary_opened`

## Criterio clave

Al final, preguntar:

> Con tus palabras, que hace Quri?
