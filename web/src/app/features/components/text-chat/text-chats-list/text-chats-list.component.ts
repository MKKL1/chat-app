import { Component } from '@angular/core';
import {MatListModule} from "@angular/material/list";
import {TextChannelInfoComponent} from "../text-channel-info/text-channel-info.component";

@Component({
  selector: 'app-text-chats-list',
  standalone: true,
  imports: [
    MatListModule,
    TextChannelInfoComponent
  ],
  templateUrl: './text-chats-list.component.html',
  styleUrl: './text-chats-list.component.scss'
})
export class TextChatsListComponent {
    // fetch from api
    textChannels: any[] = [{},{},{},{},{}];
}
