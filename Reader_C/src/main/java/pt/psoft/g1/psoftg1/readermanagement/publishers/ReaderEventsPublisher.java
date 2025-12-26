package pt.psoft.g1.psoftg1.readermanagement.publishers;

public interface ReaderEventsPublisher {
    /* Comunicacao entre Reader e AuthNUsers */
    void publishReaderTempCreatedEvent(String payload);
    void publishReaderPersistedEvent(String authorId);

    /* Comunicacao entre Reader Command e Reader Query */
    void publishReaderCreatedEvent(String payload);
    void publishReaderUpdatedEvent(String payload);
    void publishReaderDeletedEvent(String readerId);
}