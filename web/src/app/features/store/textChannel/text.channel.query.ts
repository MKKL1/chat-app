import {Injectable} from "@angular/core";
import {QueryEntity} from "@datorama/akita";
import {ChannelState, TextChannelStore} from "./text.channel.store";

@Injectable({ providedIn: 'root' })
export class TextChannelQuery extends QueryEntity<ChannelState> {

  constructor(protected override store: TextChannelStore) {
    super(store);
  }
}
