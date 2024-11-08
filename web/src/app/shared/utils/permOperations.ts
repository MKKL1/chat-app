import {sumMasks} from "./binaryOperations";

export function applyOverwriteMask(permissions: bigint, overwrite: bigint): bigint {
  let newPermissions = permissions;

  newPermissions |= overwrite;
  newPermissions &= ~(overwrite >> BigInt(32));

  return newPermissions;
}

export function applyOverwrites(basePermission: bigint, permOverwrites: bigint[]): bigint {
  return applyOverwriteMask(basePermission, sumMasks(permOverwrites));
}
