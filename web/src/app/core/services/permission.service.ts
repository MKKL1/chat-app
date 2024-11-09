import {Injectable} from "@angular/core";
import {BehaviorSubject, Observable} from "rxjs";
import {Permission} from "../../features/models/permission";
import {CommunityStore} from "../../features/store/community/community.store";
import {TextChannelStore} from "../../features/store/textChannel/text.channel.store";
import {CommunityQuery} from "../../features/store/community/community.query";
import {TextChannelQuery} from "../../features/store/textChannel/text.channel.query";
import {VoiceChannelQuery} from "../../features/store/voiceChannel/voice.channel.query";
import {sumMasks} from "../../shared/utils/binaryOperations";
import {UserService} from "./user.service";
import {Role} from "../../features/models/role";
import {applyOverwrite, applyOverwrites} from "../../shared/utils/permOperations";
import {MemberQuery} from "../../features/store/member/member.query";
import {RoleQuery} from "../../features/store/role/role.query";

//TODO unit testing
@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  //Represents current permission
  private permissionSubject: BehaviorSubject<Permission> = new BehaviorSubject<Permission>(
    new Permission("0"));

  public permissions$: Observable<Permission> = this.permissionSubject.asObservable();

  private basePerm: bigint = 0n;
  private permCommunityMask: bigint = 0n;

  constructor(private communityQuery: CommunityQuery,
              private memberQuery: MemberQuery,
              private roleQuery: RoleQuery,
              private textChannelQuery: TextChannelQuery,
              private voiceChannelQuery: VoiceChannelQuery,
              private userService: UserService
              ) {
    //TODO on destroy unsub

    // Moving it back to community service, as doing it here requires a lot of calls to akita store
    // But it should be here
    // communityQuery.selectActive().subscribe(community => {
    //   //Get base perm and save it
    //
    //TODO  setCommunityPermission()
    //
    // })

    textChannelQuery.selectActive().subscribe(channel => {
      //Get overwrites for roles from channel
      //Select roles that user has
      //Get overwrites of given roles, sum them and save them
      //Update permission, based on saved base permission and ALL overwrites

      console.log(channel)
    })

    voiceChannelQuery.selectActive().subscribe(channel => {
      //same as above
    })
  }

  public setCommunityPermission(basePermissions: bigint, userPermissions: bigint[]) {
    this.basePerm = basePermissions
    //Calculate summed overwrite mask and save it
    this.permCommunityMask = sumMasks(userPermissions)

    //Apply overwrites
    const newPerms = new Permission(applyOverwrite(this.basePerm, this.permCommunityMask))

    //Emit new permissions
    this.permissionSubject.next(newPerms)
  }

  public getPermission(): Permission {
    return this.permissionSubject.value;
  }
}
