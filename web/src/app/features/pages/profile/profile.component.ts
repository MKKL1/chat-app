import { Component } from '@angular/core';
import {GifSearchComponent} from "../../../shared/ui/gif-search/gif-search.component";
import {MatIcon} from "@angular/material/icon";
import {MatFabButton} from "@angular/material/button";

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    GifSearchComponent,
    MatIcon,
    MatFabButton
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {

}
