package app.application.adapters.persistence.mongodb.documents;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import app.domain.models.Role;
import app.domain.models.Details;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "binnacle")
public class BinnacleDocument {

    @Id
    private String id;

    private String operationType;

    private Date datetimeOperation;

    private Long idUser;

    private Role roleUser;

    private String affectedProductId;

    private Details details;
}