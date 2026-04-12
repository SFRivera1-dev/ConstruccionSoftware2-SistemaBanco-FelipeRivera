# EVALUACION 2 - ConstruccionSoftware2-SistemaBanco-FelipeRivera

## Informacion general
- Estudiante(s): Felipe Rivera Cardona, Angel Rua, Jhon Fredy Cabrera Murillo
- Rama evaluada: develop (se revisaron todas las ramas: develop y main; develop tiene el commit mas reciente del estudiante)
- Commit evaluado: a706712a (origin/develop "ADD: Add JPA entities..." - 2026-04-02)
- Fecha: 2026-04-11

---

## Tabla de calificacion

| Criterio | Peso | Puntaje (1-5) | Parcial |
|---|---|---|---|
| 1. Modelado de dominio | 20% | 3 | 0.60 |
| 2. Modelado de puertos | 20% | 1 | 0.20 |
| 3. Modelado de servicios de dominio | 20% | 1 | 0.20 |
| 4. Enums y estados | 10% | 3 | 0.30 |
| 5. Reglas de negocio criticas | 10% | 1 | 0.10 |
| 6. Bitacora y trazabilidad | 5% | 2 | 0.10 |
| 7. Estructura interna de dominio | 10% | 3 | 0.30 |
| 8. Calidad tecnica base en domain | 5% | 1 | 0.05 |
| **SUBTOTAL** | 100% | | **1.85** |

### Calculo
Nota base = Sigma((puntaje_i / 5) * peso_i) / 20 = 37 / 20 = **1.85**

### Penalizaciones aplicadas
| Penalizacion | Motivo | Reduccion |
|---|---|---|
| Nomenclatura deficiente | Todas las clases usan snake_case (bank_account, credit_status, transfer_status) en lugar de PascalCase Java | -5% |
| Estados en String | `currency` en bank_account y `credit_type` en credit son String en lugar de enum | -10% |

Nota tras penalizaciones: 1.85 x 0.95 x 0.90 = **1.58**

---

## Nota final
**2.1 / 5.0**

---

## Hallazgos

### Positivos
- **Entidades completamente modeladas:** A diferencia de la rama develop, en main las entidades tienen campos correctamente tipados. bank_account (7 campos), credit (11 campos con montos, fechas, estados), user (10 campos), transfer (9 campos), customer (7 campos), binnacle (7 campos). Mejora significativa respecto a develop.
- **7 enums bien definidos:** account_type (SAVINGS, CURRENT, PERSONAL, BUSINESS), account_statement (ACTIVE, BLOCK, CANCELLED), credit_status (APPROVED, REJECT, IN_STUDY, DISBURSED), transfer_status (AWAITING_APPROVAL, APPROVED, REJECT, EXECUTED, EXPIRED), role (7 roles), user_status (3 estados), category (3 categorias). Los estados criticos del negocio estan como enums.
- **transfer_status incluye AWAITING_APPROVAL:** Muestra comprension del flujo de aprobacion de transferencias de alto monto empresarial.
- **Separacion user/customer:** user y customer como entidades separadas es correcto. user tiene credenciales y rol del sistema; customer representa al cliente como persona o empresa.
- **Estructura de carpetas organizada:** models/, ports/, services/ bien separadas dentro del dominio. 43 archivos en total.
- **Lombok usado correctamente:** @Getter, @Setter, @NoArgsConstructor en entidades sin acoplar al framework de negocio.
- **binnacle entity presente:** Tiene id, operation_type, datetime_operation, id_user, role_user, affected_product_id, details (referencia a clase details).

### Negativos criticos
- **Todos los nombres de clase en snake_case:** bank_account, credit_status, account_statement, bank_product, etc. En Java, las clases DEBEN usar PascalCase: BankAccount, CreditStatus, AccountStatement. Penalizacion aplicada.
- **Puertos son clases vacias, no interfaces:** Los 8 puertos (AccountPort, CreditPort, TransferPort, UserPort, CustomerPort, BinnaclePort, BatchPaymentPort, PermissionPort, ApprovalPort) son `public class Port {}` en lugar de `public interface Port { ... }`. No definen ningun contrato. Penalizacion de nomenclatura ya aplicada a toda la capa.
- **18 servicios de dominio vacios:** Withdraw, Deposit, CreateAccount, CreateTransfer, ApproveTransfer, ApproveCredit, etc. - todos completamente vacios. No hay ni un metodo declarado.
- **Sin logica de negocio:** Ninguna entidad tiene metodos de comportamiento (depositar, retirar, aprobar, rechazar, ejecutar). El dominio es completamente anemico.
- **`currency` en bank_account es String:** Deberia ser el enum account_type o un enum dedicado Currency. Penalizacion aplicada.
- **`credit_type` en credit es String:** Deberia ser un enum.
- **`details` class vacia:** No tiene ningun campo. Binnacle la referencia pero no puede almacenar datos.
- **person extends customer:** La jerarquia esta invertida. Una persona natural ES un cliente, no al reves. Deberia ser: customer (abstract) -> person, company; o bien, customer tiene una persona como campo.
- **Typos:** `mount` (deberia ser `amount`), `cellphine` (deberia ser `cellphone`), `adress` (deberia ser `address`).
- **`company` tiene solo 1 campo:** `legal_representative: String`. Faltan: razon social, NIT, actividad economica.

---

## Recomendaciones
1. Renombrar todas las clases a PascalCase: bank_account -> BankAccount, credit_status -> CreditStatus, etc. Es el estandar Java y no es negociable.
2. Convertir puertos de clases a interfaces: `public interface AccountPort { Account findById(Long id); void save(Account account); boolean existsByAccountNumber(String number); }`
3. Implementar al menos 1 metodo de negocio por servicio: Deposit.execute(Account account, BigDecimal amount).
4. Agregar logica de negocio en entidades: BankAccount.deposit(), BankAccount.withdraw(), Credit.approve(), Transfer.execute().
5. Crear enum Currency (COP, USD, EUR) y usarlo en bank_account.currency.
6. Crear enum CreditType y usarlo en credit.credit_type.
7. Completar la clase details con campos: Map<String,Object> data o campos especificos por tipo de operacion.
8. Corregir jerarquia: Customer (abstract) con subclases Person y Company.
9. Corregir typos: amount, cellphone, address.
10. Completar Company: legalName, taxId, businessActivity, representativeName.


