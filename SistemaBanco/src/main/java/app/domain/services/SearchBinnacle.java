package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Binnacle;
import app.domain.ports.BinnaclePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchBinnacle {

    private final BinnaclePort binnaclePort;

    @Autowired
    public SearchBinnacle(BinnaclePort binnaclePort) {
        this.binnaclePort = binnaclePort;
    }

    // Analista interno: acceso completo a toda la bitácora
    public List<Binnacle> findAll() {
        return binnaclePort.findAll();
    }

    // Consulta por producto (cuenta, crédito, transferencia)
    // También lo usan clientes para ver su propio historial filtrado
    public List<Binnacle> findByProductId(String affectedProductId) throws BusinessException {
        if (affectedProductId == null || affectedProductId.isBlank()) {
            throw new BusinessException("El ID del producto afectado es obligatorio");
        }
        return binnaclePort.findByProductId(affectedProductId);
    }
}
