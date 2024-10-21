import {Permission} from "./permission";

export interface Role {
  id: string;
  name: string;
  permission: Permission;
  community: string;
}
