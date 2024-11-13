import {QueryEntity} from "@datorama/akita";
import {CommunityState, CommunityStore} from "./community.store";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable({ providedIn: 'root' })
export class CommunityQuery extends QueryEntity<CommunityState> {

  constructor(protected override store: CommunityStore) {
    super(store);
  }

  // TODO implement
  getActiveFull(){

  }

  selectActiveFull(){

  }

  isCommunitySelected$: Observable<boolean> =
    this.select(state => state.active !== undefined);
}
