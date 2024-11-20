import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Channel, ChannelType} from "../models/channel";
import {environment} from "../../../environment";
import {Observable, tap} from "rxjs";
import {CommunityQuery} from "../store/community/community.query";
import {TextChannelStore} from "../store/textChannel/text.channel.store";
import {VoiceChannelStore} from "../store/voiceChannel/voice.channel.store";
import {EventService} from "../../core/events/event.service";
import {Operation} from "./role.service";

@Injectable({
  providedIn: 'root'
})
export class ChannelService {
  private readonly apiPath: string = environment.api + "channels";

  constructor(private http: HttpClient,
              private textChannelStore: TextChannelStore,
              private voiceChannelStore: VoiceChannelStore,
              private communityQuery: CommunityQuery,
              private eventService: EventService) {
    this.eventService.on('CHANNEL_CREATE_EVENT', this.handleAddChannel);
    this.eventService.on('CHANNEL_UPDATE_EVENT', this.handleUpdateChannel);
    this.eventService.on('CHANNEL_DELETE_EVENT', this.handleDeleteChannel);
    this.eventService.on('PARTICIPANT_CREATE_EVENT', this.handleCreateParticipant);
    this.eventService.on('PARTICIPANT_DELETE_EVENT', this.handleDeleteParticipant);
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

  createChannel(channel: any): Observable<Channel>{
    // channel.communityId = this.communityQuery.getActiveId(); not needed/will throw exception
    //This request was moved to /communities as POST /channels/{communityId} is very similar to GET /channels/{channelId}
    return this.http.post<Channel>(environment.api + "communities/" + this.communityQuery.getActiveId() + "/channels", channel);
  }

  updateChannel(channelId: string, name: string): Observable<any>{
    const operations: Operation[] = [
      {
        op: 'replace', path: '/channel/name', value: name
      }
    ];

    return this.http.put(this.apiPath + "/" + channelId, operations);
  }

  // I'm too lazy to check channel type here
  // and there won't be channels with same id in both stores anyway
  deleteChannel(id: string): Observable<any>{
    return this.http.delete(this.apiPath + "/" + id);
  }

  updatePermissions(id: string, roleId: string, permissions: bigint){
    const operations = [
      {op: 'replace', path: '/overwrites', value: [
          {"roleId": roleId,"overwrites": permissions.toString()}
        ]
      }
    ];

    console.log(operations);

    return this.http.put(environment.api + 'channels/' + id, operations);
  }

  private handleAddChannel = (newChannel: Channel) => {
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
  };

  private handleUpdateChannel = (newChannel: any) => {
    const channel: Channel = newChannel.channel;
    // @ts-ignore
    // 1 === voice
    if(channel.type === '1'){
      this.voiceChannelStore.update(channel.id, {name: channel.name});
    } else {
      this.textChannelStore.update(channel.id, {name: channel.name});
    }
  };

  private handleDeleteChannel = (id: any) => {
    this.voiceChannelStore.remove(id);
    this.textChannelStore.remove(id);
  };

  private handleCreateParticipant = (res: {channelId: string, userId: string}) => {
    this.voiceChannelStore.update(res.channelId, (channel) => {
      return {
        ...channel,
        participants: [...channel.participants!, res.userId],
      };
    });
  };

  private handleDeleteParticipant = (res: {channelId: string, userId: string}) => {
    this.voiceChannelStore.update(res.channelId, (channel) => {
      return {
        ...channel,
        participants: channel.participants!.filter(id => id !== res.userId),
      };
    });
  };

}
