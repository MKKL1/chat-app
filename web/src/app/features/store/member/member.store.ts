import {EntityState, EntityStore, getEntityType, StoreConfig} from "@datorama/akita";
import {Member} from "../../models/member";
import {Injectable} from "@angular/core";
import {filePathMapping} from "../../../shared/utils/utils";

export interface MemberState extends EntityState<Member, string> {}

@Injectable({providedIn: 'root'})
@StoreConfig({name: 'member', idKey: 'storeId'})
export class MemberStore extends EntityStore<MemberState>{
  constructor() {
    super();
  }

  override akitaPreAddEntity(newEntity: Member): getEntityType<MemberState> {
    newEntity.id = newEntity.user.id;
    newEntity.user.imageUrl = filePathMapping(newEntity.user.imageUrl!);

    // storeId ensures each member added is unique and prevents overwriting
    // existing store entries with the same id. This approach may lead to redundant user data,
    // for example, the same user being stored multiple times as separate objects,
    // differing only by community.

    // TODO
    // Ideally, communities would be stored in an array within a single user object
    // to avoid redundancy, but I don't have time to implement that now.
    newEntity.storeId = newEntity.communityId + newEntity.user.id;

    return super.akitaPreAddEntity(newEntity);
  }
}
