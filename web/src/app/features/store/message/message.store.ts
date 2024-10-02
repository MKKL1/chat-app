import {Message} from "../../models/message";
import {EntityState, EntityStore, Store, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";

export interface MessageState extends EntityState<Message, string> {}

@Injectable({providedIn: 'root'})
@StoreConfig({name: 'message'})
export class MessageStore extends EntityStore<MessageState>{

  constructor() {
    super();
  }
}
