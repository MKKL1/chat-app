import {Injectable, OnDestroy} from "@angular/core";
import {BehaviorSubject, Observable, Subscription} from "rxjs";
import {Permission} from "../../features/models/permission";
import {sumMasks} from "../../shared/utils/binaryOperations";
import {UserService} from "./user.service";
import {applyOverwrite} from "../../shared/utils/permOperations";
import {MemberQuery} from "../../features/store/member/member.query";
import {Role} from "../../features/models/role";
import {CommunityQuery} from "../../features/store/community/community.query";
import {User} from "../../features/models/user";
import {RoleQuery} from "../../features/store/role/role.query";

@Injectable({
  providedIn: 'root'
})
export class PermissionService implements OnDestroy{
  //Represents current permission
  private permissionSubject: BehaviorSubject<Permission> = new BehaviorSubject<Permission>(
    new Permission("0")
  );

  public permissions$: Observable<Permission> = this.permissionSubject.asObservable();

  private basePerm: bigint = 0n;
  private permCommunityMask: bigint = 0n;

  private textChannelSub: Subscription;
  private voiceChannelSub: Subscription;

  constructor(private memberQuery: MemberQuery,
              private roleQuery: RoleQuery,
              private communityQuery: CommunityQuery,
              private userService: UserService
              ) {}

  public setCommunityPermission() {
    const community = this.communityQuery.getActive()!;
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
