import {Component, computed, Input, signal} from '@angular/core';
import {EmojiPickerComponent} from "../../../../shared/ui/emoji-picker/emoji-picker.component";
import {GifSearchComponent} from "../../../../shared/ui/gif-search/gif-search.component";
import {MatFormField} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatTooltip} from "@angular/material/tooltip";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ClickOutsideDirective} from "../../../../shared/directives/click-outside.directive";
import {MessageService} from "../../../services/message.service";
import {CreateMessageDto} from "../../../models/create.message.dto";
import {previewImage} from "../../../../shared/utils/image-preview";

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
  text = signal<string>('');

  selectedFile = signal<File | null>(null);
  fileName = computed(() => this.selectedFile()?.name);

  imagePreview = signal<string>('');
  selectedGif = signal<string>('');

  showEmojiPicker = signal<boolean>(false);
  showGifSearch = signal<boolean>(false);

  @Input() messageToRespond?: { id: string, text: string };

  constructor(private messageService: MessageService) {
  }

  // wrapper for handling key input
  sendMessageKeyboard(event: KeyboardEvent){
    if(event.key === 'Enter'){
      this.sendMessage();
    }
  }

  sendMessage(){
    // message can't be empty
    if(this.text().length === 0){
      return;
    }

    const message: CreateMessageDto = {text: this.text()};

    // if created message responds to another message
    // add id of this message
    if(this.messageToRespond !== undefined){
      message.respondsToMessage = this.messageToRespond.id;
    }

    // add gif link to message
    if(this.selectedGif() !== ''){
      message.gifLink = this.selectedGif();
    }

    this.messageService.sendMessage(message, this.selectedFile());

    this.text.set('');
    this.selectedGif.set('');
    this.messageToRespond = undefined;
  }

  onFileSelected(event: Event){
    // handle attaching file
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile.set(input.files[0]);
      console.log("Changing preview");
      previewImage(input.files[0]).then(image => {
        this.imagePreview.set(image);
      });
      console.log(this.imagePreview());
    }
  }

  triggerFileInput(): void{
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput.click();
  }

  resetFile(){
    this.imagePreview.set('');
    this.selectedFile.set(null);
  }

  setResponse(event: { id: string, text: string }){
    this.messageToRespond = event;
  }

  appendEmojiToInputField(emoji: string){
    this.text.update(text => text += emoji);
  }

  toggleEmojiPicker(){
    this.showEmojiPicker.update(show => !show);
  }

  closeEmojiPicker(){
    this.showEmojiPicker.set(false);
  }

  selectGif(gifUrl: string){
    console.log(gifUrl);
    this.selectedGif.set(gifUrl);
    this.showGifSearch.set(false);
  }

  resetGif(){
    this.selectedGif.set('');
  }

  toggleGifSearch(){
    this.showGifSearch.update(show => !show);
  }

  closeGifSearch(){
    this.showGifSearch.set(false);
  }

}
