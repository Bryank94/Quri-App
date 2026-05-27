# Checklist pre-beta Quri

## Objetivo

Validar si una persona entiende Quri sin explicacion externa:

> Mi sueldo se organiza automaticamente en fondos virtuales.

## Flujo que debe probarse

1. Abrir la app por primera vez.
2. Leer el onboarding de 3 pantallas.
3. Elegir una de estas acciones:
   - Crear primer fondo.
   - Probar con datos de ejemplo.
4. Entrar en Finanzas.
5. Simular una nomina.
6. Editar el reparto.
7. Confirmar el reparto.
8. Revisar Finanzas > Resumen.
9. Deshacer el reparto desde Historial.

## Datos demo esperados

- Fondos: Viaje, Emergencia, Sofa.
- Nomina demo: 1.300,00 EUR.
- Gastos demo: alquiler, supermercado, ocio.
- Plan mensual visible desde Finanzas > Resumen.
- Historial con "Nomina mayo detectada".

## Calidad UX

- En movil pequeno no debe solaparse texto.
- La barra inferior debe mostrar solo Inicio, Fondos, Finanzas y Perfil.
- Si no hay fondos: debe aparecer "Crea tu primer objetivo".
- Si no hay ingresos: debe aparecer "Simula tu primera nomina".
- Si no hay reglas activas: debe entenderse que Quri usa reparto inteligente.
- El preview antes de confirmar debe mostrar antes, reparto, despues e impacto.

## Seguridad del flujo

- Deshacer reparto no debe duplicar saldos.
- Deshacer reparto no debe dejar fondos en negativo.
- Deshacer reparto no debe borrar el historial: solo marcarlo como deshecho.
- Confirmar reparto no debe permitir asignar mas que la nomina.

## Prueba con usuarios

Dar solo estas tareas:

1. Crea una meta.
2. Simula una nomina.
3. Cambia el reparto.
4. Mira Finanzas > Resumen.
5. Explica que crees que hace Quri.

## Preguntas clave

- Entiende que es un fondo virtual?
- Confia en el reparto sugerido?
- Editaria manualmente el reparto?
- Volveria el mes siguiente?
- Pagaria por conexion bancaria real e insights automaticos?
