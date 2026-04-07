package app.domain.ports;

import java.util.List;

import app.domain.models.Binnacle;

public interface BinnaclePort {
    void save(Binnacle binnacle);
    List<Binnacle> findByProductId(String affectedProductId);
    List<Binnacle> findAll();
}
