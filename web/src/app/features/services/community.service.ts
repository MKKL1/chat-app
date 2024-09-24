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

  fetchCommunity(id: string){
    this.communityQuery.selectEntity(id).subscribe(community => {
      // community is already stored in app
      // so instead calling api it is set from storage
      if(community !== undefined){
        this.communityStore.setActive(community.id);
      }

      // call api to get community data
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
        // maybe it's too much data duplication?
        next: (community) => {
          // adding community to list of communities
          // next time when this community will be selected
          // there won't be need to fetch it from api
          this.communityStore.add(community);
          // making this community selected one
          // after this community can be referenced by other parts of app
          // as a currently chosen one
          this.communityStore.setActive(community.id);
          // TODO normalize data
          // Selecting channels from current community
          // this line is very dangerous because if I forget to call it
          // channels from previous community will be showed instead of proper ones
          this.channelStore.selectChannels(community.channels);
        },
        error: (err) => console.error(err)
      });
    });
  }

  fetchCommunities() {
    // communities already fetched
    if(this.communityQuery.getCount() !== 0){
      return;
    }

     this.http.get<Community[]>(this.apiPath
     ).subscribe({
       next: (communities) => {
         this.communityStore.set(communities);
         //this.communitiesSubject.next(communities)
       },
       error: (err) => console.error(err)
     });
  }

  getUserCommunities(): Observable<Community[]> {
    return this.communityQuery.selectAll();
    //return this.communitiesSubject.asObservable();
  }

  getOwnedCommunities(): Observable<Community[]> {
    // I don't know how filtering works
    return this.communityQuery.selectAll();
    // return this.communitiesSubject.asObservable().pipe(
    //   map((communities: Community[]) =>
    //     communities.filter((community: Community) =>
    //       community.ownerId == this.userService.getUser().id))
    // );
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

  deleteCommunity(id: string) {
    this.http.delete(this.apiPath + "/" + id).pipe()
      .subscribe({
      next: _ => {
        // ???
        this.communityStore.remove(id);

        // updating list state
        const currentCommunities = this.communitiesSubject.getValue();
        const updatedCommunities = currentCommunities.filter(community => community.id !== id);
        this.communitiesSubject.next(updatedCommunities);

        // need to unselect community if deleted one was actually selected
        if(this.communityQuery.getActiveId() === id){
          this.communityStore.removeActive(id);
        }

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
