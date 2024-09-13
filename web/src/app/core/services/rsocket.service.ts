import {Injectable, OnInit} from '@angular/core';

// if there are troubles with finding those modules use commands:
// npm install -D @types/rsocket-core
// npm install -D @types/rsocket-websocket-client
import {
  APPLICATION_JSON, BufferEncoders,
  encodeBearerAuthMetadata,
  encodeCompositeMetadata, encodeRoute, JsonSerializer,
  MESSAGE_RSOCKET_AUTHENTICATION,
  MESSAGE_RSOCKET_COMPOSITE_METADATA, MESSAGE_RSOCKET_ROUTING,
  RSocketClient, TEXT_PLAIN
} from 'rsocket-core';
import RSocketWebsocketClient from 'rsocket-websocket-client';
import {KeycloakService} from "keycloak-angular";

// this service is injected in MainComponent.ts
// which will be created after user sign in/up
// then ngOnInit method will be run

// this service should be injected to other services / components
// and allow them to send or subscribe to responds

@Injectable({
  providedIn: 'root'
})
export class RsocketService implements OnInit{

  // TODO get localhost:7000 from environment file instead of hard coding it
  // constant storing websocket ur
  private readonly websocketUrl: string = 'ws://localhost:8083/events';

  //Magical number representing maximal number of request client can make
  // Probably should be lower to make use of backpressure
  private readonly requestCount: number = 2147483647;

  private client: RSocketClient<any, any> | undefined;

  constructor(private keycloakService: KeycloakService) { }

  ngOnInit() {
  }

  // this method is used to call service for first time in MainComponent.ts
  public init(){
    console.log("Initializing RSocket client");
    this.client = this.initRSocketClient();
    this.connect();
  }

  // method which return configuration for RSocketClient
  // (or something similar, they won't give it type
  // because it was written for vanilla js)

  // later we can add json serializer here
  private initRSocketClient() {
    //TODO idToken doesn't refresh/expires when page is not reloaded for long time?
    let idToken = this.keycloakService.getKeycloakInstance().idToken;
    if(!idToken) {
      console.error("Not authenticated");
      return;
    }

    return new RSocketClient({
      transport: new RSocketWebsocketClient({
        url: this.websocketUrl,
      }, BufferEncoders),
      //For unknown reason, applying serializers causes client to stop receiving messages (Nothing is showing in console)
      // serializers: {
      //   data: JsonSerializer,
      //   metadata: JsonSerializer,
      // },
      setup: {
        keepAlive: 60000,
        lifetime: 180000,
        dataMimeType: APPLICATION_JSON.string,
        metadataMimeType: MESSAGE_RSOCKET_COMPOSITE_METADATA.string,
        payload: {
          metadata: encodeCompositeMetadata([
            // [TEXT_PLAIN, Buffer.from('Hello World')],
            [MESSAGE_RSOCKET_AUTHENTICATION, encodeBearerAuthMetadata(idToken)],
          ])
        },
      }

    });
  }

  public requestStream(path: string) { // out event stream or somethign
  }

  // I write any as type everywhere because for now it's simpler than searching for types in this library
  private connect(){
    if(!this.client){
      return;
    }


    let decoder = new TextDecoder();


    this.client.connect().subscribe({
      onComplete: (socket: any) => {
        // actual code executed after setting up connection starts here
        console.log("Connected with Spring");

        // This function will be used only by this service to renew connection, instead other parts of app should implement event listeners
        // TODO handle events based on provided id
        socket.requestStream({
          data: Buffer.from('{}'), //Placeholder for request data, if ever needed / May be removed
          metadata: encodeCompositeMetadata([
            [MESSAGE_RSOCKET_ROUTING, encodeRoute('/community/9895314911657984/messages')]
          ])
        }).subscribe({
          onComplete: () => {
            console.log("Message send")
          },
          onError: (error: any) => {
            console.error("RSocket error occured: ", error)
          },
          onNext: (payload: any) => {
            const dataAsString = decoder.decode(payload.data);
            //dataAsString is json string
            console.log(dataAsString);
          },

          onSubscribe: (subscription: any) => {
            //How many requests client can handle, when this count runs out, another request should be made
            subscription.request(this.requestCount);
          }
        });

      },
      // handling errors with connection
      onError: (error: any) => console.error("Error occured: ", error),
      onSubscribe: (cancel: any) => {
        // I'm not using this for now
      }
    });
  }
}
