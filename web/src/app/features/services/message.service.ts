import {Injectable, OnInit} from '@angular/core';
import {Message} from "../models/message";
import {MessageStore} from "../store/message/message.store";
import {CreateMessageDto} from "../models/create.message.dto";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../../environment";
import {TextChannelQuery} from "../store/textChannel/text.channel.query";
import {Params} from "@angular/router";
import {ID} from "@datorama/akita";
import {MessageQuery} from "../store/message/message.query";
import {map, Observable, tap} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MessageService{
  api: string = environment.api + "channels/";

  limit: number = 10;

  constructor(
    private http: HttpClient,
    private channelQuery: TextChannelQuery,
    private messageStore: MessageStore) {
  }

  // for now after switching channel all message data is lost
  getMessages(channelId: ID){
    const params: any = {
      limit: this.limit
    }

    this.http.get<Message[]>(this.api + `${channelId}/messages`, {
      params
    }).subscribe(messages => {
        this.messageStore.set(messages);
    });
  }

  loadMoreMessages(channelId: ID, lastMessageId?: ID): Observable<boolean>{
    const params: any = {
      limit: this.limit
    }

    if(lastMessageId){
      params.before = lastMessageId;
    }

    return this.http.get<Message[]>(this.api + `${channelId}/messages`, {
      params
    }).pipe(
      map(messages => {
        this.messageStore.add(messages);
        const isLastMessage = messages.length % (this.limit - 1) === 0;
        if(isLastMessage){
          console.log("Koniec danych");
        }
        return isLastMessage;
      })
    );
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
    this.http.patch<Message>(this.api + `/${id}`, {
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
}
