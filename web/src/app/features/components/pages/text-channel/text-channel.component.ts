import {Component, OnInit, ViewChild} from '@angular/core';
import {TextChatsListComponent} from "../../text-chat/text-chats-list/text-chats-list.component";
import {TextChatComponent} from "../../text-chat/text-chat/text-chat.component";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatSidenav, MatSidenavModule} from "@angular/material/sidenav";
import {MatListModule} from "@angular/material/list";
import {BreakpointObserver} from "@angular/cdk/layout";
import {LayoutComponent} from "../../../../core/components/layout/layout.component";
import {ChannelQuery} from "../../../store/channel/channel.query";

@Component({
  selector: 'app-text-channel',
  standalone: true,
  imports: [
    MatIconModule,
    MatButtonModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    LayoutComponent,
    TextChatsListComponent,
    TextChatComponent
  ],
  templateUrl: './text-channel.component.html',
  styleUrl: './text-channel.component.scss'
})
export class TextChannelComponent{
  isChannelSelected: boolean = false;

  constructor(private channelQuery: ChannelQuery) {
    this.channelQuery.isTextChannelSelected$.subscribe(isSelected => {
      this.isChannelSelected = isSelected;
    });
  }
}
