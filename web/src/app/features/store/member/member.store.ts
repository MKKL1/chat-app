import {EntityState, EntityStore, StoreConfig} from "@datorama/akita";
import {Member} from "../../models/member";
import {Injectable} from "@angular/core";

export interface MemberState extends EntityState<Member, string> {}

@Injectable({providedIn: 'root'})
@StoreConfig({name: 'member'})
export class MemberStore extends EntityStore<MemberState>{
  constructor() {
    super();
  }
}
