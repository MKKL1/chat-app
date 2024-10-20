import {Component, signal} from '@angular/core';
import {TextChatsListComponent} from "../../text-chat/text-chats-list/text-chats-list.component";
import {TextChatComponent} from "../../text-chat/text-chat/text-chat.component";
import {LayoutComponent} from "../../../../core/components/layout/layout.component";
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";

@Component({
  selector: 'app-text-channel',
  standalone: true,
  imports: [
    LayoutComponent,
    TextChatsListComponent,
    TextChatComponent
  ],
  templateUrl: './text-channel.component.html',
  styleUrl: './text-channel.component.scss'
})
export class TextChannelComponent{
  isChannelSelected = signal<boolean>(false);

  constructor(private channelQuery: TextChannelQuery) {
    this.channelQuery.selectActiveId().subscribe(id => {
      if(id){
        this.isChannelSelected.set(true);
      }
    });
  }
}
