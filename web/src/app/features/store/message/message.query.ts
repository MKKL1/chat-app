import {Query} from "@datorama/akita";
import {MessageState, MessageStore} from "./message.store";
import {Observable} from "rxjs";
import {Message} from "../../models/message";
import {Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class MessageQuery extends Query<MessageState> {

  constructor(protected override store: MessageStore) {
    super(store);
  }

  // add searching for message by its id and its owner id,
  // maybe by date

  // search by community id
  messages$(channelId: string): Observable<Message[]> {
    console.log(channelId);
    return this.select(state =>
      state.messages.filter(message => message.channelId == channelId));
  }

  messagesByAuthorId$(authorId: string, channelId: string): Observable<Message[]>{
    return this.select(state =>
      state.messages.filter(message =>
        message.channelId === channelId && message.userId === authorId));
  }

}
