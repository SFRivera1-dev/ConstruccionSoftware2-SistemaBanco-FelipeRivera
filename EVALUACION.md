# EVALUACIÓN - ConstruccionSoftware2-SistemaBanco-FelipeRivera

## Información General
- **Estudiantes:** Felipe Rivera Cardona, Angel Rua, Jhon Fredy Cabrera Murillo
- **Rama evaluada:** develop
- **Fecha de evaluación:** 2026-03-23

---

## Tabla de Calificación

| # | Criterio | Peso | Puntaje (1–5) | Nota ponderada |
|---|---|---|---|---|
| 1 | Modelado de dominio | 25% | 3 | 0.75 |
| 2 | Relaciones entre entidades | 15% | 3 | 0.45 |
| 3 | Uso de Enums | 15% | 4 | 0.60 |
| 4 | Manejo de estados | 5% | 4 | 0.20 |
| 5 | Tipos de datos | 5% | 2 | 0.10 |
| 6 | Separación Usuario vs Cliente | 10% | 4 | 0.40 |
| 7 | Bitácora | 5% | 2 | 0.10 |
| 8 | Reglas básicas de negocio | 5% | 2 | 0.10 |
| 9 | Estructura del proyecto | 10% | 4 | 0.40 |
| 10 | Repositorio | 10% | 4 | 0.40 |
| **TOTAL** | | **100%** | | **3.50 / 5 (base)** |

> Nota base = (3/5×0.25 + 3/5×0.15 + 4/5×0.15 + 4/5×0.05 + 2/5×0.05 + 4/5×0.10 + 2/5×0.05 + 2/5×0.05 + 4/5×0.10 + 4/5×0.10) × 5 = 0.70 × 5 = **3.50**

---

## Penalizaciones

| Penalización | Descuento | Nota resultante |
|---|---|---|
| Clases mal nombradas (convención snake_case en todos los nombres: `bank_account`, `credit`, `transfer`, `user`, etc. — Java exige PascalCase para clases) | -10% | 3.50 × 0.90 = 3.15 |
| Variables mal nombradas (snake_case en todos los atributos: `account_number`, `id_credit`, `credit_type`, `opening_date`, etc.) | -10% | 3.15 × 0.90 = **2.84** |

---

## Bonus

Ninguno aplicable.

---

## Nota Final: **2.8 / 5.0**

---

## Análisis por Criterio

### 1. Modelado de dominio — 3/5
Entidades identificadas: `customer`, `person` (persona natural, extiende `customer`), `company` (empresa, clase independiente), `user`, `bank_account`, `credit` (préstamo), `transfer`, `bank_product`, `binnacle` (bitácora), `details`.  
**Problemas:**
- `company` solo tiene `legal_representative: String` — estructura muy pobre para modelar una empresa cliente.
- No hay jerarquía clara de cliente: `person extends customer` funciona para persona natural, pero `company` es una clase separada que no extiende `customer`. Debería ser: `customer` (abstracta) → `person` y `company`.
- `details` clase completamente vacía.

### 2. Relaciones entre entidades — 3/5
`customer` tiene `List<bank_product> bankproducts` ✓.  
`binnacle` tiene `details details` ✓ (referencia al objeto, aunque `details` está vacío).  
`bank_account.account_holderID: String` — relación por ID, no por referencia.  
`credit.customer_request_id: String` — relación por ID.  
`transfer.origin_account: String` y `transfer.destination_account: String` — cuentas por ID.

### 3. Uso de Enums — 4/5
Enums implementados: `account_statement`, `account_type`, `category`, `credit_status`, `role`, `transfer_status`, `user_status` — la mayoría de los catálogos están como enums ✓.  
**Faltantes:** `currency` en `bank_account` es `String`, `credit_type` en `credit` es `String`.

### 4. Manejo de estados — 4/5
`bank_account.account_statement: account_statement` ✓, `credit.credit_status: credit_status` ✓, `transfer.transfer_status: transfer_status` ✓, `user.user_status: user_status` ✓. La mayoría de los estados usan enums correctamente.

### 5. Tipos de datos — 2/5
`Float` para montos monetarios (debería ser `BigDecimal`). `java.sql.Date` para fechas (debería ser `java.time.LocalDate`/`LocalDateTime`). Hay un typo en `user`: `cellphine` (en lugar de `cellphone`).

### 6. Separación Usuario vs Cliente — 4/5
`user` tiene `id_customer: Long` para vincularse a `customer`, representando dos entidades distintas ✓. Sin embargo, el enum `role` mezcla roles del sistema (`SERVICE_ADVISOR`, `BANK_INTERNAL_ANALYST`) con tipos de cliente (`CUSTOMER_PERSON`, `COMPANY_CLIENT`) — deberían estar en enums separados.

### 7. Bitácora — 2/5
`binnacle` existe con los campos esperados (`id_binnacle`, `operation_type`, `datetime_operation`, `role_user`, `affected_product_id`, `details`). Sin embargo, la clase `details` está completamente vacía — sin ningún campo ni método implementado. La bitácora necesita una estructura de datos flexible para los detalles de la operación.

### 8. Reglas básicas de negocio — 2/5
Se identificaron correctamente 18 casos de uso de negocio (`ApproveCredit`, `RejectCredit`, `CreateTransfer`, `ApproveTransfer`, `Deposit`, `Withdraw`, `DelegatePermissions`, etc.) — lo cual muestra una buena comprensión del dominio. Sin embargo, **todos los servicios y puertos están completamente vacíos** (clases sin ningún método ni atributo). El reconocimiento de use cases suma, pero la falta de implementación es significativa.

### 9. Estructura del proyecto — 4/5
Arquitectura bien planteada con separación en capas: `domain/models`, `domain/ports`, `domain/services`. El diseño muestra comprensión de clean architecture / hexagonal architecture. La estructura de paquetes es adecuada aunque todo está vacío a nivel de implementación.

### 10. Repositorio — 4/5
- **Nombre:** `ConstruccionSoftware2-SistemaBanco-FelipeRivera` ✓ descriptivo y con formato adecuado.
- **Commits:** 10 commits con historial progresivo en `develop`. Sin convención ADD/CHG; mensajes informales ("arreglos", "avances", "autores").
- **README:** Tiene título, descripción ("Sistema Gestion de banco") e integrantes del equipo ✓. Faltan: tecnologías y pasos de ejecución.
- **Ramas:** Tiene `develop` ✓.
- **Tag de entrega:** No existe.

---

## Fortalezas
- Identificación correcta y abundante de casos de uso de negocio (18 servicios planteados).
- Estructura de proyecto con clean architecture bien conceptualizada (models/ports/services).
- Buenos enums para la mayoría de catálogos y estados del dominio.
- README con información del equipo y descripción del proyecto.
- Buena actividad en commits (10 commits).

## Oportunidades de mejora
- **Crítico:** Cambiar todos los nombres de clases de snake_case a PascalCase (`BankAccount`, `Credit`, `Transfer`, etc.) — es una convención obligatoria en Java.
- **Crítico:** Cambiar todos los nombres de atributos de snake_case a camelCase (`accountNumber`, `idCredit`, etc.).
- **Crítico:** Implementar los servicios y puertos (actualmente vacíos): `Deposit`, `Withdraw`, `ApproveCredit`, `CreateTransfer`, etc.
- Implementar la clase `details` / `Details` con estructura flexible `Map<String, Object>`.
- Corregir la jerarquía: `customer` (abstracta) → `person` y `company`.
- Ampliar la clase `company` con los campos requeridos de empresa cliente.
- Agregar enum `currency` para moneda y `credit_type` para tipo de préstamo.
- Cambiar `Float` a `BigDecimal` y `java.sql.Date` a `java.time.LocalDate`.
- Completar el README con tecnologías y pasos de ejecución.
- Agregar tag de entrega y adoptar convención de commits ADD/CHG.
