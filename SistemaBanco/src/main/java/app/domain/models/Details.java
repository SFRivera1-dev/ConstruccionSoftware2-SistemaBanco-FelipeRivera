package app.domain.models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Details {
    // Para transferencias
    private BigDecimal mount;
    private BigDecimal balanceBeforeOrigin;
    private BigDecimal balanceAfterOrigin;
    private BigDecimal balanceBeforeDestination;
    private BigDecimal balanceAfterDestination;

    // Para créditos
    private BigDecimal amountApproved;
    private BigDecimal interestRate;
    private String previousStatus;
    private String newStatus;

    // Para vencimiento de transferencia
    private String reason;

    // Para cuentas
    private String accountNumber;
    private String accountType;
}