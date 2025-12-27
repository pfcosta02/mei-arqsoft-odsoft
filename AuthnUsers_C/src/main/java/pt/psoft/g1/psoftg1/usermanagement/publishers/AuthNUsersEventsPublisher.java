package pt.psoft.g1.psoftg1.usermanagement.publishers;

public interface AuthNUsersEventsPublisher {
    void publishUserTempCreatedEvent(String payload);

    void publishUserCreatedEvent(String payload);
    void publishUserUpdatedEvent(String payload);
    void publishUserDeletedEvent(String payload);
}
