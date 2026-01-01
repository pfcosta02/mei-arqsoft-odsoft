package pt.psoft.g1.psoftg1.lendingmanagement.publishers;

import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;

public interface LendingEventPublisher {
    void sendLendingCreated(Lending lending);
    void sendLendingUpdated(Lending lending, Long currentVersion);
    void sendLendingDeleted(Lending lending, Long currentVersion);
}
