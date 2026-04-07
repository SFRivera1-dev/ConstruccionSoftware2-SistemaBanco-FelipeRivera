package app.domain.ports;

import java.util.List;

import app.domain.models.User;

public interface PermissionPort {
    void grantPermission(Long companyDocument, Long targetUserId);
    void revokePermission(Long companyDocument, Long targetUserId);
    List<User> findUsersByCompany(Long companyDocument);

}
