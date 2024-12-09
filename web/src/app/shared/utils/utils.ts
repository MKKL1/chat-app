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

export function formatDate(date: Date){
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();

  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');

  const formattedDate = `${day}.${month}.${year} ${hours}:${minutes}`;
  return formattedDate;
}

export function formatRoleName(name: string): string{
  const withSpaces = name.replace(/([A-Z])/g, ' $1');
  return withSpaces.charAt(0).toUpperCase() + withSpaces.slice(1);
}

export function getRandomColorFromName(name: string): string{
  const sanitizedName = name.toLowerCase().replace(/\s+/g, '');
  let hash = 0;
  for (let i = 0; i < sanitizedName.length; i++) {
    hash = sanitizedName.charCodeAt(i) + ((hash << 5) - hash);
  }

  return `#${((hash >> 16) & 0xFFFFFF).toString(16).padStart(6, '0')}`;
}
