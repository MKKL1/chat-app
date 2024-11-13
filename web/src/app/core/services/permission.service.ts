import {Injectable, OnDestroy} from "@angular/core";
import {BehaviorSubject, Observable, Subscription} from "rxjs";
import {Permission} from "../../features/models/permission";
import {TextChannelQuery} from "../../features/store/textChannel/text.channel.query";
import {VoiceChannelQuery} from "../../features/store/voiceChannel/voice.channel.query";
import {sumMasks} from "../../shared/utils/binaryOperations";
import {UserService} from "./user.service";
import {applyOverwrite, applyOverwrites} from "../../shared/utils/permOperations";
import {MemberQuery} from "../../features/store/member/member.query";
import {Channel, ChannelRole} from "../../features/models/channel";
import {Role} from "../../features/models/role";
import {CommunityQuery} from "../../features/store/community/community.query";
import {User} from "../../features/models/user";
import {RoleQuery} from "../../features/store/role/role.query";

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

  private textChannelSub: Subscription;
  private voiceChannelSub: Subscription;

  constructor(private memberQuery: MemberQuery,
              private roleQuery: RoleQuery,
              private communityQuery: CommunityQuery,
              private textChannelQuery: TextChannelQuery,
              private voiceChannelQuery: VoiceChannelQuery,
              private userService: UserService
              ) {
    // permission should be computed once again after community changed
    // or after event connected to role (update, delete)

    // Moving it back to community service, as doing it here requires a lot of calls to akita store
    // But it should be here
    // I decided to do version with lot of calls because permission will be updated
    // also when update or delete role event occurs, and leaving it outside this class
    // would be redundant
    // communityQuery.selectActive().subscribe(community => {
    //   //Get base perm and save it
    //
    //TODO  setCommunityPermission()
    //
    // })

    this.textChannelSub = textChannelQuery.selectActive().subscribe(channel => {
      if(!channel) return;
      const userRoles = memberQuery.getEntity(channel.communityId + userService.getUser().id)?.roles
      //this.updatePermsFromChannel(userRoles!, channel.overwrites)
    })

    this.voiceChannelSub = voiceChannelQuery.selectActive().subscribe(channel => {
      //same as above
      if(!channel) return;
      const userRoles = memberQuery.getEntity(channel.communityId + userService.getUser().id)?.roles
      //this.updatePermsFromChannel(userRoles!, channel.overwrites)
    })
  }

  private updatePermsFromChannel(userRoles: string[], channelRoles: ChannelRole[]) {
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
    const newPerms = new Permission(applyOverwrite(this.basePerm, combinedMask))
    this.permissionSubject.next(newPerms)
    console.log("New perms: ", newPerms)
  }

  public setCommunityPermission() {
    const community = this.communityQuery.getActive()!;

    console.log(community);

    const currentUser = this.userService.getUser();

    // getting base permission for community and list of
    // permission overwrite delivered by different roles belonging to user
    this.basePerm = BigInt(this.communityQuery.getActive()!.basePermissions!);
    const isOwner = community.ownerId === currentUser.id;
    // getting roles of current user
    const userPermissions = this.getUserPermissionList(currentUser, community.id);

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

  public getUserPermissionList(currentUser: User, communityId: string): bigint[]{
    const communityRoles = this.roleQuery.getAll({
      filterBy: entity =>  entity.communityId === communityId
    });
    const currentUserRoles = this.memberQuery.getAll({
      filterBy: entity => entity.user.id === currentUser.id
    }).at(0)?.roles!;
    return communityRoles
      .filter((role: Role) => currentUserRoles.includes(role.id))
      .map((role: Role) => role.permissionOverwrites)
      .map((perm: string) => BigInt(perm));
  }

  ngOnDestroy(): void {
    this.textChannelSub.unsubscribe();
    this.voiceChannelSub.unsubscribe();
  }

}
