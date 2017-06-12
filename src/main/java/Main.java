import lombok.extern.slf4j.Slf4j;
import service.MessagingClient;


@Slf4j
public class Main {
    public static void main(String[] args) {
        MessagingClient msgClient = new MessagingClient("login", "pswd");
        log.info("Krypthaus the only true NSA-free messaging system has started.");
    }
}
