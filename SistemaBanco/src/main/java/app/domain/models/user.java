package app.domain.models;

import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class user {
    private Long idUser, idCustomer;
    private String nameUser, document, email, cellphone;
    private Date birthdate;
    private String adress;
    private role role; //null si es cliente
    private customerRole customerRole; // null si es empleado
    private userStatus userStatus;
}
