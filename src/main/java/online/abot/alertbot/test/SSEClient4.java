package online.abot.alertbot.test;

import org.glassfish.jersey.media.sse.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class SSEClient4
{
    public static void main(String[] args) throws InterruptedException {
        Client client = ClientBuilder.newBuilder()
                .register(SseFeature.class).build();
        WebTarget target = client.target("https://horizon.stellar.org/operations?cursor=now");
        EventSource eventSource = EventSource.target(target).build();
        EventListener listener = new EventListener() {
            @Override
            public void onEvent(InboundEvent inboundEvent) {
                System.out.println(inboundEvent.getName() + "; " + inboundEvent.readData(String.class));
            }
        };
        eventSource.register(listener, "message-to-client");
        eventSource.open();
/*...
        eventSource.close();*/
    }
}
