import {ID, Order, QueryEntity} from "@datorama/akita";
import {MessageState, MessageStore} from "./message.store";
import {Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class MessageQuery extends QueryEntity<MessageState> {

  constructor(protected override store: MessageStore) {
    super(store);
  }

  selectAllOrderByDesc(channelId: ID){
    console.log('in store');
    return this.selectAll({
      filterBy: entity => entity.channelId === channelId,
      sortBy: 'updatedAt',
      sortByOrder: Order.ASC
    });
  }

}
