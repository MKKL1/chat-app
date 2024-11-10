import {inject, Injectable} from '@angular/core';
import {Subject, tap} from "rxjs";
import {MessageStore} from "../../features/store/message/message.store";
import {EventHandler} from "./event.handler";
import {Message} from "../../features/models/message";
import {RsocketConnection} from "./rsocket.connection";
import {TextChannelQuery} from "../../features/store/textChannel/text.channel.query";
import {UserService} from "../services/user.service";
import {Channel} from "../../features/models/channel";
import {MessageNotification} from "../../features/models/message-notification";
import {formatDate} from "../../shared/utils/utils";
import {MemberQuery} from "../../features/store/member/member.query";
import {ChannelService} from "../../features/services/channel.service";
import {showNotification} from "../../shared/utils/notifications";
import {RoleStore} from "../../features/store/role/role.store";
import {Role} from "../../features/models/role";
import {MemberStore} from "../../features/store/member/member.store";
import {CommunityQuery} from "../../features/store/community/community.query";
import {VoiceChannelStore} from "../../features/store/voiceChannel/voice.channel.store";

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private rsocketConnection: RsocketConnection;
  private eventHandler: EventHandler;

  private communityQuery = inject(CommunityQuery);
  private textChannelQuery = inject(TextChannelQuery);
  private channelService = inject(ChannelService);
  private voiceChannelStore = inject(VoiceChannelStore);
  private userService = inject(UserService);
  private memberQuery = inject(MemberQuery);
  private memberStore = inject(MemberStore);
  private roleStore = inject(RoleStore);

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

  // after changing community from first there is no more data coming to text-chat component
  // stream is closed and it is never used again
  public handleNewStreamRequest(communityId: string) {
    // Create a new subscription, but don't use Event as a generic type
    this.rsocketConnection
      .requestStream<any>(`/community/${communityId}/messages`)
      .pipe(tap(() =>
        console.log(`Subscriping to /community/${communityId}/messages`)
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
          username: this.memberQuery.getEntity(
            this.communityQuery.getActiveId()
            + message.userId)?.user.username!
        });

        showNotification(
          `New message on channel ${this.textChannelQuery.getEntity(message.channelId)?.name}`,
          message.text
        );
      }
    });

    handler.add('MESSAGE_EDIT_EVENT', (message: Message) => {
      // TODO implement
    });

    handler.add('MESSAGE_DELETE_EVENT', (message: Message) => {
      // TODO implement
    });

    handler.add('CHANNEL_CREATE_EVENT', (channel: Channel) => {
      this.channelService.addChannel(channel);
    });

    handler.add('CHANNEL_EDIT_EVENT', (channel: Channel) => {
      // TODO implement
    });

    handler.add('CHANNEL_DELETE_EVENT', (id: any) => {
      this.channelService.removeChannel(id);
    });

    handler.add('ROLE_CREATE_EVENT', (res: any) => {
      res.role.communityId = res.role.community;
      this.roleStore.add(res.role);
    })

    // {
    //   "role": {
    //   "id": "64234317622018048",
    //     "name": "rolename",
    //     "permissionOverwrites": "26",
    //     "community": "63919088711237632"
    // },
    //   "members": [
    //   "63919009480835072"
    // ]
    // }

    // there is no info if members where deleted only if they were added
    // maybe remove all members from community and cached them again?

    // TODO implement
    handler.add('ROLE_UPDATE_EVENT', (res: any) => {
      console.log(res);
      const roleId = res.role.id;
      this.roleStore.update(roleId, res.role);

      // TODO somehow update state of members

    });

    // TODO implement
    handler.add('ROLE_DELETE_EVENT', (role: any) => {
      console.log(role);
      this.roleStore.remove(role.roleId);
    })

    handler.add('PARTICIPANT_CREATE_EVENT', (res: {channelId: string, userId: string}) => {
      this.voiceChannelStore.update(res.channelId, (channel) => {
        return {
          ...channel,
          participants: [...channel.participants!, res.userId],
        };
      });
    });

    handler.add('PARTICIPANT_DELETE_EVENT', (res: {channelId: string, userId: string}) => {
      this.voiceChannelStore.update(res.channelId, (channel) => {
        return {
          ...channel,
          participants: channel.participants!.filter(id => id !== res.userId),
        };
      });
    });

    return handler;
  }
}
