import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Channel} from "../models/channel";
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
      tap(deletedChannel => {
        this.store.deleteChannel(id);
      })
    );
  }
}
