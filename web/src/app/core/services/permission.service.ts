import {Injectable} from "@angular/core";
import {BehaviorSubject, Observable} from "rxjs";
import {Permission} from "../../features/models/permission";
import {CommunityStore} from "../../features/store/community/community.store";
import {TextChannelStore} from "../../features/store/textChannel/text.channel.store";
import {CommunityQuery} from "../../features/store/community/community.query";
import {TextChannelQuery} from "../../features/store/textChannel/text.channel.query";
import {VoiceChannelQuery} from "../../features/store/voiceChannel/voice.channel.query";

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  //Represents current permission
  private permissionSubject: BehaviorSubject<Permission> = new BehaviorSubject<Permission>(
    new Permission("0"));

  public permissions$: Observable<Permission> = this.permissionSubject.asObservable();


  constructor(private communityQuery: CommunityQuery,
              private textChannelQuery: TextChannelQuery,
              private voiceChannelQuery: VoiceChannelQuery) {
    //TODO on destroy unsub
    communityQuery.selectActive().subscribe(community => {
      //Get base perm and save it
      //Get roles and sum their perm overwrites, then save it
      //Update permission, based on saved base perms and community overwrites
    })

    textChannelQuery.selectActive().subscribe(channel => {
      //Get overwrites for roles from channel
      //Select roles that user has
      //Get overwrites of given roles, sum them and save them
      //Update permission, based on saved base permission and ALL overwrites
    })

    voiceChannelQuery.selectActive().subscribe(channel => {
      //same as above
    })
  }
}
