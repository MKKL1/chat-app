import {Component, inject} from '@angular/core';
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
import {AsyncPipe, NgClass} from "@angular/common";
import {TextChannelInfoComponent} from "../../text-chat/text-channel-info/text-channel-info.component";
import {toSignal} from "@angular/core/rxjs-interop";
import {VoiceChatService} from "../../../services/voice-chat.service";
import {MessageService} from "primeng/api";
import {MemberQuery} from "../../../store/member/member.query";
import {AvatarGroupModule} from "primeng/avatargroup";
import {AvatarModule} from "primeng/avatar";
import {MatBadge} from "@angular/material/badge";
import {ShorteningPipe} from "../../../../shared/pipes/ShorteningPipe";
import {PermissionService} from "../../../../core/services/permission.service";

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
    TextChannelInfoComponent,
    NgClass,
    AvatarGroupModule,
    AvatarModule,
    MatBadge,
    ShorteningPipe
  ],
  templateUrl: './users-list-voice.component.html',
  styleUrl: './users-list-voice.component.scss'
})
export class UsersListVoiceComponent{
  readonly dialog = inject(MatDialog);

  readonly voiceChannels = toSignal(this.channelQuery.selectAll({
    filterBy: [
      (entity, index) => entity.communityId === this.communityQuery.getActiveId()
    ]
  }));

  selectedChannelId = toSignal(this.channelQuery.selectActiveId());

  members = toSignal(this.memberQuery.selectAll({
    filterBy: entity => entity.communityId === this.communityQuery.getActiveId()
  }));

  permission = toSignal(this.permissionService.permissions$);

  constructor(
    private channelQuery: VoiceChannelQuery,
    private channelService: ChannelService,
    private communityQuery: CommunityQuery,
    private memberQuery: MemberQuery,
    private voiceChat: VoiceChatService,
    private messageService: MessageService,
    private permissionService: PermissionService) {
  }

  addChannel(){
    this.dialog.open(CreateChannelComponent, {width: '60vw'});
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
    this.voiceChat.joinRoom();
    this.messageService.add({ severity: 'info', summary: 'Joined room', detail: 'Connected with ' + channel.name })
  }

  getParticipantImage(id: string){
    return this.members()?.find(m => m.user.id === id)?.user.imageUrl;
  }

}
