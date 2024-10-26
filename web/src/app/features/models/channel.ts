export interface Channel{
  id: string;
  name: string;
  communityId: string;
  type: ChannelType;
  lastMessageId?: string;
  messagesState?: ChannelMessagesState;
}

export enum ChannelType {
  Text = 0,
  Voice = 1
}

export enum ChannelMessagesState {
  NotFetched,
  PartlyFetched,
  FullyFetched
}
