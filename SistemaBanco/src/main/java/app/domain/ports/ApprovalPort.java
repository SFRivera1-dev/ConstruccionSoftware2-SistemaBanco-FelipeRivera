package app.domain.ports;

import java.util.List;

import app.domain.models.Transfer;

public interface ApprovalPort {
    List<Transfer> findPendingTrasnferByCompany(Long companyDocument);
    void approveTransfer(Long transferId, Long approverUserId);
    void rejectTransfer(Long transferId, Long approverUserId);

}
