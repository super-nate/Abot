package online.abot.alertbot.test;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
public class SSEClient3 {
    public static void main(String[] args) throws InterruptedException {


      Client client = ClientBuilder.newBuilder().build();
        //WebTarget target = client.target("https://horizon.stellar.org/accounts/GAYVP62K6DFSNLOM27JO5VNJVCC5E3NZ6DFGONW3DAATNVHDHLI2BZQP/payments?cursor=now");
        WebTarget target = client.target("https://horizon.stellar.org/operations?cursor=now");
          SseEventSource sseEventSource = SseEventSource.target(target).build();
       /* sseEventSource.register((event) -> System.out.println("retry: " + event.getReconnectDelay() + ";target: " + event.getName() + "; "
                + event.readData(String.class)));*/
        sseEventSource.register(
                inBoundSseEvent ->System.out.println(Thread.currentThread() + ": " + inBoundSseEvent),
                Throwable::printStackTrace,
                () -> System.out.println("There will be no further events."));
        sseEventSource.open();
        /*while (true){
            Thread.sleep(100);
            target.request().get();
        }*/

        // EventSource#register(Consumer<InboundSseEvent>, Consumer<Throwable>, Runnable)
        // consumes all events and all exceptions, writing both on standard out.
        // registers onComplete callback, which will print out a message "There will be no further events."
/*        try (final SseEventSource eventSource = SseEventSource.target(target).build()) {

            eventSource.register(
                    System.out::println,
                    Throwable::printStackTrace,
                    () -> System.out.println("There will be no further events."));
            eventSource.open();

            for (int counter = 0; counter < 5; counter++) {
                target.request().post(Entity.text("message " + counter));
            }

            Thread.sleep(500); // make sure all the events have time to arrive
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

       /* WebTarget target2 = client.target("https://horizon.stellar.org/accounts/GC55XRU2OEJ5QOGTACWBQDJBVGCPGEDGOMUAPSM2BZW2DQJQTOMZTMMC/payments?cursor=now");
        SseEventSource sseEventSource2 = SseEventSource.target(target2).build();
        sseEventSource2.register((event) -> System.out.println(event.getName() + "; "
                + event.readData(String.class)));
        sseEventSource2.open();*/
// do other stuff, block here and continue when done


       // sseEventSource.close();
    }
}
