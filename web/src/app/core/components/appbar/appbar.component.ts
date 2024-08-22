import { Component } from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {RouterLink, RouterLinkActive} from "@angular/router";

@Component({
  selector: 'app-appbar',
  standalone: true,
  imports: [
    MatIcon,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './appbar.component.html',
  styleUrl: './appbar.component.scss',
})
export class AppbarComponent {

}
