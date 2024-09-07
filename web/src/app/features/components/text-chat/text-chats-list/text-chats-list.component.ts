import {Component, inject} from '@angular/core';
import {MatListModule} from "@angular/material/list";
import {TextChannelInfoComponent} from "../text-channel-info/text-channel-info.component";
import {MatButton} from "@angular/material/button";
import {MatDialog} from "@angular/material/dialog";
import {CreateCommunityComponent} from "../../community/dialogs/create-community/create-community.component";
import {CreateChannelComponent} from "../dialogs/create-channel/create-channel.component";

@Component({
  selector: 'app-text-chats-list',
  standalone: true,
  imports: [
    MatListModule,
    TextChannelInfoComponent,
    MatButton
  ],
  templateUrl: './text-chats-list.component.html',
  styleUrl: './text-chats-list.component.scss'
})
export class TextChatsListComponent {
    readonly dialog = inject(MatDialog);

    // fetch from api
    textChannels: any[] = [{},{},{},{},{}];

    addChannel(){
      const dialogRef = this.dialog.open(CreateChannelComponent, {width: '60vw'});
    }
}
