import {Permission} from "./permission";

export interface Role {
  id: string;
  name: string;
  communityId: string;
  permission: string;
}
