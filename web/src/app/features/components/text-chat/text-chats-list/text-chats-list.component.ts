import {Component, inject, OnDestroy, OnInit, signal} from '@angular/core';
import {MatListModule} from "@angular/material/list";
import {TextChannelInfoComponent} from "../text-channel-info/text-channel-info.component";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialog} from "@angular/material/dialog";
import {CreateChannelComponent} from "../dialogs/create-channel/create-channel.component";
import {MatIcon} from "@angular/material/icon";
import {Channel} from "../../../models/channel";
import {ChannelService} from "../../../services/channel.service";
import {IsOwnerDirective} from "../../../../shared/directives/is-owner.directive";
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";
import {CommunityQuery} from "../../../store/community/community.query";
import {AsyncPipe, NgClass} from "@angular/common";
import {toSignal} from "@angular/core/rxjs-interop";
import {Subscription} from "rxjs";
import {EventService} from "../../../../core/events/event.service";
import {UserService} from "../../../../core/services/user.service";
import {ID} from "@datorama/akita";
import {PermissionService} from "../../../../core/services/permission.service";
import {Permission} from "../../../models/permission";
import {ShorteningPipe} from "../../../../shared/pipes/ShorteningPipe";

@Component({
  selector: 'app-text-chats-list',
  standalone: true,
    imports: [
        MatListModule,
        TextChannelInfoComponent,
        MatButton,
        MatIcon,
        MatIconButton,
        IsOwnerDirective,
        AsyncPipe,
        NgClass,
        ShorteningPipe
    ],
  templateUrl: './text-chats-list.component.html',
  styleUrl: './text-chats-list.component.scss'
})
export class TextChatsListComponent{
    readonly dialog = inject(MatDialog);

    readonly textChannels = toSignal(this.channelQuery.selectAll({
       filterBy: [
         (entity, index) => entity.communityId === this.communityQuery.getActiveId()
       ]
    }));

    selectedChannelId = toSignal(this.channelQuery.selectActiveId());
    permission = toSignal(this.permissionService.permissions$);

    constructor(
      private channelQuery: TextChannelQuery,
      private channelService: ChannelService,
      private communityQuery: CommunityQuery,
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
      this.channelService.selectTextChannel(channel);
    }
}
