export function setBit(num: bigint, bitNumber: bigint): bigint {
  return num | (1n << bitNumber);
}

export function clearBit(num: bigint, bitNumber: bigint): bigint {
  return num & ~(1n << bitNumber);
}

export function isBitSet(num: bigint, bitNumber: bigint): boolean {
  return (num & (1n << bitNumber)) !== 0n;
}

export function sumMasks(masks: bigint[]): bigint {
  let accMask = 0n;
  for(let i = 0; i < masks.length; i++) {
    accMask |= masks[i];
  }
  return accMask;
}
