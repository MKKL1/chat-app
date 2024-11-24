import {Community} from "./community";

export interface CommunityStats{
  community: Community;
  membersCount: number;
  voiceChannelsCount: number;
  textChannelsCount: number;
  ownerName: string;
}
