package app.application.adapters.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositWithdrawRequest {

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String accountNumber;

    @Positive(message = "El monto debe ser mayor que cero")
    private BigDecimal amount;
}