import {Component, OnDestroy, OnInit, signal} from '@angular/core';
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
import {MessageInputComponent} from "../message-input/message-input.component";
import {MessageService} from "../../../services/message.service";
import {Observable, Subscription, tap} from "rxjs";
import {AsyncPipe} from "@angular/common";
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";
import {UserService} from "../../../../core/services/user.service";
import {MatButton} from "@angular/material/button";
import {Order} from "@datorama/akita";
import {toSignal} from "@angular/core/rxjs-interop";

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
    AsyncPipe,
    MatButton
  ],
  templateUrl: './text-chat.component.html',
  styleUrl: './text-chat.component.scss'
})

export class TextChatComponent implements OnInit, OnDestroy{
  channel: Channel = {communityId: "", id: "", name: "", type: ChannelType.Text, overwrites: []};

  private channelSubscription: Subscription;

  // rerenders template if new data arrives
  messages = signal<Message[]>([]);
  loadedAllData = signal<boolean>(false);

  messageToRespond: { id: string, text: string } = {id: '', text: ''};

  constructor(
    private messageService: MessageService,
    private messageQuery: MessageQuery,
    private channelQuery: TextChannelQuery,
    protected userService: UserService
  ) {}

  // to show and hide button in proper way i need to observe all channel latest message 😔
  ngOnInit() {
    // listening to changes of channel
    this.channelSubscription = this.channelQuery.selectActive().subscribe(channel => {
      console.log(channel);
      if(!channel){
        return;
      }

      this.channel = channel!;
      this.messageService.getMessages(this.channelQuery.getActiveId()!, this.channel.messagesState!);
      this.loadedAllData.set(false);

      // Instead of waiting for changes from selectAll
      // I explicitly use getAll to handle both changes of active channel and loading
      // data more messages from api
      this.messages.set(
        this.messageQuery.getAll({
            filterBy: entity => entity.channelId === this.channelQuery.getActiveId(),
            sortBy: 'updatedAt',
            sortByOrder: Order.ASC
          })
      );
    });

    // listening to new messages
    this.messageQuery.selectAll({
      filterBy: entity => entity.channelId === this.channelQuery.getActiveId(),
      sortBy: 'updatedAt',
      sortByOrder: Order.ASC
    }).subscribe(messages => {
      this.messages.set(messages);
    });
  }

  loadMoreMessage(){
    const lastMessageId = this.messages()?.at(0)?.id;
    this.messageService.getMessages(this.channelQuery.getActiveId()!, this.channel.messagesState!, lastMessageId);
  }

  setResponse(event: { id: string, text: string }){
    this.messageToRespond = event;
  }

  ngOnDestroy() {
    this.channelSubscription.unsubscribe();
  }

}
