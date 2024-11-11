import {Injectable} from '@angular/core';
import {Message} from "../models/message";
import {ChannelMessagesState} from "../models/channel";
import {MessageStore} from "../store/message/message.store";
import {CreateMessageDto} from "../models/create.message.dto";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../../environment";
import {TextChannelQuery} from "../store/textChannel/text.channel.query";
import {ID} from "@datorama/akita";
import {Observable, Subject} from "rxjs";
import {TextChannelStore} from "../store/textChannel/text.channel.store";
import {EventService} from "../../core/events/event.service";
import {formatDate} from "../../shared/utils/utils";
import {showNotification} from "../../shared/utils/notifications";
import {MessageNotification} from "../models/message-notification";
import {UserService} from "../../core/services/user.service";
import {MemberQuery} from "../store/member/member.query";
import {CommunityQuery} from "../store/community/community.query";
@Injectable({
  providedIn: 'root'
})
export class MessageService{
  api: string = environment.api + "channels/";

  limit: number = 10;

  // this subject is used to notify list of text channels about new message,
  // so it can add notification to proper channel from list
  private notificationSubject = new Subject<MessageNotification>();
  notification$ = this.notificationSubject.asObservable();

  constructor(private http: HttpClient,
              private channelQuery: TextChannelQuery,
              private messageStore: MessageStore,
              private channelStore: TextChannelStore,
              private eventService: EventService,
              private userService: UserService,
              private memberQuery: MemberQuery,
              private communityQuery: CommunityQuery) {
    this.eventService.on('MESSAGE_CREATE_EVENT', this.handleCreateMessage);
    this.eventService.on('MESSAGE_UPDATE_EVENT', this.handleUpdateMessage);
    this.eventService.on('MESSAGE_DELETE_EVENT', this.handleDeleteMessage);
  }

  getMessages(channelId: ID, state: ChannelMessagesState, lastMessageId?: string){
    channelId = channelId.toString();

    switch(state){
      case ChannelMessagesState.NotFetched:
        this.fetchMessages(channelId).subscribe(messages => {
          this.messageStore.add(messages);

          if(messages.length < this.limit){
            this.channelStore.update(channelId, {messagesState: ChannelMessagesState.FullyFetched});
          } else {
            this.channelStore.update(channelId, {messagesState: ChannelMessagesState.PartlyFetched});
          }
        });

        break;
      case ChannelMessagesState.PartlyFetched:
        this.fetchMessages(channelId, lastMessageId).subscribe(messages => {
          this.messageStore.add(messages);

          if(messages.length < this.limit){
            this.channelStore.update(channelId, {messagesState: ChannelMessagesState.FullyFetched});
          }
        });

        break;
      case ChannelMessagesState.FullyFetched:
        // because there is no action messages won't update
        return;
    }
  }

  // for now after switching channel all message data is lost
  fetchMessages(channelId: ID, lastMessageId?: string): Observable<Message[]>{
    const params: any = {
      limit: this.limit
    }

    if(lastMessageId){
      params.before = lastMessageId;
    }

    return this.http.get<Message[]>(this.api + `${channelId}/messages`, {
      params
    });
  }

  // we only want to send communityId, channelId and message text
  sendMessage(message: CreateMessageDto, file?: File | null){
    const formData = new FormData();
    message.communityId = this.channelQuery.getActive()?.communityId;
    formData.append('message', new Blob([JSON.stringify(message)], { type: 'application/json' }));

    if(file){
      formData.append('file', file, file.name);
    }

    const channelId = this.channelQuery.getActiveId();

    // Message don't have to be added to store, because rsocket is already listening for new messages
    this.http.post(this.api + `${channelId}/messages`, formData, {
      headers: new HttpHeaders({
        'enctype': 'multipart/form-data'
      })
    }).subscribe(res => {
      //console.log(res);
    });
  }

  editMessage(id: string, text: string){
    const channelId = this.channelQuery.getActiveId();

    this.http.patch<Message>(this.api + `${channelId}/messages/${id}`, {
      text: text
    }).subscribe(message => {
      this.messageStore.update(message.id, message);
    });
  }

  deleteMessage(id: string){
    const channelId = this.channelQuery.getActiveId();
    this.http.delete(this.api + `${channelId}/messages/${id}`).subscribe(res => {
      this.messageStore.remove(id);
    });
  }

  // todo implement
  addReaction(reaction: string, messageId: string, userId: string){
    console.log(reaction);
  }

  // todo implement
  deleteReaction(){

  }

  private handleCreateMessage = (message: Message) => {
    this.messageStore.add(message);
    // check if messages is from other user and if it is from other channel
    // if yes send system notification
    if (this.userService.getUser().id !== message.userId && this.channelQuery.getActiveId() !== message.channelId) {
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
        `New message on channel ${this.channelQuery.getEntity(message.channelId)?.name}`,
        message.text
      );
    }
  }

  private handleUpdateMessage = (message: Message) => {
    console.log(message);
  };

  private handleDeleteMessage = (message: Message) => {
    console.log(message);
  };

}
