import {Community} from "../models/community";
import {Store, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";

export interface CommunityState {
  community: Community;
}

export function createInitState(): CommunityState {
  return {
    community: {
      id: '',
      name: '',
      imageUrl: '',
      ownerId: ''
    }
  };
}

// maybe expand to all communities?
@Injectable({ providedIn: 'root' })
@StoreConfig({name: 'community'})
export class CommunityStore extends Store<CommunityState> {

  constructor() {
    super(createInitState());
  }

  selectCommunity(community: Community){
    console.log(community);
    this.update(state => ({community: community}));
  }

  clear(){
    this.update(createInitState());
  }

}
