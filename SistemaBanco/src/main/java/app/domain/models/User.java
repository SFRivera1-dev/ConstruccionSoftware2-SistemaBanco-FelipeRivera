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
    private String nameUser, email, cellphone;
    private Long document;
    private Date birthdate;
    private String adress;
    private Role role;
    private CustomerRole customerRole;
    private UserStatus userStatus;
}