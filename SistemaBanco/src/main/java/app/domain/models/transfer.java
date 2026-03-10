package app.domain.models;

import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor


public class transfer {
    private Long id_transfer;
    private String origin_account, destination_account;
    private Float mount;
    private Date creation_date, approval_date;
    private transfer_status transfer_status;
    private Integer creator_user_id, approved_user_id;
}
