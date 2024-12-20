import {inject} from '@angular/core';
// if there are troubles with finding those modules use commands:
// npm install -D @types/rsocket-core
// npm install -D @types/rsocket-websocket-client
import {
  APPLICATION_JSON, BufferEncoders,
  encodeBearerAuthMetadata,
  encodeCompositeMetadata, encodeRoute,
  MESSAGE_RSOCKET_AUTHENTICATION,
  MESSAGE_RSOCKET_COMPOSITE_METADATA, MESSAGE_RSOCKET_ROUTING,
  RSocketClient,
} from 'rsocket-core';
import RSocketWebsocketClient from 'rsocket-websocket-client';
import {KeycloakService} from "keycloak-angular";
import {Observable} from "rxjs";
import {environment} from "../../../environment";
import {EventConnection} from "./EventConnection";

// this service should be injected to other services / components
// and allow them to send or subscribe to responds
export class RsocketConnection implements EventConnection{
  // constant storing websocket url
  private readonly websocketUrl: string = environment.websocketUrl;

  // Magical number representing maximal number of request client can make
  private readonly requestCount: number = 2147483647;
  private readonly client: RSocketClient<any, any> | undefined;
  private rsocket: any;

  constructor() {
    console.log("Initializing RSocket client");
    this.client = this.initRSocketClient();
  }

  // method which return configuration for RSocketClient
  private initRSocketClient() {

    // Keycloak service is not injected in constructor,
    // to make it easier creating this class as a standard one and not a service
    const keycloakService = inject(KeycloakService);
    let idToken = keycloakService.getKeycloakInstance().idToken;
    if(!idToken) {
      console.error("Not authenticated");
      return;
    }

    return new RSocketClient({
      transport: new RSocketWebsocketClient({
        url: this.websocketUrl,
      }, BufferEncoders),
      setup: {
        keepAlive: 60000,
        lifetime: 180000,
        dataMimeType: APPLICATION_JSON.string,
        metadataMimeType: MESSAGE_RSOCKET_COMPOSITE_METADATA.string,
        payload: {
          metadata: encodeCompositeMetadata([
            [MESSAGE_RSOCKET_AUTHENTICATION, encodeBearerAuthMetadata(idToken)],
          ])
        },
      }

    });
  }

  public requestStream<T>(path: string): Observable<T> {
    let decoder = new TextDecoder();

    return new Observable<any>(subscriber => {
      const subscription = this.rsocket.requestStream({
        data: Buffer.from('{}'), //Placeholder for request data
        metadata: encodeCompositeMetadata([
          //'/community/9895314911657984/messages'
          [MESSAGE_RSOCKET_ROUTING, encodeRoute(path)]
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
          // Instead of returning plane string trying to map it to generic type
          // Also it has to parse all numbers to string to not broke ids
          // I guess I broke parsing
          try{
            const parsedData: {data: T} = JSON.parse(dataAsString, (key, value) => {
              if(typeof value === 'number'){
                value = BigInt(value).toString();
              }

              return value;
            });
            subscriber.next(parsedData);
          } catch(error){
            console.error("Error parsing JSON: ", error);
            subscriber.error(error);
          }
        },

        onSubscribe: (subscription: any) => {
          //How many requests client can handle, when this count runs out, another request should be made
          subscription.request(this.requestCount);
        }
      });

      return () => subscription.unsubscribe();
    });
  }

  public connect(){
    if(!this.client){
      return;
    }

    // This function will be used only by this service to renew connection
    this.client.connect().subscribe({
      onComplete: (socket) => {
        // actual code executed after setting up connection starts here
        this.rsocket = socket;
        console.log("Connected with RSocket");
      },
      // handling errors with connection
      onError: (error: any) => console.error("Error occured: ", error),
      onSubscribe: (cancel: any) => {
        // I'm not using this for now
      }
    });
  }

  public close(){
    this.rsocket.close();
    console.log(this.rsocket);
  }
}
