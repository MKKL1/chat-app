export interface Channel{
  id: string;
  name: string;
  communityId: string;
  type: ChannelType;
  overwrites: ChannelRole[];
  lastMessageId?: string;
  messagesState?: ChannelMessagesState;
}

export interface ChannelRole {
  overwrites: string;
  roleId: string;
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
