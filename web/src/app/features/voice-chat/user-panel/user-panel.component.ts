import { Component } from '@angular/core';

@Component({
  selector: 'app-user-panel',
  standalone: true,
  imports: [],
  templateUrl: './user-panel.component.html',
  styleUrl: './user-panel.component.scss'
})
export class UserPanelComponent {
  speaking: boolean = false;
  muted: boolean = false;
  image: string | null = null;
  username: string = "Username";

}
