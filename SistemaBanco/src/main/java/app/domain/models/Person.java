package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

public class Person {
    private String name;
    private Long document;
    private String email, cellphone, adress;
}
