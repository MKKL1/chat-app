import {Injectable} from "@angular/core";
import {QueryEntity} from "@datorama/akita";
import {VoiceChannelState, VoiceChannelStore} from "./voice.channel.store";

@Injectable({ providedIn: 'root' })
export class VoiceChannelQuery extends QueryEntity<VoiceChannelState> {

  constructor(protected override store: VoiceChannelStore) {
    super(store);
  }
}
