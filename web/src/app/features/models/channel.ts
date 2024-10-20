export interface Channel{
  id: string;
  name: string;
  communityId: string;
  type: ChannelType;
  lastMessageId?: string;
}

export enum ChannelType {
  Text = 0,
  Voice = 1
}
