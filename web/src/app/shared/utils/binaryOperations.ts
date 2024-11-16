export function setBit(num: bigint, n: bigint): bigint {
  return num | (1n << n);
}

export function clearBit(num: bigint, n: bigint): bigint {
  return num & ~(1n << n);
}

export function isBitSet(num: bigint, n: bigint): boolean {
  return (num & (1n << n)) !== 0n;
}

export function sumMasks(masks: bigint[]): bigint {
  let accMask = 0n;
  for(let i = 0; i < masks.length; i++) {
    accMask |= masks[i];
  }
  return accMask;
}
