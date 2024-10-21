import {Injectable} from "@angular/core";
import {QueryEntity} from "@datorama/akita";
import {RoleState, RoleStore} from "./role.store";

@Injectable({providedIn: 'root'})
export class RoleQuery extends QueryEntity<RoleState> {

  constructor(protected override store: RoleStore) {
    super(store);
  }

}
