package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class BankProduct {
    private String codeProduct, nameProduct;
    private Category category;
    private Boolean requiresApproval;
}

