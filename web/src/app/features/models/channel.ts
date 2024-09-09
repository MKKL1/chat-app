export interface Channel{
  id: string | null;
  name: string;
  communityId: string;
  type: 'TEXT' | 'VOICE';
}
