package app.application.adapters.api.request;

import app.domain.models.AccountType;
import app.domain.models.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String accountNumber;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private AccountType accountType;

    @NotNull(message = "La moneda es obligatoria")
    private Currency currency;

    @NotBlank(message = "El documento del titular es obligatorio")
    private String holderDocument;
}