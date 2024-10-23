import {Component, inject, OnInit, signal} from '@angular/core';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {MatListModule} from "@angular/material/list";
import {RouterLink} from "@angular/router";
import {UserBasicInfoComponent} from "../../../../core/components/user-basic-info/user-basic-info.component";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateChannelComponent} from "../../text-chat/dialogs/create-channel/create-channel.component";
import {Channel} from "../../../models/channel";
import {ChannelService} from "../../../services/channel.service";
import {VoiceChannelQuery} from "../../../store/voiceChannel/voice.channel.query";
import {CommunityQuery} from "../../../store/community/community.query";
import {AsyncPipe} from "@angular/common";
import {TextChannelInfoComponent} from "../../text-chat/text-channel-info/text-channel-info.component";
import {toSignal} from "@angular/core/rxjs-interop";

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
        MatIcon,
        AsyncPipe,
        MatIconButton,
        TextChannelInfoComponent
    ],
  templateUrl: './users-list-voice.component.html',
  styleUrl: './users-list-voice.component.scss'
})
export class UsersListVoiceComponent  implements OnInit{
  readonly dialog = inject(MatDialog);

  readonly voiceChannels = toSignal(this.channelQuery.selectAll({
    filterBy: [
      (entity, index) => entity.communityId === this.communityQuery.getActiveId()
    ]
  }));

  constructor(
    private channelQuery: VoiceChannelQuery,
    private channelService: ChannelService,
    private communityQuery: CommunityQuery) {
  }

  ngOnInit() {

  }

  get voiceChannels$(){
    return this.channelQuery.selectAll({
      filterBy: [
        (entity, index) => entity.communityId === this.communityQuery.getActiveId()
      ]
    });
  }

  addChannel(){
    const dialogRef = this.dialog.open(CreateChannelComponent, {width: '60vw'});
  }

  editChannel(channel: Channel){
    this.dialog.open(CreateChannelComponent, {
      width: '60vw',
      data: {
        editing: true,
        channel: channel
      }
    });
  }

  selectChannel(channel: Channel){
    this.channelService.selectVoiceChannel(channel);
  }

}
