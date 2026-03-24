package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class bankProduct {
    private String codeProduct, nameProduct;
    private category category;
    private Boolean requiresApproval;
}

