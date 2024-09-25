import {Injectable} from "@angular/core";
import {QueryEntity} from "@datorama/akita";
import {VoiceChannelStore} from "./voice.channel.store";
import {ChannelState} from "../textChannel/text.channel.store";

@Injectable({ providedIn: 'root' })
export class VoiceChannelQuery extends QueryEntity<ChannelState> {

  constructor(protected override store: VoiceChannelStore) {
    super(store);
  }
}
