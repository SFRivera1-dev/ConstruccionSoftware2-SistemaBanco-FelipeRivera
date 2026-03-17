package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class customer {
    private String name;
    private Long number_identification;
    private String email, cellphone, adress;
    private role role;
    private List<bank_product> bankproducts;
}
