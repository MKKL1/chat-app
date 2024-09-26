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
import {Channel, ChannelType} from "../../../models/channel";
import {Message} from "../../../models/message";
import {MessageQuery} from "../../../store/message/message.query";
import {UserService} from "../../../../core/services/user.service";
import {MessageInputComponent} from "../message-input/message-input.component";
import {RsocketService} from "../../../../core/services/rsocket.service";
import {MessageService} from "../../../services/message.service";
import {Observable, Subscription, tap} from "rxjs";
import {MessageStore} from "../../../store/message/message.store";
import {AsyncPipe} from "@angular/common";
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";
import {CommunityQuery} from "../../../store/community/community.query";

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
    MessageInputComponent,
    AsyncPipe
  ],
  templateUrl: './text-chat.component.html',
  styleUrl: './text-chat.component.scss'
})

// listen to channel changes if so -> change messages to messages from new channel -> first check if there are any messages -> if no load from api

// for now i have issue with listening to rabbit -> connection should open and close automatically after changing community

export class TextChatComponent implements OnInit{
  channel: Channel = {communityId: "", id: "", name: "", type: ChannelType.Text};

  private messageSubscription: Subscription | undefined;

  messages$!: Observable<Message[]>;

  messageToRespond: { id: string, text: string } = {id: '', text: ''};

  constructor(
    private rsocketService: RsocketService,
    protected userService: UserService,
    private messageService: MessageService,
    private messageQuery: MessageQuery,
    private messageStore: MessageStore,
    private channelQuery: TextChannelQuery,
    private communityQuery: CommunityQuery
  ) {}

  ngOnInit() {
    // listening to changes of channel
    this.channelQuery.selectActive().subscribe(channel => {
      this.channel = channel!;
      this.messageService.getMessages(channel?.id!);
    });

    this.messages$ = this.messageQuery.selectAll({
      filterBy: entity => entity.channelId === this.channelQuery.getActiveId()
    }).pipe(tap(data=> console.log("new message!")));

    // this method don't care on which channel user is
    // don't just add everything here
    // I have to change communityid with time
    //maybe move subscription to other place
    // this.rsocketService.requestStream<Message>(`/community/${this.channel.communityId}/messages`).subscribe((message: Message) => {
    //   this.messageStore.add(message);
    // });
  }

  setResponse(event: { id: string, text: string }){
    this.messageToRespond = event;
  }

}
