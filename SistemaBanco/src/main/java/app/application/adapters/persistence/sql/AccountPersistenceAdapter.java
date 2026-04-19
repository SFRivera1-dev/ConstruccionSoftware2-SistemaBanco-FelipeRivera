package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.BankAccountEntity;
import app.application.adapters.persistence.sql.entities.CustomerCompanyEntity;
import app.application.adapters.persistence.sql.entities.CustomerPersonEntity;
import app.application.adapters.persistence.sql.repositories.BankAccountRepository;
import app.application.adapters.persistence.sql.repositories.CustomerCompanyRepository;
import app.application.adapters.persistence.sql.repositories.CustomerPersonRepository;
import app.domain.models.BankAccount;
import app.domain.models.CustomerCompany;
import app.domain.models.CustomerPerson;
import app.domain.ports.AccountPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountPersistenceAdapter implements AccountPort {

    private final BankAccountRepository bankAccountRepository;
    private final CustomerPersonRepository customerPersonRepository;
    private final CustomerCompanyRepository customerCompanyRepository;

    @Autowired
    public AccountPersistenceAdapter(BankAccountRepository bankAccountRepository,
                                     CustomerPersonRepository customerPersonRepository,
                                     CustomerCompanyRepository customerCompanyRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerPersonRepository = customerPersonRepository;
        this.customerCompanyRepository = customerCompanyRepository;
    }

    @Override
    public void save(BankAccount account) {
        bankAccountRepository.save(toEntity(account));
    }

    @Override
    public BankAccount findByAccountNumber(String accountNumber) {
        BankAccountEntity entity = bankAccountRepository.findByAccountNumber(accountNumber);
        return entity != null ? toModel(entity) : null;
    }

    @Override
    public List<BankAccount> findByCustomerDocument(Long document) {
        List<BankAccount> result = new ArrayList<>();

        CustomerPersonEntity personEntity = customerPersonRepository.findByDocument(document);
        if (personEntity != null) {
            bankAccountRepository.findByCustomerPerson(personEntity)
                    .forEach(e -> result.add(toModel(e)));
            return result;
        }

        CustomerCompanyEntity companyEntity = customerCompanyRepository.findByDocument(document);
        if (companyEntity != null) {
            bankAccountRepository.findByCustomerCompany(companyEntity)
                    .forEach(e -> result.add(toModel(e)));
        }

        return result;
    }

    private BankAccountEntity toEntity(BankAccount account) {
        BankAccountEntity e = new BankAccountEntity();
        e.setAccountNumber(account.getAccountNumber());
        e.setAccountType(account.getAccountType());
        e.setCurrentBalance(account.getCurrentBalance());
        e.setCurrency(account.getCurrency());
        e.setAccountStatement(account.getAccountStatement());
        e.setOpeningDate(account.getOpeningDate());
        if (account.getAccountHolderID() instanceof CustomerPerson) {
            e.setCustomerPerson(customerPersonRepository
                    .findByDocument(account.getAccountHolderID().getDocument()));
        } else if (account.getAccountHolderID() instanceof CustomerCompany) {
            e.setCustomerCompany(customerCompanyRepository
                    .findByDocument(account.getAccountHolderID().getDocument()));
        }
        return e;
    }

    private BankAccount toModel(BankAccountEntity e) {
        BankAccount account = new BankAccount();
        account.setAccountNumber(e.getAccountNumber());
        account.setAccountType(e.getAccountType());
        account.setCurrentBalance(e.getCurrentBalance());
        account.setCurrency(e.getCurrency());
        account.setAccountStatement(e.getAccountStatement());
        account.setOpeningDate(e.getOpeningDate());
        if (e.getCustomerPerson() != null) {
            CustomerPerson person = new CustomerPerson();
            person.setDocument(e.getCustomerPerson().getDocument());
            person.setName(e.getCustomerPerson().getName());
            account.setAccountHolderID(person);
        } else if (e.getCustomerCompany() != null) {
            CustomerCompany company = new CustomerCompany();
            company.setDocument(e.getCustomerCompany().getDocument());
            company.setName(e.getCustomerCompany().getName());
            account.setAccountHolderID(company);
        }
        return account;
    }
}