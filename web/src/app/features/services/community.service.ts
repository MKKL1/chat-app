import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environment";
import {BehaviorSubject, EMPTY, map, Subscription, switchMap, tap} from "rxjs";
import {Community} from "../models/community";
import {UserService} from "../../core/services/user.service";
import {CommunityStore} from "../store/community/community.store";
import {Channel, ChannelType} from "../models/channel";
import {CommunityQuery} from "../store/community/community.query";
import {MatSnackBar} from "@angular/material/snack-bar";
import {TextChannelStore} from "../store/textChannel/text.channel.store";
import {VoiceChannelStore} from "../store/voiceChannel/voice.channel.store";
import {MemberStore} from "../store/member/member.store";
import {RoleStore} from "../store/role/role.store";
import {VoiceChannelQuery} from "../store/voiceChannel/voice.channel.query";
import {TextChannelQuery} from "../store/textChannel/text.channel.query";
import {MessageStore} from "../store/message/message.store";
import {EventService} from "../../core/events/event.service";

@Injectable({
  providedIn: 'root'
})
export class CommunityService {
  private readonly apiPath: string = environment.api + "communities";

  private snackBar = inject(MatSnackBar);

  constructor(
    private http: HttpClient,
    private communityStore: CommunityStore,
    private communityQuery: CommunityQuery,
    private voiceChannelStore: VoiceChannelStore,
    private voiceChannelQuery: VoiceChannelQuery,
    private textChannelStore: TextChannelStore,
    private textChannelQuery: TextChannelQuery,
    private memberStore: MemberStore,
    private roleStore: RoleStore,
    private eventService: EventService
  ) { }

  fetchCommunity(id: string){
    const community = this.communityQuery.getEntity(id);

    console.log(community);

    this.eventService.handleNewStreamRequest(community?.id!);

    // Data about communities is stored in storage after initiating app
    // however data about channels, members etc. may be lacking
    // I introduce flag fullyFetched, so this method will now decide if data should be
    // fetch data api (fullyFetched is false) or taken from storage (fullyFetched is true)
    // This flag will be automatically set after updating community object
    if(community?.fullyFetched !== undefined && community?.fullyFetched){
      this.communityStore.setActive(community.id);
      // After selecting new community as active, active channels have to be removed
      // to prevent producing list of channels containing ones from both communities
      this.textChannelStore.removeActive(this.textChannelQuery.getActiveId());
      this.voiceChannelStore.removeActive(this.voiceChannelQuery.getActiveId());
      return;
    }

    // call api to get community data
    this.http.get(this.apiPath + "/" + id + "/info").pipe(
      map((res: any) => {
        // maybe map this on backend
        return {
          community: res.community,
          roles: res.roles,
          members: res.members,
          channels: res.channels.map((channel: any) => ({
            ...channel,
            type: channel.type === '0' ? ChannelType.Text : ChannelType.Voice
          }))
        }
      })
    ).subscribe({
      next: (response) => {
        // Updating existing entity triggers setting fullyFetched flag
        // which prevents fetching this community again
        this.communityStore.update(id, response.community);
        // making this community selected one
        // after this community can be referenced by other parts of app
        // as a currently chosen one
        this.communityStore.setActive(response.community.id);
        // All relational data connected to community is normalized
        // and saved to separated stores which will make state of app easier to maintain

        const textChannels: Channel[] = [];
        const voiceChannels: Channel[] = [];

        // dividing channels into 2 array by channel type
        response.channels.forEach((channel: Channel) => {
          if(channel.type === ChannelType.Text){
            textChannels.push(channel);
          } else {
            voiceChannels.push(channel);
          }
        });

        this.textChannelStore.add(textChannels);
        this.voiceChannelStore.add(voiceChannels);
        this.memberStore.add(response.members);
        this.roleStore.add(response.roles);
      },
      error: (err) => console.error(err)
    });
  }

  getCommunities(){
    this.communityQuery.selectHasCache().pipe(
      switchMap(hasCache => {
        const apiCall = this.http.get<Community[]>(this.apiPath).pipe(
          tap(communities => this.communityStore.set(communities))
        );
        return hasCache ? EMPTY : apiCall
      })
    ).subscribe();
  }

  // change those types
  createCommunity(form: {name: string}) {
    this.http.post<Community>(this.apiPath, {name: form.name})
      .subscribe(
        community => this.communityStore.add(community)
      );
  }

  editCommunity(){
    // TODO implement
  }

  deleteCommunity(id: string) {
    this.http.delete(this.apiPath + "/" + id).pipe()
      .subscribe({
      next: _ => {
        // ???
        this.communityStore.remove(id);

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
