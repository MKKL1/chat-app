import {Component, EventEmitter, Output, signal} from '@angular/core';
import {MatCard, MatCardContent} from "@angular/material/card";
import {FormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {HttpClient, HttpParams} from "@angular/common/http";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {environment} from "../../../../environment";

@Component({
  selector: 'app-gif-search',
  standalone: true,
  imports: [
    MatCard,
    MatCardContent,
    FormsModule,
    MatInputModule,
    MatIcon,
    MatProgressSpinner
  ],
  templateUrl: './gif-search.component.html',
  styleUrl: './gif-search.component.scss'
})
export class GifSearchComponent {
  private apiKey: string = environment.giphyKey;
  private serviceUrl: string = 'https://api.giphy.com/v1/gifs';
  private limit = '15'

  tag: string = '';
  public gifs = signal<any[]>([]);
  @Output() chosenGif = new EventEmitter<string>();

  loading = signal<boolean>(false);

  constructor(private http: HttpClient) {}

  // Currently chosen gifs should only live while this component exists,
  // so I don't use service
  async search(){
    this.loading.set(true);

    const params = new HttpParams()
      .set('api_key', this.apiKey)
      .set('limit', this.limit)
      .set('q', this.tag);

    this.http
      .get<any>(`${this.serviceUrl}/search`, { params })
      .subscribe((res) => {
        this.gifs.set(res.data);
        this.loading.set(false);
      });
  }

  chooseGif(gif: any){
    this.chosenGif.emit(gif.images.downsized_medium.url);
  }

}
