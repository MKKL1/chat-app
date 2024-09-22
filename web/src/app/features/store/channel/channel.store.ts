import {Channel, ChannelType} from "../../models/channel";
import {Store, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";

export interface ChannelState {
  channels: Channel[];
  selectedVoiceChannel: Channel;
  selectedTextChannel: Channel;
}

function createInitState(): ChannelState {
  return {
    channels: [],
    selectedVoiceChannel: {id: '', name: '', communityId: '', type: ChannelType.Voice},
    selectedTextChannel: {id: '', name: '', communityId: '', type: ChannelType.Voice},
  };
}

// More convenient methods for changing state exists, but I can't use them for some reason

@Injectable({providedIn: 'root'})
@StoreConfig({name: 'channel'})
export class ChannelStore extends Store<ChannelState> {

  constructor() {
    super(createInitState());
  }

  selectChannels(channels: Channel[]){
    console.log(channels);
    this.update(state => ({channels: channels}));
  }

  selectVoiceChannel(channel: Channel){
    this.update(state => ({selectedVoiceChannel: channel}));
  }

  selectTextChannel(channel: Channel){
    this.update(state => ({selectedTextChannel: channel}));
  }

  addChannel(channel: Channel){
    this.update(state => ({channels: [...state.channels, channel]}));
  }

  // check if edited channel isn't currently chosen one, if so handle it
  editChannel(updatedChannel: Channel){
    this.update(state => ({
      ...state, channels: state.channels.map(channel => channel.id === updatedChannel.id ? updatedChannel : channel)
    }))
  }

  deleteChannel(id: string){
    this.update(state => ({channels: state.channels.filter(channel => channel.id !== id)}));
  }

  clear(){
    this.update(createInitState());
  }
}
