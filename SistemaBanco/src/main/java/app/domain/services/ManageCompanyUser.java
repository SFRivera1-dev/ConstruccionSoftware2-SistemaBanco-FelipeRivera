package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Customer;
import app.domain.models.CustomerRole;
import app.domain.models.User;
import app.domain.ports.ClientPort;
import app.domain.ports.PermissionPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageCompanyUser {

    private final PermissionPort permissionPort;
    private final ClientPort clientPort;

    @Autowired
    public ManageCompanyUser(PermissionPort permissionPort, ClientPort clientPort) {
        this.permissionPort = permissionPort;
        this.clientPort = clientPort;
    }

    public List<User> getCompanyUsers(Long companyDocument) throws BusinessException {
        Customer company = clientPort.findByDocument(companyDocument);
        if (company == null) {
            throw new BusinessException("No existe una empresa con ese documento");
        }
        if (company.getCustomerRole() != CustomerRole.COMPANY_CLIENT) {
            throw new BusinessException("El documento no corresponde a una empresa");
        }
        return permissionPort.findUsersByCompany(companyDocument);
    }

    public void manageUser(Long companyDocument, Long targetUserId, boolean enable) throws BusinessException {
        Customer company = clientPort.findByDocument(companyDocument);
        if (company == null) {
            throw new BusinessException("No existe una empresa con ese documento");
        }
        if (company.getCustomerRole() != CustomerRole.COMPANY_CLIENT) {
            throw new BusinessException("El documento no corresponde a una empresa");
        }
        if (targetUserId == null) {
            throw new BusinessException("El ID del usuario es obligatorio");
        }

        if (enable) {
            permissionPort.grantPermission(companyDocument, targetUserId);
        } else {
            permissionPort.revokePermission(companyDocument, targetUserId);
        }
    }
}