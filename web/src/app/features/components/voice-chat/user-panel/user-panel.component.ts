import {Component, ElementRef, HostBinding, Input, ViewChild} from '@angular/core';
import {NgClass, NgStyle} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {ParticipantInfo, VoiceChatService} from "../../../services/voice-chat.service";
import {Member} from "../../../models/member";

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
  @Input() member: Member | null;
  @Input() participant: ParticipantInfo | null;
  @Input() isSpeaking: boolean = false;
}
