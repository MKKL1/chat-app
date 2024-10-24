import {inject, Injectable} from '@angular/core';
import {Subject, Subscription} from "rxjs";
import {MessageStore} from "../../features/store/message/message.store";
import {EventHandler} from "./event.handler";
import {Message} from "../../features/models/message";
import {RsocketConnection} from "./rsocket.connection";
import {TextChannelQuery} from "../../features/store/textChannel/text.channel.query";
import {UserService} from "../services/user.service";
import {Channel, ChannelType} from "../../features/models/channel";
import {MessageNotification} from "../../features/models/message-notification";
import {formatDate} from "../../shared/utils/utils";
import {MemberQuery} from "../../features/store/member/member.query";
import {ChannelService} from "../../features/services/channel.service";
import {showNotification} from "../../shared/utils/notifications";
import {TextChannelStore} from "../../features/store/textChannel/text.channel.store";

@Injectable({
  providedIn: 'root'
})
// don't work properly
export class EventService {

  private rsocketConnection: RsocketConnection;
  private eventHandler: EventHandler;
  private currentStreamSubscription: Subscription | null = null;

  private textChannelQuery = inject(TextChannelQuery);
  private channelService = inject(ChannelService);
  private userService = inject(UserService);
  private memberQuery = inject(MemberQuery);

  // this subject is used to notify list of text channels about new message,
  // so it can add notification to proper channel from list
  private notificationSubject = new Subject<MessageNotification>();

  notification$ = this.notificationSubject.asObservable();

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

    handler.add('MESSAGE_CREATE_EVENT', (message: Message) => {
      this.messageStore.add(message);

      // check if messages is from other user and if it is from other channel
      // if yes send system notification
      if(this.userService.getUser().id !== message.userId && this.textChannelQuery.getActiveId() !== message.channelId){
        // send notification to channel list
        this.notificationSubject.next({
          channelId: message.channelId,
          date: formatDate(message.updatedAt),
          message: message.text,
          username: this.memberQuery.getEntity(message.userId)?.user.username!
        });

        showNotification(
          `New message on channel ${this.textChannelQuery.getEntity(message.channelId)?.name}`,
          message.text
        );
      }
    });

    handler.add('MESSAGE_EDIT_EVENT', (message: Message) => {

    });

    handler.add('MESSAGE_DELETE_EVENT', (message: Message) => {

    });

    handler.add('CHANNEL_CREATE_EVENT', (channel: Channel) => {
      this.channelService.addChannel(channel);
    });

    handler.add('CHANNEL_EDIT_EVENT', (channel: Channel) => {

    });

    handler.add('CHANNEL_DELETE_EVENT', (id: any) => {
      this.channelService.removeChannel(id);
    });

    return handler;
  }
}
