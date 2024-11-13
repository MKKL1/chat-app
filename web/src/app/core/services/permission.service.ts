import {Injectable, OnDestroy} from "@angular/core";
import {BehaviorSubject, Observable, Subscription} from "rxjs";
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
import {Channel, ChannelRole} from "../../features/models/channel";

//TODO unit testing
@Injectable({
  providedIn: 'root'
})
export class PermissionService implements OnDestroy{
  //Represents current permission
  private permissionSubject: BehaviorSubject<Permission> = new BehaviorSubject<Permission>(
    new Permission("0"));

  public permissions$: Observable<Permission> = this.permissionSubject.asObservable();

  private basePerm: bigint = 0n;
  private permCommunityMask: bigint = 0n;
  private isOwner: boolean = false;

  private textChannelSub: Subscription;
  private voiceChannelSub: Subscription;

  constructor(private memberQuery: MemberQuery,
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

    this.textChannelSub = textChannelQuery.selectActive().subscribe(channel => {
      if(!channel) return;
      const userRoles = memberQuery.getEntity(channel.communityId + userService.getUser().id)?.roles
      this.updatePermsFromChannel(userRoles!, channel.overwrites)
    })

    this.voiceChannelSub = voiceChannelQuery.selectActive().subscribe(channel => {
      //same as above
      if(!channel) return;
      const userRoles = memberQuery.getEntity(channel.communityId + userService.getUser().id)?.roles
      this.updatePermsFromChannel(userRoles!, channel.overwrites)
    })
  }

  ngOnDestroy(): void {
    this.textChannelSub.unsubscribe();
    this.voiceChannelSub.unsubscribe();
  }

  private updatePermsFromChannel(userRoles: string[], channelRoles: ChannelRole[]) {
    let newPerms: Permission;
    if(this.isOwner) {
      newPerms = new Permission(0xFFFFFFFFn)
    } else {
      //Get overwrites that affect user
      const overwritesOfChannelForUser = channelRoles
        .filter((channelRole: ChannelRole) => userRoles?.includes(channelRole.roleId))
        .map((channelRole: ChannelRole) => channelRole.overwrites)
        .map((overwrite: string) => BigInt(overwrite))

      //TODO handle null
      //Sum all permission overwrites (community and channel)
      const permChannelMask = sumMasks(overwritesOfChannelForUser!)
      const combinedMask = permChannelMask | this.permCommunityMask

      //Update permission, based on saved base permission and ALL overwrites
      newPerms = new Permission(applyOverwrite(this.basePerm, combinedMask))
    }
    this.permissionSubject.next(newPerms)
  }

  public setCommunityPermission(basePermissions: bigint, userPermissions: bigint[], isOwner: boolean) {
    this.basePerm = basePermissions
    this.isOwner = isOwner //Setting isOwner, not sure if it may cause problems in future
    let newPerms: Permission;
    if(isOwner) {
      newPerms = new Permission(0xFFFFFFFFn)
    } else {
      //Calculate summed overwrite mask and save it
      this.permCommunityMask = sumMasks(userPermissions)

      //Apply overwrites
      newPerms = new Permission(applyOverwrite(this.basePerm, this.permCommunityMask))
    }

    //Emit new permissions
    this.permissionSubject.next(newPerms)
  }

  public getPermission(): Permission {
    return this.permissionSubject.value;
  }


}
