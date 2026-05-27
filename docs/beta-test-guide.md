# Guia de beta cerrada - Quri 0.1.2-beta

Objetivo: comprobar si Quri se entiende, si las recomendaciones de Fase 3 aportan valor y si el flujo principal no se rompe.

## Antes de empezar

No expliques la app. Solo pide que la usen y observa.

## Tareas para testers

1. Registrarse o iniciar sesion.
2. Crear dos fondos: uno urgente y otro a medio plazo.
3. Anadir un ingreso tipo nomina.
4. Anadir varios gastos, incluyendo uno innecesario.
5. Abrir Finanzas y leer la tarjeta "Fase 3: inteligencia financiera".
6. Editar un gasto desde Historial.
7. Eliminar un ingreso o gasto de prueba.
8. Activar recordatorios inteligentes.

## Preguntas al final

- Con tus palabras, que hace Quri?
- Que mensaje de Finanzas te parecio mas util?
- La salud financiera /100 se entiende?
- Te fiarias de las recomendaciones?
- Volverias a abrir Quri el mes que viene?
- Que parte te genero dudas?

## Criterios para considerar la beta lista

- El tester entiende Quri sin explicacion externa.
- Puede crear fondos y registrar movimientos sin ayuda.
- Entiende por que Quri le recomienda reforzar o recortar algo.
- Puede corregir un error desde Historial.
- No confunde Finanzas con Analisis.

## Eventos Firebase a revisar

- onboarding_completed
- first_goal_created
- first_salary_simulated
- allocation_confirmed
- finance_summary_opened
- financial_intelligence_opened
- habit_notifications_enabled
