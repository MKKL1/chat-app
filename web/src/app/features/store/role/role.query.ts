import {Injectable} from "@angular/core";
import {Query} from "@datorama/akita";
import {CommunityState, CommunityStore} from "../community/community.store";
import {RoleState, RoleStore} from "./role.store";

@Injectable({providedIn: 'root'})
export class RoleQuery extends Query<RoleState> {

  constructor(protected override store: RoleStore) {
    super(store);
  }

}
