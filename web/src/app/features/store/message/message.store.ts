import {Message} from "../../models/message";
import {EntityState, Store, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";

//export interface MessageState extends EntityState<Message, string> {}


export interface MessageState {
  messages: Message[]
}

function createInitialState(): MessageState{
  return {
    messages: []
  }
}

@Injectable({providedIn: 'root'})
@StoreConfig({name: 'message'})
export class MessageStore extends Store<MessageState>{

  constructor() {
    super(createInitialState());
  }

  addMessage(message: Message){
    this.update(state => ({messages: [...state.messages, message]}));
  }

  addMessages(messages: Message[]) {
    this.update(state => ({messages: [...state.messages, ...messages]}));
  }

  // TODO implement
  editMessage(message: Message){

  }

  deleteMessage(id: string){
    this.update(state => ({
      messages: state.messages.filter(message => message.id !== id
    )}));
  }

  clear(){
    this.update(createInitialState());
  }

}
