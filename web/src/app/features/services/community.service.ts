import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../../environment";
import {catchError, EMPTY, map, Observable, switchMap, tap, throwError} from "rxjs";
import {Community} from "../models/community";
import {CommunityStore} from "../store/community/community.store";
import {Channel, ChannelType} from "../models/channel";
import {Role} from "../models/role";
import {CommunityQuery} from "../store/community/community.query";
import {TextChannelStore} from "../store/textChannel/text.channel.store";
import {VoiceChannelStore} from "../store/voiceChannel/voice.channel.store";
import {MemberStore} from "../store/member/member.store";
import {RoleStore} from "../store/role/role.store";
import {VoiceChannelQuery} from "../store/voiceChannel/voice.channel.query";
import {TextChannelQuery} from "../store/textChannel/text.channel.query";
import {EventService} from "../../core/events/event.service";
import {Member} from "../models/member";
import {PermissionService} from "../../core/services/permission.service";
import {MessageService} from "primeng/api";

@Injectable({
  providedIn: 'root'
})
export class CommunityService {
  private readonly apiPath: string = environment.api + "communities";

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
    private eventService: EventService,
    private messageService: MessageService,
    private permissionService: PermissionService
  ) {
    this.eventService.on('COMMUNITY_UPDATE_EVENT', this.handleCommunityEdit);
    this.eventService.on('COMMUNITY_DELETE_EVENT', this.handleCommunityDelete);
  }

  changeCommunity(id: string){
    const community = this.communityQuery.getEntity(id);

    this.eventService.handleNewStreamRequest(community?.id!);

    // After selecting new community as active, active channels have to be removed
    // to prevent producing list of channels containing ones from both communities
    this.textChannelStore.removeActive(this.textChannelQuery.getActiveId());
    this.voiceChannelStore.removeActive(this.voiceChannelQuery.getActiveId());

    // Data about communities is stored after initiating app
    // however data about channels, members etc. may be lacking
    // I introduce flag fullyFetched, so this method will now decide if data should be
    // fetched from api (fullyFetched is false) or taken from storage (fullyFetched is true)
    // This flag will be automatically set after updating community object
    if(community?.fullyFetched !== undefined && community?.fullyFetched){
      this.communityStore.setActive(community.id);
      this.permissionService.setCommunityPermission();
      return;
    }

    // this part is executed when data is not fetched yet
    // call api to get community data
    this.fetchCommunity(id);
  }

  private fetchCommunity(id: string){
    this.http.get(this.apiPath + "/" + id + "/info").pipe(
      map((res: any) => {
        return {
          community: res.community,
          roles: res.roles.map((role: Role) => ({
            ...role,
            communityId: res.community.id
          })),
          members: res.members.map((member: Member) => ({
            ...member,
            communityId: res.community.id
          })),
          channels: res.channels.map((channelData: any) => ({
            ...channelData.channel,
            type: channelData.channel.type === '0' ? ChannelType.Text : ChannelType.Voice,
            overwrites: channelData.overwrites,
            participants: channelData.participants
          }))
        };
      })
    ).subscribe({
      next: (response) => {
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

        // storing data related to community in separated stores
        this.textChannelStore.add(textChannels);
        this.voiceChannelStore.add(voiceChannels);
        this.roleStore.add(response.roles);
        this.memberStore.add(response.members);

        // Updating existing entity triggers setting fullyFetched flag
        // which prevents fetching this community again
        this.communityStore.update(id, response.community);
        // making this community selected one
        // after this community can be referenced by other parts of app
        // as a currently chosen one
        this.communityStore.setActive(response.community.id);
        this.permissionService.setCommunityPermission();
      },
      error: (err) => console.error(err)
    });
  }

  getCommunities(){
    this.communityStore.setLoading(true);
    this.communityQuery.selectHasCache().pipe(
      switchMap(hasCache => {
        const apiCall = this.http.get<Community[]>(this.apiPath).pipe(
          tap(communities => this.communityStore.set(communities))
        );
        this.communityStore.setLoading(false);
        return hasCache ? EMPTY : apiCall
      })
    ).subscribe(_ => {
      this.communityStore.setLoading(false);
    });
  }

  createCommunity(form: {name: string}, file: File | undefined) {
    const formData = new FormData();
    formData.append('community', new Blob([JSON.stringify(form)], { type: 'application/json' }));

    if(file){
      formData.append('file', file, file.name);
    }

    this.http.post<Community>(this.apiPath, formData, {
      headers: new HttpHeaders({
        'enctype': 'multipart/form-data'
      })
    })
    .pipe(
      catchError(err => {
        console.error(err);
        this.messageService.add({severity: 'error', summary: 'Cannot create new community'});
        return throwError(() => err);
    }))
    .subscribe(community => {
      this.communityStore.add(community);
      this.messageService.add({severity: 'success', summary: `Created ${community.name} community`});
    });
  }

  editCommunity(id: string, name: string, file: File | undefined){
    const formData = new FormData();
    formData.append('community', new Blob(
      [JSON.stringify({name: name})],
      { type: 'application/json' })
    );

    if(file){
      formData.append('file', file, file.name);
    }

    this.http.patch(this.apiPath + '/' + id, formData, {
      headers: new HttpHeaders({
        'enctype': 'multipart/form-data'
      })
    }).subscribe(res => {
      console.log(res);
      this.messageService.add({severity: 'info', summary: 'Community edited'});
    })
  }

  deleteCommunity(id: string) {
    this.http.delete(this.apiPath + "/" + id).subscribe();
  }

  private handleCommunityEdit = (res: any) => {
    this.communityStore.update(res.id, res);
  };

  private handleCommunityDelete = (id: string) => {
    this.communityStore.remove(id);
    // need to unselect community if deleted one was actually selected
    if(this.communityQuery.getActiveId() === id){
      this.communityStore.removeActive(id);
    }
  };

  createInvitation(id: string, days: number){
    return this.http.post<{link: string}>(this.apiPath + "/" + id + "/invite",{days: days});
  }

  getInvitationInfo(communityId: string, invitationId: string): Observable<Community> {
    return this.http.get<Community>(environment.api + "communities/" + communityId + "/invitation/" + invitationId);
  }

  acceptInvitation(communityId: string, invitationId: string){
    return this.http.post(environment.api + "communities/" + communityId + "/join", {invitationId: invitationId});
  }

}
