import { Component } from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {AppbarComponent} from "../../../../core/components/appbar/appbar.component";

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    AppbarComponent,
    RouterOutlet
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent {

}
