import {Injectable, OnInit} from '@angular/core';
import {Message} from "../models/message";
import {MessageStore} from "../store/message/message.store";
import {CreateMessageDto} from "../models/create.message.dto";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environment";
import {TextChannelQuery} from "../store/textChannel/text.channel.query";

@Injectable({
  providedIn: 'root'
})
export class MessageService{
  api: string = '';
  channelId: string = '';
  communityId: string = '';

  constructor(
    private http: HttpClient,
    private channelQuery: TextChannelQuery,
    private messageStore: MessageStore) {
    const channel = this.channelQuery.getActive();
    this.channelId = channel?.id!;
    this.communityId = channel?.communityId!;
    this.api = environment.api + "channels/";
    //this.api = environment.api + "channels/" + this.channelId + '/messages';
  }

  // todo get only few first messages, load more later as user scroll to the top of text-chat component
  // check if messages are cashed??
  getMessages(channelId: string){
    this.http.get<Message[]>(this.api + `${channelId}/messages`).subscribe(messages => {
      this.messageStore.set(messages);
    });
  }

  // we only want to send communityId, channelId and message text
  sendMessage(message: CreateMessageDto, file?: File | null){
    // todo send file and save it on backend
    // faking creating message
    // const message: Message = {
    //   channelId: messageDTO.channelId,
    //   edited: false,
    //   gifPath: "",
    //   id: this.idCounter.toString(),
    //   messageAttachment: undefined,
    //   reactions: [],
    //   respondsTo: "",
    //   text: messageDTO.text,
    //   updatedAt: new Date(),
    //   userId: messageDTO.userId
    // };

    message.communityId = this.communityId;

    const channelId = this.channelQuery.getActiveId();

    // Message don't have to be added to store, because rsocket is already listening for new messages
    this.http.post(this.api + `${channelId}/messages`, message).subscribe(res => {
      //console.log(res);
    });
  }

  editMessage(id: string, text: string){
    this.http.patch<Message>(this.api + `/${id}`, {
      text: text
    }).subscribe(message => {
      this.messageStore.update(message.id, message);
    });
  }

  deleteMessage(id: string){
    this.http.delete(this.api + `${id}/messages`).subscribe(res => {
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
}
