# EVALUACION 2 - ConstruccionSoftware2-SistemaBanco-FelipeRivera

## Informacion general
- Estudiante(s): Felipe Rivera Cardona, Angel Rua, Jhon Fredy Cabrera Murillo
- Rama evaluada: develop
- Commit evaluado: 603c32c6cb83106428cc08a5af4605d2048d07ed
- Fecha: 2026-04-11

---

## Tabla de calificacion

| Criterio | Peso | Puntaje (1-5) | Parcial |
|---|---|---|---|
| 1. Modelado de dominio | 20% | 2 | 0.40 |
| 2. Modelado de puertos | 20% | 1 | 0.20 |
| 3. Modelado de servicios de dominio | 20% | 1 | 0.20 |
| 4. Enums y estados | 10% | 3 | 0.30 |
| 5. Reglas de negocio criticas | 10% | 1 | 0.10 |
| 6. Bitacora y trazabilidad | 5% | 2 | 0.10 |
| 7. Estructura interna de dominio | 10% | 3 | 0.30 |
| 8. Calidad tecnica base en domain | 5% | 1 | 0.05 |
| **SUBTOTAL** | 100% | | **1.65** |

### Calculo
Nota base = Σ((puntaje_i / 5) * peso_i) / 20 = 33 / 20 = **1.65**

### Penalizaciones aplicadas
| Penalizacion | Motivo | Reduccion |
|---|---|---|
| Nomenclatura deficiente | Todas las clases, enums y campos usan snake_case en vez de PascalCase/camelCase Java | -5% |
| Estados en String | `currency` en bank_account es String en lugar de usar Currency enum | -10% |

Nota tras penalizaciones: 1.65 × 0.95 × 0.90 = **1.41**

---

## Nota final
**1.4 / 5.0**

---

## Hallazgos

### Positivos
- **Estructura hexagonal presente:** Carpetas models/, ports/, services/ bien diferenciadas dentro del dominio.
- **Cantidad numerosa de artefactos:** 43 archivos en dominio indica voluntad de implementar un dominio completo.
- **Enums definidos:** account_statement, account_type, category, credit_status, role, transfer_status, user_status existen con valores razonables.
- **Bitacora (binnacle):** La entidad binnacle existe con campos, y BinnaclePort fue declarado (aunque vacio).
- **Separacion user/customer:** user y customer como entidades separadas es un buen diseño.
- **Presencia de excepciones de dominio:** AlreadyExistsException, InsufficientFoundsException (sic) en el paquete exceptions.

### Negativos criticos
- **snake_case en nombres de clases Java:** Todas las clases del dominio usan snake_case (`bank_account`, `credit_status`, `transfer_status`, `account_statement`). En Java, las clases deben tener PascalCase y los campos camelCase. Esta es una violacion grave de las convenciones de Java. Penalizacion aplicada.
- **Puertos son clases vacias, no interfaces:** `AccountPort`, `LoanPort`, `TransferPort`, `UserPort`, `CustomerPort`, `BinnaclePort`, `DetailPort`, `TransferPort` son clases Java (`public class AccountPort {}`) en lugar de interfaces. No definen ningun contrato. Un puerto vacio no tiene valor como contrato hexagonal.
- **Servicios de dominio son clases vacias:** Los 18 servicios declarados (CreateAccount, ApproveTransfer, CreateUser, etc.) estan completamente vacios. No hay ni un metodo implementado ni declarado.
- **Jerarquia invertida:** `person extends customer` — la relacion deberia ser al reves: un cliente puede ser una persona natural. Customer extiende Person (si Person es mas general), o ambas son independientes.
- **campo `currency` como String:** bank_account tiene `String currency` en lugar de usar el enum `account_statement` (o un enum dedicado Currency). Penalizacion aplicada.
- **Clase `details` completamente vacia:** No tiene ningun campo. No sirve como modelo de datos para detalles de operaciones.
- **Clase `company` minima:** Solo tiene un campo `legal_representative` de tipo String. Le faltan: razon social, NIT, actividad economica.
- **Sin logica de negocio:** Ninguna entidad tiene metodos de negocio (depositar, retirar, aprobar, ejecutar).
- **Acoplamiento de herencia incorrecto:** La clase `person` hereda de `customer` cuando deberia ser al reves o no heredarse.

---

## Recomendaciones
1. **Renombrar todas las clases a PascalCase:** `bank_account` → `BankAccount`, `credit_status` → `CreditStatus`, etc. Es el estandar Java y no es negociable.
2. **Renombrar todos los campos a camelCase:** `account_number` → `accountNumber`, `legal_representative` → `legalRepresentative`, etc.
3. **Convertir puertos a interfaces:** `public interface AccountPort { ... }` con metodos `save`, `findById`, `findByAccountNumber`.
4. **Agregar metodos a los servicios de dominio:** Al menos un caso de uso implementado por servicio.
5. **Corregir jerarquia:** `Customer` debe ser independiente o extender de `Person`; `Person` no debe extender de `Customer`.
6. **Usar Currency enum:** Cambiar `String currency` en `BankAccount` por el enum correspondiente.
7. **Implementar logica de negocio:** BankAccount.deposit(), BankAccount.withdraw() con validación de saldo; Loan.approve(), Loan.reject().
8. **Completar la clase `details`:** Definir campos para almacenar detalles de operaciones (operationType, amount, date, etc.) o usar `Map<String, Object>` como dato variable.
9. **Corregir el nombre `InsufficientFoundsException`:** El correcto es `InsufficientFundsException`.
