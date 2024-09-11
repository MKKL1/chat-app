import {Query} from "@datorama/akita";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {Channel, ChannelType} from "../../models/channel";
import {ChannelState, ChannelStore} from "./channel.store";

@Injectable({providedIn: 'root'})
export class ChannelQuery extends Query<ChannelState> {

  textChannels$: Observable<Channel[]> = this.select(state =>
    state.channels.filter(channel => channel.type === ChannelType.Text));

  textChannel$: Observable<Channel> = this.select(state => state.selectedTextChannel);

  isTextChannelSelected$: Observable<boolean> = this.select(state => state.selectedTextChannel.id !== '');

  voiceChannels$: Observable<Channel[]> = this.select(state =>
    state.channels.filter(channel => channel.type === ChannelType.Voice));

  voiceChannel$: Observable<Channel> = this.select(state => state.selectedVoiceChannel);

  isVoiceChannelSelected$: Observable<boolean> = this.select(state => state.selectedVoiceChannel.id !== '');

  constructor(protected override store: ChannelStore) {
    super(store);
  }

}
