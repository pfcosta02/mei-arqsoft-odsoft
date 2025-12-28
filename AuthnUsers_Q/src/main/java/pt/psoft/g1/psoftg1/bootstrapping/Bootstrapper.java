package pt.psoft.g1.psoftg1.bootstrapping;

import pt.psoft.g1.psoftg1.usermanagement.api.AuthNUsersRabbitmqRpcController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Profile("bootstrap")
public class Bootstrapper implements CommandLineRunner {

    @Autowired
    private AuthNUsersRabbitmqRpcController rpc;

    @Override
    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void run(String... args) 
    {
        rpc.bootstrapHelper();
    }
}
