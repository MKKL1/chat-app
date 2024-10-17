import {Injectable} from '@angular/core';
import {Subscription} from "rxjs";
import {MessageStore} from "../../features/store/message/message.store";
import {EventHandler} from "./event.handler";
import {Message} from "../../features/models/message";
import {RsocketConnection} from "./rsocket.connection";
import {environment} from "../../../environment";

@Injectable({
  providedIn: 'root'
})
// don't work properly
export class EventService {

  private rsocketConnection: RsocketConnection;
  private eventHandler: EventHandler;
  private currentStreamSubscription: Subscription | null = null;

  constructor(private messageStore: MessageStore) {
    this.rsocketConnection = new RsocketConnection();
    this.eventHandler = this.initEventHandler();
  }

  public init(){
    this.rsocketConnection.connect();
  }

  // broken as hell
  // after changing community from first there is no more data coming to text-chat component
  // subscription is destroyed and it is never subscribed again
  public handleNewStreamRequest(communityId: string) {
    console.log(this.currentStreamSubscription);

    // Unsubscribing current stream before creating a new one
    this.closeCurrentStream();

    console.log(this.currentStreamSubscription);

    console.log(`Subscribing to: /community/${communityId}/messages`);

    // Create a new subscription, but don't use Event as a generic type
    this.currentStreamSubscription = this.rsocketConnection
      .requestStream<any>(`/community/${communityId}/messages`)
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

    console.log(this.currentStreamSubscription);
  }

  // TODO make it work
  private closeCurrentStream() {
    if (this.currentStreamSubscription !== undefined && this.currentStreamSubscription !== null && !this.currentStreamSubscription.closed) {
      try {
        this.currentStreamSubscription.unsubscribe();
        this.currentStreamSubscription = null;
        console.log('Previous stream unsubscribed successfully.');
      } catch (error) {
        console.error("Error during unsubscription:", error);
      }
    } else {
      console.log('Stream already unsubscribed or never initialized');
    }
  }

  // First idea was to create EventHandler object in separated file and
  // add callbacks there, but with this approach passed functions
  // would not be able to use classes delivered by DI, which makes them almost useless
  private initEventHandler(){
    const handler = new EventHandler();

    handler.add('MESSAGE_CREATE_EVENT', (data: Message) => {
      console.log(data);
      // modifying path so it can be sent to api
      data.attachments = data.attachments.map(attachment => {
        attachment.path = environment.api + "/" + attachment.path;
        return attachment;
      });
      this.messageStore.add(data);
    });

    return handler;
  }
}
