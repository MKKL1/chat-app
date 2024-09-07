import {Injectable, OnInit} from '@angular/core';

// if there are troubles with finding those modules use commands:
// npm install -D @types/rsocket-core
// npm install -D @types/rsocket-websocket-client

import {encodeCompositeMetadata, encodeRoute, RSocketClient} from 'rsocket-core';
import RSocketWebsocketClient from 'rsocket-websocket-client';

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
  // constant storing websocket url
  private readonly websocketUrl: string = 'ws://localhost:7000/rsocket';

  private client: RSocketClient<any, any> | undefined;

  constructor() { }

  ngOnInit() {
  }

  // this method is used to call service for first time in MainComponent.ts
  public init(){
    console.log("Initializing RSocket client");
    const client = this.initRSocketClient();
    this.client = client;
    this.connect();
  }

  // method which return configuration for RSocketClient
  // (or something similar, they won't give it type
  // because it was written for vanilla js)

  // later we can add json serializer here
  private initRSocketClient() {
    return new RSocketClient({
      transport: new RSocketWebsocketClient({
        url: this.websocketUrl,
        // only needed with tauri, browser handles websockets by itself
        wsCreator: (url: any) => {
          return new WebSocket(url);
        }
      }),
      setup: {
        keepAlive: 1000000,
        lifetime: 100000,
        dataMimeType: 'text/plain',
        metadataMimeType: 'message/x.rsocket.routing.v0'
      }
    });
  }

  // I write any as type everywhere because for now it's simpler than searching for types in this library
  private connect(){
    if(!this.client){
      return;
    }

    this.client.connect().subscribe({
      onComplete: (socket: any) => {
        // actual code executed after setting up connection starts here
        console.log("Connected with Spring");

        // TODO abstract this to function which can be then used in other parts of app
        // for now it's just to showcase, app run and sends something to backend
        socket.requestStream({
          // data send to spring
          data: 'Hello RSocket',
          metadata: String.fromCharCode('events.stream'.length) + 'events.stream'
        }).subscribe({
          onComplete: () => {
            console.log("Message send")
          },
          onError: (error: any) => {
            console.error("RSocket error occured: ", error)
          },
          onNext: (payload: any) => {
            console.log(payload);
          },
          // I don't know why but without it, rsocket won't work
          onSubscribe: (subscription: any) => {
            subscription.request(1000000);
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
