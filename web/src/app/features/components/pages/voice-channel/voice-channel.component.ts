import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {UserPanelComponent} from "../../voice-chat/user-panel/user-panel.component";
import {UsersListVoiceComponent} from "../../voice-chat/users-list-voice/users-list-voice.component";
import {MatIcon} from "@angular/material/icon";
import {LayoutComponent} from "../../../../core/components/layout/layout.component";

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
export class VoiceChannelComponent{
  channelName: string = "Community 1";
  channelUsers: any[] = [

  ];

  clientMuted: boolean = false;
  clientSilent: boolean = false;

  // error why using OnInit for some reason
  ngOnInit() {
    // this.subscription = this.screenSizeService.isMobileView$.subscribe(isMobile => {
    //   this.isMobile = isMobile;
    // });
  }

  toggleClientMuted(){
    this.clientMuted = !this.clientMuted;
  }

  toggleClientSilent(){
    this.clientSilent = !this.clientSilent;
  }
}
