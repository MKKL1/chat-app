import { Component } from '@angular/core';
import {MatFormField} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {EmojiPickerComponent} from "../../../shared/ui/emoji-picker/emoji-picker.component";
import {ClickOutsideDirective} from "../../../shared/directives/click-outside.directive";
import {FormsModule} from "@angular/forms";

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
    FormsModule
  ],
  templateUrl: './text-chat.component.html',
  styleUrl: './text-chat.component.scss'
})
export class TextChatComponent {
  message: string = '';

  selectedFile: File | null = null;
  fileName: string | null = null;

  showEmojiPicker: boolean = false;

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

  appendEmojiToInputField(emoji: string){
    this.message += emoji;
  }

  toggleEmojiPicker(){
    this.showEmojiPicker = !this.showEmojiPicker;
  }

  closeEmojiPicker(){
    this.showEmojiPicker = false;
  }

}
