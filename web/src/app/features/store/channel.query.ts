import {Query} from "@datorama/akita";
import {CommunityState, CommunityStore} from "./community.store";
import {Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class ChannelQuery extends Query<CommunityState> {

  constructor(protected override store: CommunityStore) {
    super(store);
  }

}
