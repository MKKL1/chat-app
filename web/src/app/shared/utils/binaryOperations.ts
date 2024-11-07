export function setBit(num: bigint, n: bigint){
  return num | (1n << n);
}

export function overwriteBasePermission(overwrite: bigint, base: bigint){
  let result = 0n;
  let bitPosition = 0n;

  while (overwrite > 0n || base > 0n) {
    // getting the least significant bit from both numbers
    const bitA = overwrite & 1n;
    const bitB = base & 1n;

    // console.log(`overwrite ${bitA}`);
    // console.log(`base ${bitB}`);

    let resultBit;
    // if both bits are 1 returning 1
    if (bitA === 1n && bitB === 1n) {
      resultBit = 1n;
    // if bit from accumulated roles is 0, it overwrites base bit
    } else if (bitA === 1n && bitB === 0n) {
      resultBit = 1n;
    // if both bits are 0, or bit from accumulated roles is 0, returns 0
    } else {
      resultBit = 0n;
    }

    // adding result bit in certain place
    result |= (resultBit << bitPosition);
    //console.log(result.toString(2));

    // moving right, to analyse next bit
    overwrite >>= 1n;
    base >>= 1n;
    bitPosition++;
  }

  return result;
}
