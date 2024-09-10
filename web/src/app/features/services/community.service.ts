import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environment";
import {BehaviorSubject, filter, map, mergeMap, Observable, of, tap} from "rxjs";
import {Community} from "../models/community";
import {UserService} from "./user.service";
import {CommunityStore} from "../store/community/community.store";
import {log} from "@angular-devkit/build-angular/src/builders/ssr-dev-server";
import {Channel} from "../models/channel";
import {ChannelStore} from "../store/channel/channel.store";
import {CommunityQuery} from "../store/community/community.query";

@Injectable({
  providedIn: 'root'
})
export class CommunityService {
  private readonly apiPath: string = environment.api + "communities";

  private communitiesSubject: BehaviorSubject<Community[]> = new BehaviorSubject<Community[]>([]);

  constructor(
    private http: HttpClient,
    private userService: UserService,
    private communityQuery: CommunityQuery,
    private communityStore: CommunityStore,
    private channelStore: ChannelStore
  ) { }

  // TODO to change community user need to click two times, also it catches community every time
  fetchCommunity(id: string){
    this.http.get(this.apiPath + "/" + id + "/info").pipe(
      map((res: any) => {
        // maybe map this on backend
        return {
          id: res.community.id,
          name: res.community.name,
          imageUrl: res.community.imageUrl,
          ownerId: res.community.ownerId,
          roles: res.roles,
          members: res.members,
          channels: res.channels
        }
      })
    ).subscribe({
      next: (community) => {
        this.communityStore.selectCommunity(community);
        this.channelStore.selectChannels(community.channels);
      },
      error: (err) => console.error(err)
    });
  }

  fetchCommunities() {
     this.http.get<Community[]>(this.apiPath
     ).subscribe({
       next: (communities) => this.communitiesSubject.next(communities),
       error: (err) => console.error(err)
     });
  }

  getUserCommunities(): Observable<Community[]> {
    return this.communitiesSubject.asObservable();
  }

  getOwnedCommunities(): Observable<Community[]> {
    return this.communitiesSubject.asObservable().pipe(
      map((communities: Community[]) =>
        communities.filter((community: Community) =>
          community.ownerId == this.userService.getUser().id))
    );
  }

  // change those types
  createCommunity(form: {name: string}) {
    this.http.post<Community>(this.apiPath, {name: form.name})
      .subscribe(
        community => this.communitiesSubject.next(
          [
            ...this.communitiesSubject.getValue(),
            community
          ]
        )
      );
  }

  // handle response -> delete community from list, message, navigate from community etc.
  deleteCommunity(id: string) {
    return this.http.delete(this.apiPath + "/" + id).subscribe(
      res => console.log(res)
    );
  }

  createInvitation(id: string, days: number){
    return this.http.post<{link: string}>(this.apiPath + "/" + id + "/invite",{days: days});
  }

  acceptInvitation(communityId: string, invitationId: string){
    return this.http.post(environment.api + "communities/" + communityId + "/join", {invitationId: invitationId});
  }

}
