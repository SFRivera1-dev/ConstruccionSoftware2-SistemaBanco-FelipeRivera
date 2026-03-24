package app.domain.models;

import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class binnacle {
    private String idBinnacle, operationType;
    private Date datetimeOperation;
    private Long idUser;
    private role roleUser;
    private String affectedProductId;
    private details details;
}
