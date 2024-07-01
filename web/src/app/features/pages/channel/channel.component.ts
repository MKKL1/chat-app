import { Component } from '@angular/core';
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {UserPanelComponent} from "../../voice-chat/user-panel/user-panel.component";
import {UsersListVoiceComponent} from "../../voice-chat/users-list-voice/users-list-voice.component";

@Component({
  selector: 'app-channel',
  standalone: true,
  imports: [
    MatGridList,
    MatGridTile,
    UserPanelComponent,
    UsersListVoiceComponent
  ],
  templateUrl: './channel.component.html',
  styleUrl: './channel.component.scss'
})
export class ChannelComponent {

}
