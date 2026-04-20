package app.application.adapters.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @NotBlank(message = "La cuenta origen es obligatoria")
    private String originAccount;

    @NotBlank(message = "La cuenta destino es obligatoria")
    private String destinationAccount;

    @Positive(message = "El monto debe ser mayor que cero")
    private BigDecimal mount;
}