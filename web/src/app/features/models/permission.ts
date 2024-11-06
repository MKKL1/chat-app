export interface Permission{
  // number in which bytes define if specific action is allowed
  rawValue: string;

  isAdministrator: boolean;
  canModifyRole: boolean;
  canCreateInvitation: boolean;
  canCreateChannel: boolean;
  canModifyChannel: boolean;
  canCreateMessage: boolean;
  canDeleteMessage: boolean;
  canCreateReaction: boolean;

}
