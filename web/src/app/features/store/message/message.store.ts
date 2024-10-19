import {Message} from "../../models/message";
import {EntityState, EntityStore, getEntityType, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";
import {filePathMapping, snowflakeDate} from "../../../shared/utils/utils";

export interface MessageState extends EntityState<Message, string> {}

@Injectable({providedIn: 'root'})
@StoreConfig({name: 'message'})
export class MessageStore extends EntityStore<MessageState>{
  constructor() {
    super();
  }

  override akitaPreAddEntity(newEntity: Message): getEntityType<MessageState> {
    // mapping paths of files, so they can be fetched from api
    if(newEntity.attachments){
      newEntity.attachments = newEntity.attachments.map(attachment => {
        return {
          ...attachment,
          path: filePathMapping(attachment.path)
        };
      });
    }
    
    // getting date from snowflake
    newEntity.updatedAt = snowflakeDate(newEntity.id);

    return super.akitaPreAddEntity(newEntity);
  }
}
