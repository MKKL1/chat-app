import {Community} from "../../models/community";
import {ActiveState, EntityState, EntityStore, getEntityType, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";
import {filePathMapping} from "../../../shared/utils/utils";

export interface CommunityState extends EntityState<Community, string>, ActiveState {}

@Injectable({ providedIn: 'root' })
@StoreConfig({name: 'community'})
export class CommunityStore extends EntityStore<CommunityState> {

  constructor() {
    super();
  }

  override akitaPreAddEntity(newEntity: Community): getEntityType<CommunityState> {
    newEntity.imageUrl = filePathMapping(newEntity.imageUrl);
    return super.akitaPreAddEntity(newEntity);
  }

  // setting flag so nextEntity won't be fetched again
  override akitaPreUpdateEntity(_: Readonly<getEntityType<CommunityState>>, nextEntity: any): getEntityType<CommunityState> {
    nextEntity.imageUrl = filePathMapping(nextEntity.imageUrl);
    nextEntity.fullyFetched = true;
    return super.akitaPreUpdateEntity(_, nextEntity);
  }

}
