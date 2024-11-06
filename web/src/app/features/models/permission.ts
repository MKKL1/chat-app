export class Permission{
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

  constructor(permissionBytes: string) {
    this.rawValue = permissionBytes;

    console.log(permissionBytes);

    const bytes = BigInt(permissionBytes);

    const ADMINISTRATOR_BIT = 0n;
    const MODIFY_ROLE_BIT = 1n;
    const CREATE_INVITATION_BIT = 2n;
    const CREATE_CHANNEL_BIT = 3n;
    const MODIFY_CHANNEL_BIT = 4n;
    const CREATE_MESSAGE_BIT = 5n;
    const DELETE_MESSAGE_BIT = 6n;
    const CREATE_REACTION_BIT = 7n;

    this.isAdministrator = this.checkPermission(bytes, ADMINISTRATOR_BIT);
    this.canModifyRole = this.checkPermission(bytes, MODIFY_ROLE_BIT);
    this.canCreateInvitation = this.checkPermission(bytes, CREATE_INVITATION_BIT);
    this.canCreateChannel = this.checkPermission(bytes, CREATE_CHANNEL_BIT);
    this.canModifyChannel = this.checkPermission(bytes, MODIFY_CHANNEL_BIT);
    this.canCreateMessage = this.checkPermission(bytes, CREATE_MESSAGE_BIT);
    this.canDeleteMessage = this.checkPermission(bytes, DELETE_MESSAGE_BIT);
    this.canCreateReaction = this.checkPermission(bytes, CREATE_REACTION_BIT);
  }

  private checkPermission(bytes: bigint, bitPosition: bigint): boolean {
    return (bytes & (1n << bitPosition)) !== 0n;
  }

}
