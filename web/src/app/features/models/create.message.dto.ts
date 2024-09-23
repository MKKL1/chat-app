export interface CreateMessageDto {
  text: string;
  communityId?: string;
  messageToRespond?: string;
  gifLink?: string;
}
