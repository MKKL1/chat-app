import {inject, Injectable} from '@angular/core';
import {environment} from "../../../environment";
import {HttpClient} from "@angular/common/http";
import {CommunityQuery} from "../store/community/community.query";
import {Observable} from "rxjs";
import {Role} from "../models/role";
import {Member} from "../models/member";
import {EventService} from "../../core/events/event.service";
import {RoleStore} from "../store/role/role.store";

interface Operation{
  op: string,
  path: string,
  value: string | number
}

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  api = environment.api + "communities/";

  constructor(private http: HttpClient,
              private communityQuery: CommunityQuery,
              private eventService: EventService,
              private roleStore: RoleStore) {
      this.eventService.on('ROLE_CREATE_EVENT', this.handleCreateRole);
      this.eventService.on('ROLE_UPDATE_EVENT', this.handleUpdateRole);
      this.eventService.on('ROLE_DELETE_EVENT', this.handleDeleteRole);
  }

  createRole(name: string, permissions: bigint): Observable<Role>{
    const communityId = this.communityQuery.getActiveId();

    return this.http.post<Role>(this.api + communityId + "/roles", {
      name: name,
      permissionOverwrites: permissions.toString(),
      members: []
    });
  }

  deleteRole(id: string){
    const communityId = this.communityQuery.getActiveId();
    this.http.delete(this.api + communityId + "/roles/" + id).subscribe();
  }

  changeRoleMembers(role: Role, membersToAddIds: Member[], membersToRemoveIds: Member[]): Observable<any>{
    const communityId = this.communityQuery.getActiveId();
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

    console.log(operations);

    return this.http.patch(this.api + communityId + '/roles/' + role.id, operations);
  }

  // handling events
  private handleCreateRole = (res: any) => {
   res.role.communityId = res.role.community;
   this.roleStore.add(res.role);
  };

  // {
  //   "role": {
  //   "id": "64234317622018048",
  //     "name": "rolename",
  //     "permissionOverwrites": "26",
  //     "community": "63919088711237632"
  // },
  //   "members": [
  //   "63919009480835072"
  // ]
  // }

  // there is no info if members where deleted only if they were added
  // maybe remove all members from community and cached them again?
  private handleUpdateRole = (res: any) => {
    console.log(res);
  };

  private handleDeleteRole = (role: any) => {
   console.log(role);
   this.roleStore.remove(role.roleId);
  };

}
