import {Component, Input} from '@angular/core';
import {MatListModule} from "@angular/material/list";
import {DatePipe} from "@angular/common";
import {ShorteningPipe} from "../../../../shared/pipes/ShorteningPipe";
import {Channel} from "../../../models/channel";
import {MatIconButton, MatMiniFabButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-text-channel-info',
  standalone: true,
  imports: [
    MatListModule,
    DatePipe,
    ShorteningPipe,
    MatMiniFabButton,
    MatIcon,
    MatIconButton
  ],
  templateUrl: './text-channel-info.component.html',
  styleUrl: './text-channel-info.component.scss'
})
export class TextChannelInfoComponent {
  @Input() channel: Channel | undefined;

  // TODO add variables for data which will be displayed
  channelName: string = "Channel 1";
  username: string = "User 1";
  // Testing pipe which will transform it to shorter form
  message: string = "Example messageExample messageExample messageExample messageExample messageExample message";
  date: Date = new Date();
  // Change colors

  // TODO load chats for channel
  changeTextChannel(){
    // implement
  }
}
