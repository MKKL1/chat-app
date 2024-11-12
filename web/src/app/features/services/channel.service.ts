import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Channel, ChannelType} from "../models/channel";
import {environment} from "../../../environment";
import {Observable, tap} from "rxjs";
import {CommunityQuery} from "../store/community/community.query";
import {TextChannelStore} from "../store/textChannel/text.channel.store";
import {VoiceChannelStore} from "../store/voiceChannel/voice.channel.store";
import {VoiceChannelQuery} from "../store/voiceChannel/voice.channel.query";

@Injectable({
  providedIn: 'root'
})
export class ChannelService {
  private readonly apiPath: string = environment.api + "channels";

  constructor(
    private http: HttpClient,
    private textChannelStore: TextChannelStore,
    private voiceChannelStore: VoiceChannelStore,
    private communityQuery: CommunityQuery) {
  }

  selectVoiceChannel(channel: Channel){
    if(channel.type === ChannelType.Text){
      return;
    }

    if(!channel.id){
      return;
    }

    //TODO update permissions
    this.voiceChannelStore.setActive(channel.id);
  }

  selectTextChannel(channel: Channel){
    if(channel.type === ChannelType.Voice){
      return;
    }

    if(!channel.id){
      return;
    }

    //TODO update permissions
    this.textChannelStore.setActive(channel.id);
  }

  // I add channel in separated method, so it can be also used in EventService
  addChannel(newChannel: Channel){
    // it wouldn't be necessary if I could just pass numbers in json,
    // but I can't because ids are too big and I have to map them to string
    // and I can't tell if number is other value than id, so it also is mapped to string
    // @ts-ignore
    newChannel.type = newChannel.type === '0' ? ChannelType.Text : ChannelType.Voice;

    if(newChannel.type === ChannelType.Text){
      this.textChannelStore.add(newChannel);
    } else {
      this.voiceChannelStore.add(newChannel);
    }
  }

  createChannel(channel: any): Observable<Channel>{
    // channel.communityId = this.communityQuery.getActiveId(); not needed/will throw exception
    //This request was moved to /communities as POST /channels/{communityId} is very similar to GET /channels/{channelId}
    return this.http.post<Channel>(environment.api + "communities/" + this.communityQuery.getActiveId() + "/channels", channel);
  }

  updateChannel(channel: Channel){
    console.log(channel);
    // @ts-ignore
    channel.type = channel.type === 0 ? 'TEXT_CHANNEL' : 'VOICE_CHANNEL';
    console.log(channel);

    return this.http.put<Channel>(this.apiPath + "/" + channel.id, {channel}).pipe(
      tap(updatedChannel => {
        if(!updatedChannel.id){
          return;
        }

        updatedChannel.type === ChannelType.Text ?
          this.textChannelStore.update(updatedChannel.id, updatedChannel) :
          this.voiceChannelStore.update(updatedChannel.id, updatedChannel);
      })
    );
  }

  removeChannel(id: string){
    this.voiceChannelStore.remove(id);
    this.textChannelStore.remove(id);
  }

  // I'm too lazy to check channel type here
  // and there won't be channels with same id in both stores anyway
  deleteChannel(id: string){
    return this.http.delete(this.apiPath + "/" + id);
  }
}
