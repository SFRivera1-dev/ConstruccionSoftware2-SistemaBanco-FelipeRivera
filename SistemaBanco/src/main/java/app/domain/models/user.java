package app.domain.models;

import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class user {
    private Long id_user, id_customer;
    private String name_user, document, email, cellphine;
    private Date birthdate;
    private String adress;
    private role role;
    private user_status user_status;
}
