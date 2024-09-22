import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environment";
import {BehaviorSubject, map, Observable} from "rxjs";
import {Community} from "../models/community";
import {UserService} from "../../core/services/user.service";
import {CommunityStore} from "../store/community/community.store";
import {ChannelType} from "../models/channel";
import {ChannelStore} from "../store/channel/channel.store";
import {CommunityQuery} from "../store/community/community.query";
import {MatSnackBar} from "@angular/material/snack-bar";

@Injectable({
  providedIn: 'root'
})
export class CommunityService {
  private readonly apiPath: string = environment.api + "communities";

  private communitiesSubject: BehaviorSubject<Community[]> = new BehaviorSubject<Community[]>([]);

  private snackBar = inject(MatSnackBar);

  constructor(
    private http: HttpClient,
    private userService: UserService,
    private communityStore: CommunityStore,
    private communityQuery: CommunityQuery,
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
          channels: res.channels.map((channel: any) => ({
            ...channel,
            type: channel.type === '0' ? ChannelType.Text : ChannelType.Voice
          }))
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
       next: (communities) => {
         this.communitiesSubject.next(communities)
       },
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
    this.http.delete(this.apiPath + "/" + id).pipe()
      .subscribe({
      next: _ => {
        // ???
        this.communityStore.deleteCommunity(id);

        // updating list state
        const currentCommunities = this.communitiesSubject.getValue();
        const updatedCommunities = currentCommunities.filter(community => community.id !== id);
        this.communitiesSubject.next(updatedCommunities);

        // need to unselect community if deleted one was actually selected
        this.communityQuery.community$.subscribe(community => {
          if(community.id === id){
            this.communityStore.clear();
          }
        });

        // global info
        this.snackBar.open("Community deleted", 'Ok', {duration: 3000})
      },
      error: err => this.snackBar.open("Error occurred during deleting community " + err)
    });
  }

  createInvitation(id: string, days: number){
    return this.http.post<{link: string}>(this.apiPath + "/" + id + "/invite",{days: days});
  }

  acceptInvitation(communityId: string, invitationId: string){
    return this.http.post(environment.api + "communities/" + communityId + "/join", {invitationId: invitationId});
  }

}
