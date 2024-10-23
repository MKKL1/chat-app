import {Reaction} from "./reaction";
import {MessageAttachment} from "./message.attachment";

export interface Message{
  id: string;
  text: string;
  channelId: string;
  userId: string;
  edited: boolean;
  updatedAt: Date;
  dateFormatted: string;
  respondsToMessage?: string;
  reactions: Reaction[];
  attachments: MessageAttachment[];
  gifLink?: string
}
