import { Injectable } from '@angular/core';
import {Message} from "../models/message";
import {MessageStore} from "../store/message/message.store";
import {CreateMessageDto} from "../models/create.message.dto";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environment";
import {TextChannelQuery} from "../store/textChannel/text.channel.query";

@Injectable({
  providedIn: 'root'
})
export class MessageService {
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
      this.api = environment.api + "channels/" + this.channelId + '/messages';
  }

  // todo get only few first messages, load more later as user scroll to the top of text-chat component
  getMessages(){
    this.http.get<Message[]>(this.api).subscribe(messages => {
      this.messageStore.addMessages(messages);
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

    // Message don't have to be added to store, because rsocket is already listening for new messages
    this.http.post(this.api, message).subscribe(res => {
      //console.log(res);
    });
  }

  editMessage(id: string, text: string){
    this.http.patch<Message>(this.api + `/${id}`, {
      text: text
    }).subscribe(message => {
      this.messageStore.editMessage(message);
    });
  }

  deleteMessage(id: string){
    this.http.delete(this.api + `/${id}`).subscribe(res => {
      this.messageStore.deleteMessage(id);
    });
  }

  // todo implement
  addReaction(reaction: string, messageId: string, userId: string){
    console.log(reaction);
  }
}
