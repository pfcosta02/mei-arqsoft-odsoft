package pt.psoft.g1.psoftg1.lendingmanagement.services;

import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingQueryDTO;
import pt.psoft.g1.psoftg1.shared.dtos.LendingEventAMQP;

import java.util.List;

public interface LendingService {

    // LEITURA
    List<LendingQueryDTO> getAllLendings();
    LendingQueryDTO getLendingByNumber(String lendingNumber);
    List<LendingQueryDTO> getLendingsByReader(String readerNumber);
    List<LendingQueryDTO> getActiveLendingsByReader(String readerNumber);
    List<LendingQueryDTO> getOverdueLendings();
    int countLendingsCurrentYear();
    Double getAverageLendingDuration();

    // SINCRONIZAÇÃO
    void createFromEvent(LendingEventAMQP event);
    void updateFromEvent(LendingEventAMQP event);
    void deleteFromEvent(LendingEventAMQP event);
}
