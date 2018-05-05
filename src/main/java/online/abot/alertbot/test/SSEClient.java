/*
package online.abot.alertbot.test;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SSEClient {
    public static void main(final String[] args) {
        final WebClient client = WebClient.create();
        client.get()
                .uri("https://horizon.stellar.org/transactions?limit=10&order=desc")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .flatMapMany(response -> response.body(BodyExtractors.toFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
                })))
                .filter(sse -> Objects.nonNull(sse.data()))
                .map(ServerSentEvent::data)
                .subscribe(System.out::println);
               */
/* .buffer(10)
                .doOnNext(System.out::println)
                .blockFirst();*//*

    }

*/
/*    public static void main(String[] args) throws InterruptedException {
        *//*
*/
/*final Flux<ServerSentEvent> stream = WebClient
                .create("http://emojitrack-gostreamer.herokuapp.com")
                .get().uri("/subscribe/eps")
                .retrieve()
                .bodyToFlux(ServerSentEvent.class);*//*
*/
/*

        final Flux<String> stream = WebClient
                .create("https://horizon.stellar.org")
                .get().uri("/accounts/GAYVP62K6DFSNLOM27JO5VNJVCC5E3NZ6DFGONW3DAATNVHDHLI2BZQP/payments?cursor=now")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class);

        stream.subscribe(sse -> System.out.println("Received: "+sse));

        //TimeUnit.MINUTES.sleep(10);
    }*//*

}*/
