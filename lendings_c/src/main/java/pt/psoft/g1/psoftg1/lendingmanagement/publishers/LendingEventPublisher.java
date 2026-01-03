package pt.psoft.g1.psoftg1.lendingmanagement.publishers;

import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;

public interface LendingEventPublisher {
    void publishLendingCreated(Lending lending);
    void publishLendingUpdated(Lending lending);
    void publishLendingDeleted(Lending lending);
}
