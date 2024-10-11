import {Community} from "../../models/community";
import {ActiveState, EntityState, EntityStore, getEntityType, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";

export interface CommunityState extends EntityState<Community, string>, ActiveState {}

@Injectable({ providedIn: 'root' })
@StoreConfig({name: 'community'})
export class CommunityStore extends EntityStore<CommunityState> {

  constructor() {
    super();
  }

  // setting flag so nextEntity won't be fetched again
  override akitaPreUpdateEntity(_: Readonly<getEntityType<CommunityState>>, nextEntity: any): getEntityType<CommunityState> {
    nextEntity.fullyFetched = true;
    return super.akitaPreUpdateEntity(_, nextEntity);
  }

}
