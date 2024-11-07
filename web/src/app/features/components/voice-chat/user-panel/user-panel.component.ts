import {Component, HostBinding} from '@angular/core';
import {NgClass, NgStyle} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {VoiceChatService} from "../../../services/voice-chat.service";

@Component({
  selector: 'app-user-panel',
  standalone: true,
  imports: [
    NgClass,
    NgStyle,
    MatIcon
  ],
  templateUrl: './user-panel.component.html',
  styleUrl: './user-panel.component.scss'
})
export class UserPanelComponent {
  @HostBinding('class.speaking') speaking: boolean = true;
  muted: boolean = false;
  silent: boolean = false;
  image: string | null = null;
  username: string = "Username";

  constructor(private voiceChat: VoiceChatService) {
    // setting random value just to see difference in ui
    this.speaking = Math.random() < 0.5;
  }

  toggleMuted(){
    this.muted = !this.muted;
  }

  toggleSilent(){
    this.speaking = !this.silent;
  }

}
