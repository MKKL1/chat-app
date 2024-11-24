import {Injectable} from '@angular/core';
import {tap} from "rxjs";
import {EventCallback, EventHandler} from "./event.handler";
import {RsocketConnection} from "./rsocket.connection";

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private rsocketConnection: RsocketConnection;
  private eventHandler: EventHandler;

  constructor() {
    this.rsocketConnection = new RsocketConnection();
    this.eventHandler = new EventHandler();
  }

  public init(){
    this.rsocketConnection.connect();
  }

  // after changing community from first there is no more data coming to text-chat component
  // stream is closed and it is never used again
  public handleNewStreamRequest(communityId: string) {
    // Create a new subscription, but don't use Event as a generic type
    this.rsocketConnection
      .requestStream<any>(`/community/${communityId}/messages`)
      .pipe(tap(() =>
        console.log(`Message from /community/${communityId}/messages`)
      ))
      .subscribe({
        next: (event: any) => {
          console.log(`Event received: ${event.name}`);
          console.log('Event data:', event.data);
          // eventHandler search for callback function with eventName
          // corresponding to one in event and invoke it
          this.eventHandler.handleEvent(event.name, event.data);
        },
        error: (err: any) => console.error('Stream error:', err),
        complete: () => console.log('Stream completed'),
      });
  }

  public on(eventName: string, handler: EventCallback){
    this.eventHandler.add(eventName, handler);
  }

}
