package app.domain.models;

import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class User {
    private Long idUser, idCustomer;
    private String nameUser, document, email, cellphone;
    private Date birthdate;
    private String adress;
    private Role role; //null si es cliente
    private CustomerRole customerRole; // null si es empleado
    private UserStatus userStatus;
}
