import {Community} from "./community";
import {User} from "./user";

export interface CommunityResponse{
  community: Community;
  owner: User;
}
