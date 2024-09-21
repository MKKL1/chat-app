import {Component, Input} from '@angular/core';
import {EmojiPickerComponent} from "../../../../shared/ui/emoji-picker/emoji-picker.component";
import {GifSearchComponent} from "../../../../shared/ui/gif-search/gif-search.component";
import {MatFormField} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatTooltip} from "@angular/material/tooltip";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ClickOutsideDirective} from "../../../../shared/directives/click-outside.directive";
import {MessageService} from "../../../services/message.service";
import {UserService} from "../../../../core/services/user.service";

@Component({
  selector: 'app-message-input',
  standalone: true,
  imports: [
    EmojiPickerComponent,
    GifSearchComponent,
    MatFormField,
    MatIcon,
    MatInput,
    MatTooltip,
    ReactiveFormsModule,
    FormsModule,
    ClickOutsideDirective
  ],
  templateUrl: './message-input.component.html',
  styleUrl: './message-input.component.scss'
})
export class MessageInputComponent {
  message: string = '';

  selectedFile: File | null = null;
  fileName: string | null = null;
  selectedGif: string = '';

  showEmojiPicker: boolean = false;
  showGifSearch: boolean = false;

  @Input() messageToRespond?: { id: string, text: string };

  constructor(
    private userService: UserService,
    private messageService: MessageService
  ) {
  }

  sendMessageKeyboard(event: KeyboardEvent){
    if(event.key === 'Enter'){
      this.sendMessage();
    }
  }

  sendMessage(){
    if(this.message.length === 0){
      return;
    }

    // if created message responds to another message
    // add id of this message
    if(this.messageToRespond !== undefined){

    }

    this.messageService.sendMessage(this.message);

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
    this.messageToRespond = event;
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
