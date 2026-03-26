package app.domain.models;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class Customer extends Person{
    private CustomerRole customerRole;
    private List<BankProduct> bankProducts;
}
