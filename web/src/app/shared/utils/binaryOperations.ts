export function setBit(num: bigint, n: bigint){
  return num | (1n << n);
}

export function sumMasks(masks: bigint[]): bigint {
  let accMask = 0n;
  for(let i = 0; i < masks.length; i++) {
    accMask |= masks[i];
  }
  return accMask;
}
