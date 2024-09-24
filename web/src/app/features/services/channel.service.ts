import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Channel, ChannelType} from "../models/channel";
import {environment} from "../../../environment";
import {Observable, tap} from "rxjs";
import {CommunityQuery} from "../store/community/community.query";
import {ChannelStore} from "../store/channel/channel.store";

@Injectable({
  providedIn: 'root'
})
export class ChannelService {
  private readonly apiPath: string = environment.api + "channels";
  private communityId: string | undefined;

  // getting id of currently chosen community
  constructor(private http: HttpClient, private store: ChannelStore, private communityQuery: CommunityQuery) {
    this.communityQuery.community$.subscribe(community => {
      this.communityId = community.id;
    })
  }

  selectVoiceChannel(channel: Channel){
    this.store.selectVoiceChannel(channel);
  }

  selectTextChannel(channel: Channel){
    this.store.selectTextChannel(channel);
  }

  createChannel(channel: any): Observable<Channel>{
    if (this.communityId != null) {
      channel.communityId = this.communityId;
    }

    return this.http.post<Channel>(this.apiPath, channel).pipe(
      tap(newChannel => {
        // it wouldn't be necessary if I could just pass numbers in json,
        // but I can't because ids are too big and I have to map them to string
        // and I can't tell if number is other value than id, so it also is mapped to string
        // @ts-ignore
        newChannel.type = newChannel.type === '0' ? ChannelType.Text : ChannelType.Voice;
        this.store.addChannel(newChannel);
      })
    );
  }

  updateChannel(channel: Channel){
    return this.http.put<Channel>(this.apiPath + "/" + channel.id, {channel}).pipe(
      tap(updatedChannel => this.store.editChannel(updatedChannel))
    );
  }

  deleteChannel(id: string){
    return this.http.delete(this.apiPath + "/" + id).pipe(
      tap(res => {
        this.store.deleteChannel(id);
      })
    );
  }
}
