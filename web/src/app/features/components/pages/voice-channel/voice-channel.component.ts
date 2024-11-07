import {Component} from '@angular/core';
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {UserPanelComponent} from "../../voice-chat/user-panel/user-panel.component";
import {UsersListVoiceComponent} from "../../voice-chat/users-list-voice/users-list-voice.component";
import {MatIcon} from "@angular/material/icon";
import {LayoutComponent} from "../../../../core/components/layout/layout.component";
import {VoiceChatService} from "../../../services/voice-chat.service";

@Component({
  selector: 'app-voice-channel',
  standalone: true,
  imports: [
    MatGridList,
    MatGridTile,
    UserPanelComponent,
    UsersListVoiceComponent,
    MatIcon,
    LayoutComponent
  ],
  templateUrl: './voice-channel.component.html',
  styleUrl: './voice-channel.component.scss'
})

// I am not rewriting anything in voice channel because it will probably
// change in future anyway

export class VoiceChannelComponent{
  channelName: string = "Community 1";
  channelUsers: any[] = [

  ];

  clientMuted: boolean = false;
  clientSilent: boolean = false;

  constructor(private voiceChat: VoiceChatService) {
  }

  ngOnInit() {
  }

  toggleClientMuted(){
    this.clientMuted = !this.clientMuted;
  }

  toggleClientSilent(){
    this.clientSilent = !this.clientSilent;
  }

  disconnect(){
    this.voiceChat.leaveRoom();
  }
}
