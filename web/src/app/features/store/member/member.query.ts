import {Injectable} from "@angular/core";
import {ID, QueryEntity} from "@datorama/akita";
import {MemberState, MemberStore} from "./member.store";

@Injectable({providedIn: 'root'})
export class MemberQuery extends QueryEntity<MemberState> {

  constructor(protected override store: MemberStore) {
    super(store);
  }

  getByCommunity(id: string | ID | number){
    return this.getAll({
      filterBy: entity => entity.communityId === id
    });
  }

}
