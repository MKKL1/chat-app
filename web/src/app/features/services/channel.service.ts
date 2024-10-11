import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Channel, ChannelType} from "../models/channel";
import {environment} from "../../../environment";
import {Observable, tap} from "rxjs";
import {CommunityQuery} from "../store/community/community.query";
import {TextChannelStore} from "../store/textChannel/text.channel.store";
import {VoiceChannelStore} from "../store/voiceChannel/voice.channel.store";

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

    this.voiceChannelStore.setActive(channel.id);
  }

  selectTextChannel(channel: Channel){
    if(channel.type === ChannelType.Voice){
      return;
    }

    if(!channel.id){
      return;
    }

    this.textChannelStore.setActive(channel.id);
  }

  createChannel(channel: any): Observable<Channel>{
    channel.communityId = this.communityQuery.getActiveId();

    return this.http.post<Channel>(this.apiPath, channel).pipe(
      tap(newChannel => {
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
      })
    );
  }

  updateChannel(channel: Channel){
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

  // I'm too lazy to check channel type here
  // and there won't be channels with same id in both stores anyway
  deleteChannel(id: string){
    return this.http.delete(this.apiPath + "/" + id).pipe(
      tap(res => {
        this.voiceChannelStore.remove(id);
        this.textChannelStore.remove(id);
      })
    );
  }
}
