# Validacion beta Quri

## Objetivo de esta fase

No construir mas funcionalidades hasta responder:

> La gente entiende Quri y volveria a usarlo cada mes?

## Eventos instrumentados

La app ya registra localmente estos eventos en `quri_analytics` y Logcat:

- `onboarding_completed`
- `demo_data_loaded`
- `first_goal_created`
- `first_salary_simulated`
- `allocation_edited`
- `allocation_confirmed`
- `undo_used`
- `finance_summary_opened`

Cuando haya API key, se puede sustituir `LocalAnalyticsTracker` por `PostHogAnalyticsTracker`.

## Sesiones de usuario

Grabar 5 sesiones iniciales, mirando:

- donde dudan
- que no entienden
- que tocan primero
- si entienden "fondos virtuales"
- si confian en el reparto sugerido
- si abren Finanzas > Resumen sin ayuda

## Tareas para cada tester

1. Crea una meta.
2. Simula una nomina.
3. Cambia el reparto.
4. Mira Finanzas > Resumen.
5. Explica que crees que hace Quri.

## Interpretacion

- Lo entienden y vuelven: seguir con banco real solo lectura.
- Lo entienden pero no vuelven: mejorar habito, recordatorios y valor mensual.
- No lo entienden: reposicionar Quri y simplificar onboarding.

## Hipotesis a validar

La propuesta principal es:

> Mi sueldo se organiza automaticamente.

Pero tambien hay que observar si aman mas:

- tranquilidad mensual
- metas visuales
- simulacion
- evitar gastar demasiado
- predicciones de llegada

## Siguiente fase si valida bien

Integracion bancaria real solo lectura:

- Tink, TrueLayer o GoCardless Bank Account Data.
- Detectar nominas reales.
- Importar movimientos.
- Categorizar ingresos y gastos.
- Mantener fondos virtuales.
- No mover dinero real todavia.
