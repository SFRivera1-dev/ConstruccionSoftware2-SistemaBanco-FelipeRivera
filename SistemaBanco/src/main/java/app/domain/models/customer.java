package app.domain.models;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class customer extends person{
    private customerRole customerRole;
    private List<bankProduct> bankProducts;
}
