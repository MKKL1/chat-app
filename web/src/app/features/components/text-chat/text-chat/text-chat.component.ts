import {AfterViewInit, Component, ElementRef, HostListener, OnDestroy, OnInit, signal, ViewChild} from '@angular/core';
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
import {Channel, ChannelMessagesState, ChannelType} from "../../../models/channel";
import {Message} from "../../../models/message";
import {MessageQuery} from "../../../store/message/message.query";
import {MessageInputComponent} from "../message-input/message-input.component";
import {MessageService} from "../../../services/message.service";
import {Subscription} from "rxjs";
import {AsyncPipe} from "@angular/common";
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";
import {UserService} from "../../../../core/services/user.service";
import {MatButton} from "@angular/material/button";
import {Order} from "@datorama/akita";
import {SkeletonModule} from "primeng/skeleton";

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
    MatButton,
    SkeletonModule
  ],
  templateUrl: './text-chat.component.html',
  styleUrl: './text-chat.component.scss'
})

export class TextChatComponent implements OnInit, OnDestroy, AfterViewInit{
  channel: Channel = {communityId: "", id: "", name: "", type: ChannelType.Text, overwrites: []};

  private channelSubscription: Subscription;

  // rerenders template if new data arrives
  messages = signal<Message[]>([]);
  loadedAllData = signal<boolean>(false);

  messageToRespond: { id: string, text: string } = {id: '', text: ''};

  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  @HostListener('scroll', ['$event'])
  onScrollEvent() {
    this.onScroll();
  }

  constructor(
    private messageService: MessageService,
    private messageQuery: MessageQuery,
    private channelQuery: TextChannelQuery,
    protected userService: UserService
  ) {}

  ngOnInit() {
    // listening to changes of channel
    this.channelSubscription = this.channelQuery.selectActive().subscribe(channel => {
      if(!channel){
        return;
      }

      this.channel = channel!;
      this.messageService.getMessages(this.channelQuery.getActiveId()!, this.channel.messagesState!);
      if(this.channel.messagesState === ChannelMessagesState.FullyFetched){
        this.loadedAllData.set(true);
      } else {
        this.loadedAllData.set(false);
      }

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
      // this callback can be invoked both when new message arrives and when user loads old messages
      // if this is new messages it should scroll down to it
      // in case of old messages scroll shouldn't be performed
      const lastMessage = this.messages().at(-1);
      const lastMessageUpdated = messages.at(-1);

      this.messages.set(messages);

      // this means that new messages arrived
      // if old messages were loaded last messages from both collections would have same id
      // checking isScrolledToBottom because user who is reading older messages
      // shouldn't be moved to new ones every time when new message arrives
      if(lastMessage?.id !== lastMessageUpdated?.id && this.isScrolledToBottom()){
        // maybe check if user is already at the bottom
        this.scrollToBottom();
      }
    });
  }

  ngAfterViewInit() {
    this.scrollToBottom();
  }

  private scrollToBottom(){
    setTimeout(() => {
    try{
      this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
    } catch(err){
      console.error(err);
    }}, 1);
  }

  private isScrolledToTop(): boolean {
    return this.scrollContainer.nativeElement.scrollTop === 0;
  }

  private isScrolledToBottom(): boolean {
    const scrollContainer = this.scrollContainer.nativeElement;
    return scrollContainer.scrollTop + scrollContainer.clientHeight >= scrollContainer.scrollHeight;
  }

  onScroll() {
    // if user scrolled to top, new messages should be loaded
    if (this.isScrolledToTop() && !this.loadedAllData()) {
      this.loadMoreMessage();
      const activeChannel = this.channelQuery.getActive();
      // setting loadedAllData to true, so fetching function won't be invoked again
      if(activeChannel?.messagesState === ChannelMessagesState.FullyFetched){
        this.loadedAllData.set(true);
      }
    }
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
