import { Injectable } from '@angular/core';
import {Message} from "../models/message";
import {MessageStore} from "../store/message/message.store";
import {CreateMessageDto} from "../models/create.message.dto";
import {Reaction} from "../models/reaction";

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  idCounter = 0;

  constructor(private messageStore: MessageStore) { }

  // todo call api for few first messages in chat, and store them in message store
  getMessages(channelId: string){

  }

  // we only want to send userId, channelId and message text
  sendMessage(messageDTO: CreateMessageDto, file: File | null){
    // todo send message to api and wait for response
    // todo send file and save it on backend
    // faking creating message
    const message: Message = {
      channelId: messageDTO.channelId,
      edited: false,
      gifPath: "",
      id: this.idCounter.toString(),
      messageAttachment: undefined,
      reactions: [],
      respondsTo: "",
      text: messageDTO.text,
      updatedAt: new Date(),
      userId: messageDTO.userId
    };

    this.idCounter++;
    this.messageStore.addMessage(message);
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
