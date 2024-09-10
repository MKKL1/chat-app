import {Community} from "../../models/community";
import {Store, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";
import {Channel} from "../../models/channel";
import {Member} from "../../models/member";
import {Role} from "../../models/role";

export interface CommunityState {
  communities: Community[];
  selectedCommunity: Community;
}

export function createInitState(): CommunityState {
  return {
    communities: [],
    selectedCommunity: {
      id: '',
      name: '',
      imageUrl: '',
      ownerId: '',
      channels: [],
      members: [],
      roles: []
    }
  };
}

@Injectable({ providedIn: 'root' })
@StoreConfig({name: 'community'})
export class CommunityStore extends Store<CommunityState> {

  constructor() {
    super(createInitState());
  }

  // maybe split those methods into seperated files
  // all those functions are shit
  // beside simply updating community I cannot do anything else
  // i must split it into separated stores and handle changes
  selectCommunity(community: Community){
    this.update(state => ({selectedCommunity: community}));
  }

  addCommunity(community: Community){
    this.update(state => ({communities: [...state.communities, community]}));
  }

  updateCommunity(updatedCommunity: Community){
    this.update(state => ({communities: state.communities.map(community =>
        community.id === updatedCommunity.id ? { ...community, ...updatedCommunity } : community
      )}));
  }

  deleteCommunity(id: string){
    this.update(state => ({communities: state.communities.filter(community => community.id !== id)}));
  }

  setChannels(id: string, channels: Channel[]){
    this.update(state => ({
      communities: state.communities.map(community =>
        community.id === id ? { ...community, channels: [...community.channels, ...channels] } : community
      )
    }));
  }

  setMembers(id: string, members: Member[]){
    this.update(state => ({
      communities: state.communities.map(community =>
        community.id === id ? { ...community, members: [...community.members, ...members] } : community
      )
    }));
  }

  addMember(id: string, member: Member){
    this.update(state => ({
      communities: state.communities.map(community =>
        community.id === id ? { ...community, members: [...community.members, member] } : community
      )
    }));
  }

  updateMember(id: string, updatedMember: Member){
    this.update(state => ({
      communities: state.communities.map(community =>
        community.id === id
          ? {
            ...community,
            members: community.members.map(member =>
              member.id === updatedMember.id ? { ...member, ...updatedMember } : member
            )
          }
          : community
      )
    }));
  }

  deleteMember(communityId: string, id: string){
    this.update(state => ({
      communities: state.communities.map(community =>
        community.id === communityId
          ? {
            ...community,
            channels: community.channels.filter(channel => channel.id !== id)
          }
          : community
      )
    }));
  }

  setRoles(id: string, roles: Role[]){
    this.update(state => ({
      communities: state.communities.map(community =>
        community.id === id ? { ...community, roles: [...community.roles, ...roles] } : community
      )
    }));
  }

  addRole(id: string, role: Role){
    this.update(state => ({
      communities: state.communities.map(community =>
        community.id === id ? { ...community, roles: [...community.roles, role] } : community
      )
    }));
  }

  updateRole(id: string, updatedRole: Role){
    this.update(state => ({
      communities: state.communities.map(community =>
        community.id === id
          ? {
            ...community,
            roles: community.roles.map(role =>
              role.id === updatedRole.id ? { ...role, ...updatedRole } : role
            )
          }
          : community
      )
    }));
  }

  deleteRole(communityId: string, id: string){
    this.update(state => ({
      communities: state.communities.map(community =>
        community.id === communityId
          ? {
            ...community,
            roles: community.roles.filter(role => role.id !== id)
          }
          : community
      )
    }));
  }

  clear(){
    this.update(createInitState());
  }

}
