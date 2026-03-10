package app.domain.models;

import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class binnacle {
    private String id_binnacle, operation_type;
    private Date datetime_operation;
    private Long id_user;
    private role role_user;
    private String affected_product_id;
    private details details;
}
