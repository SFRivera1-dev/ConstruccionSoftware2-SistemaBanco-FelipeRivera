package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.CustomerPersonEntity;
import app.application.adapters.persistence.sql.repositories.CustomerPersonRepository;
import app.domain.models.Customer;
import app.application.adapters.persistence.sql.entities.CustomerCompanyEntity;
import app.application.adapters.persistence.sql.repositories.CustomerCompanyRepository;
import app.domain.models.CustomerPerson;
import app.domain.models.CustomerCompany;
import app.domain.ports.ClientPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientPersistenceAdapter implements ClientPort {

    private final CustomerPersonRepository customerPersonRepository;
    private final CustomerCompanyRepository customerCompanyRepository;

    @Autowired
    public ClientPersistenceAdapter(CustomerPersonRepository customerPersonRepository,
                                    CustomerCompanyRepository customerCompanyRepository) {
        this.customerPersonRepository = customerPersonRepository;
        this.customerCompanyRepository = customerCompanyRepository;
    }

    @Override
    public Customer findByDocument(Long document) {
        CustomerPersonEntity personEntity = customerPersonRepository.findByDocument(document);
        if (personEntity != null) {
            return toPersonModel(personEntity);
        }
        CustomerCompanyEntity companyEntity = customerCompanyRepository.findByDocument(document);
        if (companyEntity != null) {
            return toCompanyModel(companyEntity);
        }
        return null;
    }

    @Override
    public Customer findById(Long id) {
        return customerPersonRepository.findById(id)
                .map(this::toPersonModel)
                .orElse(null);
    }

    private CustomerPerson toPersonModel(CustomerPersonEntity e) {
        CustomerPerson person = new CustomerPerson();
        person.setName(e.getName());
        person.setDocument(e.getDocument());
        person.setEmail(e.getEmail());
        person.setCellphone(e.getCellphone());
        person.setAdress(e.getAddress());
        person.setBirthdate(e.getBirthdate());
        person.setCustomerRole(e.getCustomerRole());
        return person;
    }

    private CustomerCompany toCompanyModel(CustomerCompanyEntity e) {
        CustomerCompany company = new CustomerCompany();
        company.setName(e.getName());
        company.setDocument(e.getDocument());
        company.setEmail(e.getEmail());
        company.setCellphone(e.getCellphone());
        company.setAdress(e.getAddress());
        company.setLegalRepresentative(e.getLegalRepresentative());
        company.setCustomerRole(e.getCustomerRole());
        return company;
    }
}