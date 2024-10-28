export function setBit(num: bigint, n: bigint){
  return num | (1n << n);
}
