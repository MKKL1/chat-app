import {Component, OnInit} from '@angular/core';
import {MatFormField} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {EmojiPickerComponent} from "../../../../shared/ui/emoji-picker/emoji-picker.component";
import {ClickOutsideDirective} from "../../../../shared/directives/click-outside.directive";
import {FormsModule} from "@angular/forms";
import {MessageComponent} from "../message/message.component";
import {GifSearchComponent} from "../../../../shared/ui/gif-search/gif-search.component";
import {FadeInOutScrollDirective} from "../../../../shared/directives/fade-in-out-scroll.directive";
import {ChannelQuery} from "../../../store/channel/channel.query";
import {Channel, ChannelType} from "../../../models/channel";
import {MessageService} from "../../../services/message.service";
import {Message} from "../../../models/message";
import {MessageQuery} from "../../../store/message/message.query";
import {CreateMessageDto} from "../../../models/create.message.dto";
import {UserService} from "../../../services/user.service";
import {MessageInputComponent} from "../message-input/message-input.component";

// maybe split it into component with messages to read, and component with input to create new message

@Component({
  selector: 'app-text-chat',
  standalone: true,
  imports: [
    MatFormField,
    MatInput,
    MatIcon,
    MatTooltip,
    EmojiPickerComponent,
    ClickOutsideDirective,
    FormsModule,
    MessageComponent,
    GifSearchComponent,
    FadeInOutScrollDirective,
    MessageInputComponent
  ],
  templateUrl: './text-chat.component.html',
  styleUrl: './text-chat.component.scss'
})
export class TextChatComponent implements OnInit{
  channel: Channel = {communityId: "", id: "", name: "", type: ChannelType.Text};
  messages: Message[] = [];

  messageToRespond: { id: string, text: string } = {id: '', text: ''};

  constructor(
    protected userService: UserService,
    private channelQuery: ChannelQuery,
    private messageQuery: MessageQuery
  ) {}

  ngOnInit() {
    this.channelQuery.textChannel$.subscribe(channel => {
      console.log("Text channel changed");
      console.log(channel);
      this.channel = channel;
      this.messages = [];
    });
    this.messageQuery.messages$(this.channel.id ?? '').subscribe(messages => {
      this.messages = messages;
    });
  }

  setResponse(event: { id: string, text: string }){
    this.messageToRespond = event;
  }

}
