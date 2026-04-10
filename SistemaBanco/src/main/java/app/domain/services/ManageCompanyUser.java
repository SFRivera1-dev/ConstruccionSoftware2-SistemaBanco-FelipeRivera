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

    // El supervisor de empresa puede ver y gestionar los usuarios operativos de su empresa
    public List<User> getCompanyUsers(Long companyDocument) throws BusinessException {
        Customer company = clientPort.findByDocument(companyDocument);
        if (company == null) {
            throw new BusinessException("No existe una empresa con ese documento");
        }

        // Regla: solo aplica para empresas
        if (company.getCustomerRole() != CustomerRole.COMPANY_CLIENT) {
            throw new BusinessException("El documento no corresponde a una empresa");
        }

        return permissionPort.findUsersByCompany(companyDocument);
    }

    // El supervisor activa o desactiva permisos de usuarios de su empresa
    public void enableUser(Long companyDocument, Long targetUserId) throws BusinessException {
        Customer company = clientPort.findByDocument(companyDocument);
        if (company == null) {
            throw new BusinessException("No existe una empresa con ese documento");
        }
        if (company.getCustomerRole() != CustomerRole.COMPANY_CLIENT) {
            throw new BusinessException("El documento no corresponde a una empresa");
        }
        if (targetUserId == null) {
            throw new BusinessException("El ID del usuario a habilitar es obligatorio");
        }

        permissionPort.grantPermission(companyDocument, targetUserId);
    }

    public void disableUser(Long companyDocument, Long targetUserId) throws BusinessException {
        Customer company = clientPort.findByDocument(companyDocument);
        if (company == null) {
            throw new BusinessException("No existe una empresa con ese documento");
        }
        if (company.getCustomerRole() != CustomerRole.COMPANY_CLIENT) {
            throw new BusinessException("El documento no corresponde a una empresa");
        }
        if (targetUserId == null) {
            throw new BusinessException("El ID del usuario a deshabilitar es obligatorio");
        }

        permissionPort.revokePermission(companyDocument, targetUserId);
    }
}
