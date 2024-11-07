import {User} from "./user";

export interface Member{
  id: string;
  roles: string[];
  user: User;
  communityId: string;
  storeId: string;
}
