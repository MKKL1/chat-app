import { Injectable } from '@angular/core';
import {environment} from "../../../environment";
import {HttpClient} from "@angular/common/http";
import {CommunityQuery} from "../store/community/community.query";
import {Observable} from "rxjs";
import {Role} from "../models/role";
import {Member} from "../models/member";

interface Operation{
  op: string,
  path: string,
  value: string
}

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  api = environment.api + "communities/";

  constructor(private http: HttpClient, private communityQuery: CommunityQuery) { }

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

    this.http.delete(this.api + communityId + "/roles/" + id).subscribe(role => {
      console.log(role);
    });
  }

  // I don't really know what should i send to update roles ???
  changeRoleMembers(role: Role, membersToAddIds: Member[], membersToRemoveIds: Member[]){
    const communityId = this.communityQuery.getActiveId();
    //let pathCounter: number = 1;

    const operations: Operation[] = [];

    // const addOperation: Operation = {
    //   op: 'add',
    //   path: path,
    //   value: []
    // };
    //
    // const removeOperation: Operation = {
    //   op: 'remove',
    //   path: path,
    //   value: []
    // };

    membersToRemoveIds.forEach(m => {
      operations.push({
        op: 'remove',
        path: '/members',
        value: m.id
      });
      //pathCounter++;
      // const member = { member:  m.id};
      // addOperation.value.push(member);
    });

    membersToAddIds.forEach(m => {
      operations.push({
        op: 'add',
        path: '/members',
        value: m.id
      });
      //pathCounter++;

      // const member = {member: m.id};
      // addOperation.value.push(member);
    });

    //console.log([removeOperation, addOperation])

    console.log(operations);

    this.http.patch(this.api + communityId + '/roles/' + role.id, operations).subscribe();
  }

}
