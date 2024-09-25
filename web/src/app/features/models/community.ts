import {Channel} from "./channel";
import {Member} from "./member";
import {Role} from "./role";

export interface Community {
  id: string;
  name: string;
  imageUrl: string;
  ownerId: string;
  fullyFetched: boolean;
  channels: Channel[];
  members: Member[];
  roles: Role[];
}
