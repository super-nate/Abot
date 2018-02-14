package online.abot.alertbot.test;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
public class SSEClient3 {
    public static void main(String[] args) {


        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("https://horizon.stellar.org/accounts/GAYVP62K6DFSNLOM27JO5VNJVCC5E3NZ6DFGONW3DAATNVHDHLI2BZQP/payments?cursor=now");
        SseEventSource sseEventSource = SseEventSource.target(target).build();
        sseEventSource.register((event) -> System.out.println(event.getName() + "; "
                + event.readData(String.class)));
        sseEventSource.open();

// do other stuff, block here and continue when done
        /*while (true){

        }*/

       // sseEventSource.close();
    }
}
