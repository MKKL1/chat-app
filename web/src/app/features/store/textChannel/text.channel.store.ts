import {ActiveState, EntityState, EntityStore, getEntityType, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";
import {Channel} from "../../models/channel";

export interface ChannelState extends EntityState<Channel, string>, ActiveState {}

@Injectable({ providedIn: 'root' })
@StoreConfig({name: 'community'})
export class TextChannelStore extends EntityStore<ChannelState> {

  constructor() {
    super();
  }



}
