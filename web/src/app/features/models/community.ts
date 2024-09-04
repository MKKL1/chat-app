import {User} from "./user";

export interface Community {
  id: number;
  name: string;
  imageUrl: string;
  ownerId: number
}
