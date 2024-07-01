import { Component } from '@angular/core';
import {UsersListComponent} from "../../community/users-list/users-list.component";
import {UserPanelComponent} from "../../voice-chat/user-panel/user-panel.component";

@Component({
  selector: 'app-community',
  standalone: true,
  imports: [
    UsersListComponent,
    UserPanelComponent
  ],
  templateUrl: './community.component.html',
  styleUrl: './community.component.scss'
})
export class CommunityComponent {

}
