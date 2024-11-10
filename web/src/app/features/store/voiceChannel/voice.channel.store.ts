import {ActiveState, EntityState, EntityStore, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";
import {Channel} from "../../models/channel";

export interface VoiceChannelState extends EntityState<Channel, string>, ActiveState {}

@Injectable({ providedIn: 'root' })
@StoreConfig({name: 'community'})
export class VoiceChannelStore extends EntityStore<VoiceChannelState> {

  constructor() {
    super();
  }

}
