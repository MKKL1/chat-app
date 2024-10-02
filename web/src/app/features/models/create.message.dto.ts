export interface CreateMessageDto {
  text: string;
  communityId?: string;
  respondsToMessage?: string;
  gifLink?: string;
}
