import { Component } from '@angular/core';
import {TextChatsListComponent} from "../../text-chat/text-chats-list/text-chats-list.component";
import {TextChatComponent} from "../../text-chat/text-chat/text-chat.component";

@Component({
  selector: 'app-text-channel',
  standalone: true,
  imports: [
    TextChatsListComponent,
    TextChatComponent
  ],
  templateUrl: './text-channel.component.html',
  styleUrl: './text-channel.component.scss'
})
export class TextChannelComponent {

}
