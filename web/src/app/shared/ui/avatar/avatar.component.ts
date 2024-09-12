import { Component } from '@angular/core';
import {MatBadge} from "@angular/material/badge";

@Component({
  selector: 'app-avatar',
  standalone: true,
  imports: [
    MatBadge
  ],
  templateUrl: './avatar.component.html',
  styleUrl: './avatar.component.scss'
})
export class AvatarComponent {
  image: string | null = null;
  // TODO add status for badge color
  status: string = "";
}
