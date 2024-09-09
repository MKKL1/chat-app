import {Query} from "@datorama/akita";
import {CommunityState, CommunityStore} from "./community.store";
import {Observable} from "rxjs";
import {Community} from "../models/community";
import {Injectable} from "@angular/core";

@Injectable({ providedIn: 'root' })
export class CommunityQuery extends Query<CommunityState> {
  community$: Observable<Community> = this.select(state => state.selectedCommunity);

  isCommunitySelected$: Observable<boolean> = this.select(state => {
    return state.selectedCommunity.id !== '';
  });

  constructor(protected override store: CommunityStore) {
    super(store);
  }
}
