import {Community} from "../../models/community";
import {ActiveState, EntityState, EntityStore, Store, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";
import {Channel} from "../../models/channel";
import {Member} from "../../models/member";
import {Role} from "../../models/role";

export interface CommunityState extends EntityState<Community, string>, ActiveState {}

// function createInitState(): CommunityState {
//   return {
//     communities: [],
//     selectedCommunity: {
//       id: '',
//       name: '',
//       imageUrl: '',
//       ownerId: '',
//       channels: [],
//       members: [],
//       roles: []
//     }
//   };
// }

@Injectable({ providedIn: 'root' })
@StoreConfig({name: 'community'})
export class CommunityStore extends EntityStore<CommunityState> {

  constructor() {
    super();
  }

  // selectCommunity(community: Community){
  //   this.update(state => ({selectedCommunity: community}));
  // }
  //
  // addCommunity(community: Community){
  //   this.update(state => ({communities: [...state.communities, community]}))
  // }
  //
  // updateCommunity(updatedCommunity: Community){
  //   this.update(state => ({communities: state.communities.map(community =>
  //       community.id === updatedCommunity.id ? { ...community, ...updatedCommunity } : community
  //     )}));
  // }
  //
  // deleteCommunity(id: string){
  //   this.update(state => ({communities: state.communities.filter(community => community.id !== id)}));
  // }
  //
  // clear(){
  //   this.update(createInitState());
  // }

}
