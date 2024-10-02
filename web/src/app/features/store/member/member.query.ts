import {Injectable} from "@angular/core";
import {Query} from "@datorama/akita";
import {CommunityState, CommunityStore} from "../community/community.store";
import {MemberState, MemberStore} from "./member.store";

@Injectable({providedIn: 'root'})
export class MemberQuery extends Query<MemberState> {

  constructor(protected override store: MemberStore) {
    super(store);
  }

}
