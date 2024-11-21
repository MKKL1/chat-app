import {QueryEntity} from "@datorama/akita";
import {CommunityState, CommunityStore} from "./community.store";
import {Observable} from "rxjs";
import {inject, Injectable} from "@angular/core";
import {MemberQuery} from "../member/member.query";
import {TextChannelQuery} from "../textChannel/text.channel.query";
import {VoiceChannelQuery} from "../voiceChannel/voice.channel.query";
import {CommunityStats} from "../../models/community-stats";

@Injectable({ providedIn: 'root' })
export class CommunityQuery extends QueryEntity<CommunityState> {

  memberQuery = inject(MemberQuery);
  textChannelQuery = inject(TextChannelQuery);
  voiceChannelQuery = inject(VoiceChannelQuery);

  constructor(protected override store: CommunityStore) {
    super(store);
  }

  public getStats(): CommunityStats | undefined{
    const activeCommunity = this.getActive();

    if(activeCommunity === undefined){
      return undefined;
    }

    const textChannels = this.textChannelQuery.getAll({
      filterBy: entity => entity.communityId === activeCommunity.id
    });
    const voiceChannels = this.voiceChannelQuery.getAll({
      filterBy: entity => entity.communityId === activeCommunity.id
    });
    const members = this.memberQuery.getAll({
      filterBy: entity => entity.communityId === activeCommunity.id
    });
    const owner = members.find(m => m.id === activeCommunity.ownerId);

    return {
      community: activeCommunity,
      textChannelsCount: textChannels.length,
      voiceChannelsCount: voiceChannels.length,
      membersCount: members.length,
      ownerName: owner?.user.username!
    };
  }
}
