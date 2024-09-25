import {EntityState, EntityStore, StoreConfig} from "@datorama/akita";
import {Role} from "../../models/role";
import {Injectable} from "@angular/core";

export interface RoleState extends EntityState<Role, string> {}

@Injectable({providedIn: 'root'})
@StoreConfig({name: 'role'})
export class RoleStore extends EntityStore<RoleState>{

  constructor() {
    super();
  }

}
