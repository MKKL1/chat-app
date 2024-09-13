import {Component, inject, OnInit, signal} from '@angular/core';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {MatListModule} from "@angular/material/list";
import {disableDebugTools} from "@angular/platform-browser";
import {RouterLink} from "@angular/router";
import {UserBasicInfoComponent} from "../../../../core/components/user-basic-info/user-basic-info.component";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateChannelComponent} from "../../text-chat/dialogs/create-channel/create-channel.component";
import {ChannelStore} from "../../../store/channel/channel.store";
import {ChannelQuery} from "../../../store/channel/channel.query";
import {Channel} from "../../../models/channel";
import {ChannelService} from "../../../services/channel.service";

@Component({
  selector: 'app-users-list-voice',
  standalone: true,
    imports: [
        MatAccordion,
        MatChip,
        MatChipSet,
        MatExpansionPanel,
        MatExpansionPanelHeader,
        MatExpansionPanelTitle,
        UserBasicInfoComponent,
        MatListModule,
        RouterLink,
        MatButton,
        MatIcon
    ],
  templateUrl: './users-list-voice.component.html',
  styleUrl: './users-list-voice.component.scss'
})
export class UsersListVoiceComponent  implements OnInit{
  readonly dialog = inject(MatDialog);
  readonly panelOpenState = signal(false);

  voiceChannels: Channel[] = [];

  constructor(private channelQuery: ChannelQuery, private channelService: ChannelService) {
  }

  ngOnInit() {
    this.channelQuery.voiceChannels$.subscribe(channels => {
      this.voiceChannels = channels;
    });
  }

  addChannel(){
    const dialogRef = this.dialog.open(CreateChannelComponent, {width: '60vw'});
  }

  selectChannel(channel: Channel){
    this.channelService.selectVoiceChannel(channel);
  }

}
