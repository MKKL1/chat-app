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
    FadeInOutScrollDirective
  ],
  templateUrl: './text-chat.component.html',
  styleUrl: './text-chat.component.scss'
})
export class TextChatComponent implements OnInit{
  channel: Channel = {communityId: "", id: "", name: "", type: ChannelType.Text};
  messages: Message[] = [];

  message: string = '';

  responding: boolean = false;
  messageToRespondId?: { id: string, text: string };

  selectedFile: File | null = null;
  fileName: string | null = null;
  selectedGif: string = '';

  showEmojiPicker: boolean = false;
  showGifSearch: boolean = false;

  constructor(
    protected userService: UserService,
    private messageService: MessageService,
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

  sendMessage(){
    if(this.message.length === 0){
      return;
    }

    const messageDTO: CreateMessageDto = {
      channelId: this.channel.id ?? '',
      userId: this.userService.getUser().id,
      text: this.message
    };

    // if created message responds to another message
    // add id od this message
    if(this.messageToRespondId !== undefined){

    }

    this.messageService.sendMessage(messageDTO, this.selectedFile);

    this.message = '';
  }

  onFileSelected(event: Event){
    // handle attaching file
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.fileName = this.selectedFile.name;
    }
  }

  triggerFileInput(): void{
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput.click();
  }

  setResponse(event: { id: string, text: string }){
    this.responding = true;
    this.messageToRespondId = event;
  }

  appendEmojiToInputField(emoji: string){
    this.message += emoji;
  }

  toggleEmojiPicker(){
    this.showEmojiPicker = !this.showEmojiPicker;
  }

  closeEmojiPicker(){
    this.showEmojiPicker = false;
  }

  selectGif(gifUrl: string){
    console.log(gifUrl);
    this.selectedGif = gifUrl;
  }

  toggleGifSearch(){
    this.showGifSearch = !this.showGifSearch;
  }

  closeGifSearch(){
    this.showGifSearch = false;
  }

}
