import {EntityStore, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";
import {ChannelState} from "../textChannel/text.channel.store";

@Injectable({ providedIn: 'root' })
@StoreConfig({name: 'community'})
export class VoiceChannelStore extends EntityStore<ChannelState> {

  constructor() {
    super();
  }

}
