import {Injectable} from "@angular/core";
import {QueryEntity} from "@datorama/akita";
import {MemberState, MemberStore} from "./member.store";

@Injectable({providedIn: 'root'})
export class MemberQuery extends QueryEntity<MemberState> {

  constructor(protected override store: MemberStore) {
    super(store);
  }

}
