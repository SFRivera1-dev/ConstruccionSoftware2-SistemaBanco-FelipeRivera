package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.CreditEntity;
import app.application.adapters.persistence.sql.entities.CustomerCompanyEntity;
import app.application.adapters.persistence.sql.entities.CustomerPersonEntity;
import app.application.adapters.persistence.sql.repositories.CreditRepository;
import app.application.adapters.persistence.sql.repositories.CustomerCompanyRepository;
import app.application.adapters.persistence.sql.repositories.CustomerPersonRepository;
import app.domain.models.Credit;
import app.domain.models.CreditStatus;
import app.domain.models.CustomerCompany;
import app.domain.models.CustomerPerson;
import app.domain.ports.CreditPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreditPersistenceAdapter implements CreditPort {

    private final CreditRepository creditRepository;
    private final CustomerPersonRepository customerPersonRepository;
    private final CustomerCompanyRepository customerCompanyRepository;

    @Autowired
    public CreditPersistenceAdapter(CreditRepository creditRepository,
                                    CustomerPersonRepository customerPersonRepository,
                                    CustomerCompanyRepository customerCompanyRepository) {
        this.creditRepository = creditRepository;
        this.customerPersonRepository = customerPersonRepository;
        this.customerCompanyRepository = customerCompanyRepository;
    }

    @Override
    public void save(Credit credit) {
        CreditEntity saved = creditRepository.save(toEntity(credit));
        credit.setIdCredit(saved.getId());
    }

    @Override
    public Credit findById(Long id) {
        return creditRepository.findById(id)
                .map(this::toModel)
                .orElse(null);
    }

    @Override
    public List<Credit> findByCustomerDocument(Long document) {
        List<Credit> result = new ArrayList<>();

        CustomerPersonEntity personEntity = customerPersonRepository.findByDocument(document);
        if (personEntity != null) {
            creditRepository.findByCustomerPerson(personEntity)
                    .forEach(e -> result.add(toModel(e)));
            return result;
        }

        CustomerCompanyEntity companyEntity = customerCompanyRepository.findByDocument(document);
        if (companyEntity != null) {
            creditRepository.findByCustomerCompany(companyEntity)
                    .forEach(e -> result.add(toModel(e)));
        }

        return result;
    }

    @Override
    public void updateStatus(Long creditId, CreditStatus newStatus) {
        creditRepository.findById(creditId).ifPresent(e -> {
            e.setCreditStatus(newStatus);
            creditRepository.save(e);
        });
    }

    @Override
    public void update(Credit credit) {
    creditRepository.findById(credit.getIdCredit()).ifPresent(e -> {
        e.setAmountApproved(credit.getAmountApproved());
        e.setApprovalDate(credit.getApprovalDate());
        e.setDisbursementDate(credit.getDisbursementDate());
        e.setCreditStatus(credit.getCreditStatus());
        e.setDestinationAccount(credit.getDestinationAccount());
        creditRepository.save(e);
    });
}

    private CreditEntity toEntity(Credit credit) {
        CreditEntity e = new CreditEntity();
        e.setCreditType(credit.getCreditType());
        e.setAmountRequested(credit.getAmountRequested());
        e.setAmountApproved(credit.getAmountApproved());
        e.setInterestRate(credit.getInterestRate());
        e.setTermMonths(credit.getTermMonths());
        e.setCreditStatus(credit.getCreditStatus());
        e.setApprovalDate(credit.getApprovalDate());
        e.setDisbursementDate(credit.getDisbursementDate());
        e.setDestinationAccount(credit.getDestinationAccount());
        if (credit.getCustomerRequestId() instanceof CustomerPerson) {
            e.setCustomerPerson(customerPersonRepository
                    .findByDocument(credit.getCustomerRequestId().getDocument()));
        } else if (credit.getCustomerRequestId() instanceof CustomerCompany) {
            e.setCustomerCompany(customerCompanyRepository
                    .findByDocument(credit.getCustomerRequestId().getDocument()));
        }
        return e;
    }

    private Credit toModel(CreditEntity e) {
        Credit credit = new Credit();
        credit.setIdCredit(e.getId());
        credit.setCreditType(e.getCreditType());
        credit.setAmountRequested(e.getAmountRequested());
        credit.setAmountApproved(e.getAmountApproved());
        credit.setInterestRate(e.getInterestRate());
        credit.setTermMonths(e.getTermMonths());
        credit.setCreditStatus(e.getCreditStatus());
        credit.setApprovalDate(e.getApprovalDate());
        credit.setDisbursementDate(e.getDisbursementDate());
        credit.setDestinationAccount(e.getDestinationAccount());
        if (e.getCustomerPerson() != null) {
            CustomerPerson person = new CustomerPerson();
            person.setDocument(e.getCustomerPerson().getDocument());
            person.setName(e.getCustomerPerson().getName());
            credit.setCustomerRequestId(person);
        } else if (e.getCustomerCompany() != null) {
            CustomerCompany company = new CustomerCompany();
            company.setDocument(e.getCustomerCompany().getDocument());
            company.setName(e.getCustomerCompany().getName());
            credit.setCustomerRequestId(company);
        }
        return credit;
    }
}