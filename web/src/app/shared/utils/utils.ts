import {environment} from "../../../environment";

export function previewImage(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();

    reader.onload = () => {
      resolve(reader.result as string);
    };

    reader.onerror = (error) => {
      reject(error);
    };

    reader.readAsDataURL(file);
  });
}

export function filePathMapping(path: string): string{
  return environment.api + "file/" + path;
}

export function snowflakeDate(snowflake: string){
  const timestamp = (BigInt(snowflake) >> 22n) + BigInt(environment.snowflakeEpoch);
  return new Date(Number(timestamp));
}
