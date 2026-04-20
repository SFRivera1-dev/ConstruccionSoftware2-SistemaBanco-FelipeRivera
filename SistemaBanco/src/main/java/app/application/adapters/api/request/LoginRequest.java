package app.application.adapters.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "El documento es obligatorio")
    private String document;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}