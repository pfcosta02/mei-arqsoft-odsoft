package pt.psoft.g1.psoftg1.lendingmanagement.services;

import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingCommandDTO;
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingViewAMQP;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.shared.services.Page;

import java.util.List;
import java.util.Optional;

public interface LendingService {
    /**
     * Cria um novo lending
     */
    Lending createLending(LendingCommandDTO dto);

    /**
     * Marca um lending como devolvido
     */
    Lending returnLending(String lendingNumber, String commentary);

    /**
     * Deleta um lending
     */
    void deleteLending(String lendingNumber);
}
