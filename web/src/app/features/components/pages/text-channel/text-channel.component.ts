import {Component, OnDestroy, signal} from '@angular/core';
import {TextChatsListComponent} from "../../text-chat/text-chats-list/text-chats-list.component";
import {TextChatComponent} from "../../text-chat/text-chat/text-chat.component";
import {LayoutComponent} from "../../../../core/components/layout/layout.component";
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";
import {Subscription} from "rxjs";

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
export class TextChannelComponent implements OnDestroy{
  isChannelSelected = signal<boolean>(false);
  private channelSubscription: Subscription;

  constructor(private channelQuery: TextChannelQuery) {
    this.channelSubscription = this.channelQuery.selectActiveId().subscribe(id => {
      if(id){
        this.isChannelSelected.set(true);
      }
    });
  }

  ngOnDestroy() {
    this.channelSubscription.unsubscribe();
  }
}
