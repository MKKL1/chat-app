import {Component, inject, OnInit} from '@angular/core';
import {MatListModule} from "@angular/material/list";
import {TextChannelInfoComponent} from "../text-channel-info/text-channel-info.component";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialog} from "@angular/material/dialog";
import {CreateCommunityComponent} from "../../community/dialogs/create-community/create-community.component";
import {CreateChannelComponent} from "../dialogs/create-channel/create-channel.component";
import {MatIcon} from "@angular/material/icon";
import {ChannelQuery} from "../../../store/channel/channel.query";
import {Channel} from "../../../models/channel";
import {ChannelService} from "../../../services/channel.service";
import {IsOwnerDirective} from "../../../../shared/directives/is-owner.directive";

@Component({
  selector: 'app-text-chats-list',
  standalone: true,
  imports: [
    MatListModule,
    TextChannelInfoComponent,
    MatButton,
    MatIcon,
    MatIconButton,
    IsOwnerDirective
  ],
  templateUrl: './text-chats-list.component.html',
  styleUrl: './text-chats-list.component.scss'
})
export class TextChatsListComponent implements OnInit{
    readonly dialog = inject(MatDialog);

    textChannels: Channel[] = [];

    constructor(private channelQuery: ChannelQuery, private channelService: ChannelService) {
    }

    ngOnInit() {
      this.channelQuery.textChannels$.subscribe(channels => {
        this.textChannels = channels;
      })
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
