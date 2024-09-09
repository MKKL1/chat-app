import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Channel} from "../models/channel";
import {environment} from "../../../environment";
import {tap} from "rxjs";
import {CommunityQuery} from "../store/community.query";

@Injectable({
  providedIn: 'root'
})
export class ChannelService {
  private readonly apiPath: string = environment.api + "channels";
  private communityId: string | undefined;

  // getting id of currently chosen community
  constructor(private http: HttpClient, private communityQuery: CommunityQuery) {
    this.communityQuery.community$.subscribe(community => {
      this.communityId = community.id;
    })
  }

  // here beside returning info to component, I also need to save channel in store
  createChannel(channel: any){
    console.log(channel);

    if (this.communityId != null) {
      channel.communityId = this.communityId;
    }

    this.http.post(this.apiPath, channel).pipe(
      tap(res => {
        console.log(res);
        // TODO save in store
      })
    ).subscribe(res => {
      console.debug(res);
    });
  }
}
