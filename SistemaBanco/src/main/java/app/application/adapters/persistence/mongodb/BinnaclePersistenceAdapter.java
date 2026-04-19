package app.application.adapters.persistence.mongodb;

import app.application.adapters.persistence.mongodb.documents.BinnacleDocument;
import app.application.adapters.persistence.mongodb.repositories.BinnacleRepository;
import app.domain.models.Binnacle;
import app.domain.ports.BinnaclePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BinnaclePersistenceAdapter implements BinnaclePort {

    private final BinnacleRepository binnacleRepository;

    @Autowired
    public BinnaclePersistenceAdapter(BinnacleRepository binnacleRepository) {
        this.binnacleRepository = binnacleRepository;
    }

    @Override
    public void save(Binnacle binnacle) {
        binnacleRepository.save(toDocument(binnacle));
    }

    @Override
    public List<Binnacle> findByProductId(String affectedProductId) {
        return binnacleRepository.findByAffectedProductId(affectedProductId)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public List<Binnacle> findAll() {
        return binnacleRepository.findAll()
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    private BinnacleDocument toDocument(Binnacle binnacle) {
        BinnacleDocument doc = new BinnacleDocument();
        doc.setOperationType(binnacle.getOperationType());
        doc.setDatetimeOperation(binnacle.getDatetimeOperation());
        doc.setIdUser(binnacle.getIdUser());
        doc.setRoleUser(binnacle.getRoleUser());
        doc.setAffectedProductId(binnacle.getAffectedProductId());
        doc.setDetails(binnacle.getDetails());
        return doc;
    }

    private Binnacle toModel(BinnacleDocument doc) {
        Binnacle binnacle = new Binnacle();
        binnacle.setIdBinnacle(doc.getId());
        binnacle.setOperationType(doc.getOperationType());
        binnacle.setDatetimeOperation(doc.getDatetimeOperation());
        binnacle.setIdUser(doc.getIdUser());
        binnacle.setRoleUser(doc.getRoleUser());
        binnacle.setAffectedProductId(doc.getAffectedProductId());
        binnacle.setDetails(doc.getDetails());
        return binnacle;
    }
}