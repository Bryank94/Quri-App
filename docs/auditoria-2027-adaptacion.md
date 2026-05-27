# Adaptacion de Quri a la auditoria 2027

Este proyecto queda alineado con la auditoria `Auditoria_App_Ahorro_2027.docx` en los puntos implementables dentro de la app Android local.

## Cambios aplicados

- Privacidad: `allowBackup=false`, backup completo desactivado y reglas de extraccion sin datos de usuario.
- Seguridad de credenciales: contrasenas locales con PBKDF2-HMAC-SHA256, salt de 32 bytes, 120.000 iteraciones y comparacion constante.
- Acceso interno: el panel de administracion solo se expone en builds debug; no aparece como entrada visible en builds de release.
- Recompensas: motor auditable para puntos, umbral minimo de canje, conversion `1.000 puntos = 0,50 EUR` y tope mensual.
- UX auditada: home y progreso muestran puntos, valor canjeable y distancia al siguiente canje.
- Pruebas: tests unitarios sobre la logica critica de recompensas y canje.

## Pendiente para aptitud completa 2027

- Backend global multi-region, API Gateway, observabilidad y SIEM.
- Passkeys/FIDO2, TOTP real y step-up authentication para operaciones sensibles.
- KYC, antifraude avanzado y controles regulatorios PSD2/PSD3, DORA y AML.
- Cifrado de base de datos local con gestion de claves en Android Keystore o solucion equivalente.
- Pipeline CI/CD con SAST, DAST, pentest y cobertura minima del 80% en logica critica.

## Criterio de entrega

La app queda preparada como MVP adaptado a la auditoria, no como producto financiero certificado para lanzamiento global. Los elementos que requieren infraestructura, proveedores regulados o backend distribuido quedan documentados como trabajo obligatorio antes de release comercial.
