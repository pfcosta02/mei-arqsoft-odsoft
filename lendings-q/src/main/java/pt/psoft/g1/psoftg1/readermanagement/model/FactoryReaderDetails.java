package pt.psoft.g1.psoftg1.readermanagement.model;

import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.usermanagement.model.FactoryUser;

@Component
public class FactoryReaderDetails {

    FactoryUser _factoryUser;

    public FactoryReaderDetails(FactoryUser factoryUser) {
        _factoryUser = factoryUser;
    }

    public ReaderDetails newReaderDetails(String readerNumber) {
        return new ReaderDetails(readerNumber, _factoryUser);
    }
}
