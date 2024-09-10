export interface Channel{
  id: string | null;
  name: string;
  communityId: string;
  type: ChannelType;
}

export enum ChannelType {
  Text = 0,
  Voice = 1
}
