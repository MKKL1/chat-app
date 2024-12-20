import {Injectable} from '@angular/core';
import {environment} from "../../../environment";
import {HttpClient} from "@angular/common/http";
import {CommunityQuery} from "../store/community/community.query";
import {EMPTY, Observable} from "rxjs";
import {Role} from "../models/role";
import {Member} from "../models/member";
import {EventService} from "../../core/events/event.service";
import {RoleStore} from "../store/role/role.store";
import {MemberQuery} from "../store/member/member.query";
import {MemberStore} from "../store/member/member.store";
import {PermissionService} from "../../core/services/permission.service";
import {Operation} from "../models/operation";

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  api = environment.api + "communities/";

  constructor(private http: HttpClient,
              private communityQuery: CommunityQuery,
              private memberQuery: MemberQuery,
              private memberStore: MemberStore,
              private permissionService: PermissionService,
              private eventService: EventService,
              private roleStore: RoleStore) {
  }

  // if user didn't use somewhere this service, callbacks will not be registered
  init(){
    this.eventService.on('ROLE_CREATE_EVENT', this.handleCreateRole);
    this.eventService.on('ROLE_UPDATE_EVENT', this.handleUpdateRole);
    this.eventService.on('ROLE_DELETE_EVENT', this.handleDeleteRole);
  }

  createRole(name: string, permissions: bigint): Observable<any>{
    const communityId = this.communityQuery.getActiveId();

    return this.http.post(this.api + communityId + "/roles", {
      name: name,
      permissionOverwrites: permissions.toString(),
      members: []
    });
  }

  editRole(roleId: string, name?: string, permissions?: string): Observable<any>{
    const operations: Operation[] = []

    if(name){
      operations.push({
        op: 'replace',
        path: '/role/name',
        value: name});
    }

    if(permissions){
      operations.push({
        op: 'replace',
        path: '/role/permissionOverwrites',
        value: permissions
      });
    }

    if(operations?.length === 0){
      return EMPTY;
    }

    return this.http.patch(`${environment.api}roles/${roleId}`,operations);
  }

  deleteRole(id: string){
    this.http.delete(environment.api + "roles/" + id).subscribe();
  }

  changeRoleMembers(role: Role, membersToAddIds: Member[], membersToRemoveIds: Member[]): Observable<any>{
    const operations: Operation[] = [];

    membersToAddIds.forEach(m => {
      operations.push({
        op: 'add',
        path: '/members/-',
        value: m.id
      });
    });

    membersToRemoveIds.forEach(m => {
      operations.push({
        op: 'remove',
        path: '/members/0',
        value: m.id
      });
    });

    return this.http.patch(environment.api + 'roles/' + role.id, operations);
  }

  // handling events
  private handleCreateRole = (res: any) => {
   res.role.communityId = res.role.community;
   this.roleStore.add(res.role);
  };

  private handleUpdateRole = (res: any) => {
    const role = res.role;
    const members = res.members;

    this.roleStore.update(role.id, role);

    const currentMembers = this.memberQuery.getByCommunity(role.community);
    const updatedMembers: Member[] = [];
    currentMembers.forEach(member => {
      let roles = [...member.roles];

      if (members.includes(member.id)) {
        // make sure that role is added to member
        // and do it only if role isn't already added
        if (!roles.includes(role.id)) {
          roles.push(role.id);
        }
      } else {
        // delete role from member
        const index = roles.indexOf(role.id);
        if (index !== -1) {
          roles.splice(index, 1);
        }
      }

      updatedMembers.push({ ...member, roles });
    });

    this.memberStore.upsertMany(updatedMembers);

    // compute current user permissions once again
    this.permissionService.setCommunityPermission();
  };

  private handleDeleteRole = (role: {roleId: string}) => {
    this.roleStore.remove(role.roleId);

    // handle deleting role from members
    let updatedMembers: Member[] = [];
    const currentMembers = this.memberQuery.getByCommunity(this.communityQuery.getActiveId()!);
    currentMembers.forEach(member => {
      let roles = [...member.roles];
      if(roles.includes(role.roleId)){
        const index = roles.indexOf(role.roleId);
        if (index !== -1) {
          roles.splice(index, 1);
        }
      }
      updatedMembers.push({ ...member, roles });
    });

    this.memberStore.upsertMany(updatedMembers);
    this.permissionService.setCommunityPermission();
  };

}
