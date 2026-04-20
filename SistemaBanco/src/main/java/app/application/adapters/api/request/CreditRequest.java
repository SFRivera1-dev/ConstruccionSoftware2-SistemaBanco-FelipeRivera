package app.application.adapters.api.request;

import app.domain.models.CreditType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreditRequest {

    @NotNull(message = "El tipo de crédito es obligatorio")
    private CreditType creditType;

    @NotNull(message = "El documento del cliente es obligatorio")
    private Long customerDocument;

    @Positive(message = "El monto solicitado debe ser mayor que cero")
    private BigDecimal amountRequested;

    @Positive(message = "La tasa de interés debe ser mayor que cero")
    private BigDecimal interestRate;

    @Positive(message = "El plazo debe ser mayor que cero")
    private Integer termMonths;
}