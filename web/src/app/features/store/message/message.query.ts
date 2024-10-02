import {QueryEntity} from "@datorama/akita";
import {MessageState, MessageStore} from "./message.store";
import {Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class MessageQuery extends QueryEntity<MessageState> {

  constructor(protected override store: MessageStore) {
    super(store);
  }

  // add searching for message by its id and its owner id,
  // maybe by date

}
