import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: 'short',
  standalone: true
})
export class ShorteningPipe implements PipeTransform{
  transform(value: string): string {
    return value.length > 15 ? value.substring(0,15) + "..." : value;
  }
}
