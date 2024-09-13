import {Injectable} from "@angular/core";
import {Query} from "@datorama/akita";
import {CommunityState, CommunityStore} from "../community/community.store";

@Injectable({providedIn: 'root'})
export class MemberQuery extends Query<CommunityState> {

  constructor(protected override store: CommunityStore) {
    super(store);
  }

}
