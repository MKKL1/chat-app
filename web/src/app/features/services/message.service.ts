import { Injectable } from '@angular/core';
import {Message} from "../models/message";
import {MessageStore} from "../store/message/message.store";
import {CreateMessageDto} from "../models/create.message.dto";
import {Reaction} from "../models/reaction";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environment";
import {ChannelQuery} from "../store/channel/channel.query";
import {CommunityQuery} from "../store/community/community.query";

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  api: string = '';
  channelId: string = '';
  communityId: string = '';

  constructor(
    private http: HttpClient,
    private channelQuery: ChannelQuery,
    private messageStore: MessageStore) {
    this.channelQuery.textChannel$.subscribe(channel => {
      this.channelId = channel.id!;
      this.communityId = channel.communityId!;
      this.api = environment.api + "channels/" + this.channelId + '/messages';
    })

  }

  // todo call api for few first messages in chat, and store them in message store
  getMessages(channelId: string){

  }

  // we only want to send communityId, channelId and message text
  sendMessage(message: string, file?: File | null){
    // todo send message to api and wait for response
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

    // Message don't have to be added to store, because rsocket is already listening for new messages
    this.http.post(this.api, {
      channelId: this.channelId,
      communityId: this.communityId,
      text: message
    }).subscribe(res => {
      console.log(res);
    });
  }

  editMessage(message: Message){
    // todo send message to api and wait for response
    this.messageStore.editMessage(message);
  }

  deleteMessage(id: string){
    // todo send message to api and wait for response
    this.messageStore.deleteMessage(id);
  }

  // todo implement
  addReaction(reaction: string, messageId: string, userId: string){
    console.log(reaction);
  }
}
