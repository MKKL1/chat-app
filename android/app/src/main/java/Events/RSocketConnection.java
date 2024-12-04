package Events;

import android.util.Log;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import Config.env;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.metadata.AuthMetadataCodec;
import io.rsocket.metadata.CompositeMetadataCodec;
import io.rsocket.metadata.TaggingMetadataCodec;
import io.rsocket.metadata.WellKnownAuthType;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.ByteBufPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RSocketConnection {
    private String token;
    private static final int REQUEST_COUNT = Integer.MAX_VALUE;
    private RSocket rSocket;
    private boolean isConnected = false;

    public RSocketConnection(String token) {
        this.token = token;
        connect();
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Create RSocket connection to server publisher
     */
    public void connect() {

        if (token == null || token.isEmpty()) {
            Log.e("RSocketConnection", "Nieautoryzowany dostęp, nie podano tokenu JWT");
            return;
        }

        RSocketConnector connector = RSocketConnector.create()
                .metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString())
                .dataMimeType(WellKnownMimeType.APPLICATION_JSON.getString())
                .setupPayload(ByteBufPayload.create(new byte[0], encodeBearer(token)));


        WebsocketClientTransport transport = WebsocketClientTransport.create(URI.create(env.websocketUrl));
        Mono<RSocket> rSocketMono = connector.connect(transport);

        rSocketMono.doOnTerminate(() -> {
            isConnected = rSocket != null;
        }).subscribe(rSocket -> {
            this.rSocket = rSocket;
            isConnected = true;
            Log.d("RSocketConnection", "Połączono z serwerem RSocket");
        }, throwable -> {
            isConnected = false;
            Log.e("RSocketConnection", "Błąd połączenia z serwerem RSocket", throwable);
        });
    }

    /**
     * Observe specific queue/stream from server RSocket publisher
     * @param path - URL to specific queue/stream
     */
    public Flux<String> requestStream(String path) {
        if (rSocket == null || !isConnected) {
            return Flux.error(new IllegalStateException("RSocket connection is not established"));
        }
        ByteBuf data = Unpooled.wrappedBuffer("{}".getBytes(StandardCharsets.UTF_8));

        return rSocket.requestStream(ByteBufPayload.create(data, encodeRoute(path)))
                .map(payload -> {
                    String response = payload.getDataUtf8();
                    payload.release();
                    return response;
                })
                .doOnError(throwable -> Log.e("RSocketConnection", "Błąd strumienia", throwable))
                .doOnComplete(() -> Log.d("RSocketConnection", "Strumień zakończony"))
                .log();
    }

    /**
     * Encode Authorization Header
     * @param token - jwt token
     */
    private byte[] encodeBearer(String token) {
        ByteBuf metadata = AuthMetadataCodec.encodeMetadata(
                ByteBufAllocator.DEFAULT,
                WellKnownAuthType.BEARER,
                Unpooled.copiedBuffer(token, CharsetUtil.UTF_8)
        );
        CompositeByteBuf compositeMetadata = Unpooled.compositeBuffer();
        CompositeMetadataCodec.encodeAndAddMetadata(
                compositeMetadata,
                ByteBufAllocator.DEFAULT,
                WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION,
                metadata
        );
        byte[] result = new byte[compositeMetadata.readableBytes()];
        compositeMetadata.getBytes(compositeMetadata.readerIndex(), result);

        return result;
    }

    /**
     * Disconnect from RSocket server
     */
    public void close() {
        if (rSocket != null) {
            rSocket.dispose();
            Log.d("RSocketConnection", "Zamknięto połączenie RSocket");
        }
    }

    /**
     * Encode URL to RSocket queue/stream
     * @param path - URL to queue/stream
     */
    private ByteBuf encodeRoute(String path) {

        CompositeByteBuf compositeMetadata = ByteBufAllocator.DEFAULT.compositeBuffer();

        CompositeMetadataCodec.encodeAndAddMetadata(
                compositeMetadata,
                ByteBufAllocator.DEFAULT,
                WellKnownMimeType.MESSAGE_RSOCKET_ROUTING,
                TaggingMetadataCodec.createTaggingContent(
                        ByteBufAllocator.DEFAULT,
                        Collections.singletonList(path)
                )
        );
        return compositeMetadata;
    }
}
