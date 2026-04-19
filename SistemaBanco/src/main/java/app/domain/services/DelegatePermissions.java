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
public class DelegatePermissions {

    private final PermissionPort permissionPort;
    private final ClientPort clientPort;

    @Autowired
    public DelegatePermissions(PermissionPort permissionPort, ClientPort clientPort) {
        this.permissionPort = permissionPort;
        this.clientPort = clientPort;
    }

    public void execute(Long companyDocument, Long targetUserId, boolean grant) throws BusinessException {
        Customer company = clientPort.findByDocument(companyDocument);
        if (company == null) {
            throw new BusinessException("No existe una empresa con ese documento");
        }
        if (company.getCustomerRole() != CustomerRole.COMPANY_CLIENT) {
            throw new BusinessException("Solo las empresas pueden delegar permisos a usuarios operativos");
        }
        if (targetUserId == null) {
            throw new BusinessException("El ID del usuario destino es obligatorio");
        }

        if (grant) {
            permissionPort.grantPermission(companyDocument, targetUserId);
        } else {
            permissionPort.revokePermission(companyDocument, targetUserId);
        }
    }

    public List<User> findUsersByCompany(Long companyDocument) throws BusinessException {
        Customer company = clientPort.findByDocument(companyDocument);
        if (company == null) {
            throw new BusinessException("No existe una empresa con ese documento");
        }
        return permissionPort.findUsersByCompany(companyDocument);
    }
}