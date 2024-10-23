import {EntityState, EntityStore, getEntityType, StoreConfig} from "@datorama/akita";
import {Member} from "../../models/member";
import {Injectable} from "@angular/core";
import {Community} from "../../models/community";
import {filePathMapping} from "../../../shared/utils/utils";
import {CommunityState} from "../community/community.store";

export interface MemberState extends EntityState<Member, string> {}

@Injectable({providedIn: 'root'})
@StoreConfig({name: 'member'})
export class MemberStore extends EntityStore<MemberState>{
  constructor() {
    super();
  }

  override akitaPreAddEntity(newEntity: Member): getEntityType<MemberState> {
    newEntity.id = newEntity.user.id;
    newEntity.user.imageUrl = filePathMapping(newEntity.user.imageUrl!);
    return super.akitaPreAddEntity(newEntity);
  }
}
