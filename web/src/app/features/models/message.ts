import {Reaction} from "./reaction";
import {MessageAttachment} from "./message.attachment";

export interface Message{
  id: string;
  text: string;
  channelId: string;
  userId: string;
  edited: boolean;
  updatedAt: Date;
  respondsToMessage?: string;
  reactions: Reaction[];
  messageAttachment?: MessageAttachment;
  gifLink?: string
}
