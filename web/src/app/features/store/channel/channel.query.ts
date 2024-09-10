import {Query} from "@datorama/akita";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {Channel, ChannelType} from "../../models/channel";
import {ChannelState, ChannelStore} from "./channel.store";

@Injectable({providedIn: 'root'})
export class ChannelQuery extends Query<ChannelState> {

  textChannels$: Observable<Channel[]> = this.select(state =>
    state.channels.filter(channel => channel.type === ChannelType.Text));

  voiceChannels$: Observable<Channel[]> = this.select(state =>
    state.channels.filter(channel => channel.type === ChannelType.Voice));

  constructor(protected override store: ChannelStore) {
    super(store);
  }

}
