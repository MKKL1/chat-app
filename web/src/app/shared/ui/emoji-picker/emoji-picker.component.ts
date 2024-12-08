import {AfterViewInit, Component, ElementRef, EventEmitter, Output} from '@angular/core';
import {Picker} from "emoji-mart";
import {ClickOutsideDirective} from "../../directives/click-outside.directive";

@Component({
  selector: 'app-emoji-picker',
  standalone: true,
  imports: [
    ClickOutsideDirective
  ],
  template: '<div id="emoji-picker-container" clickOutside (clickOutside)="hideEmojiPicker()"></div>'
})
export class EmojiPickerComponent implements AfterViewInit{
  @Output() clickOutside = new EventEmitter<void>();
  @Output() emojiSelect = new EventEmitter<string>();

  constructor(private elRef: ElementRef) {}

  // initializing emoji picker with certain options
  ngAfterViewInit(): void {
    const pickerOptions = {
      theme: 'dark',
      set: 'google',
      previewPosition: 'none',
      // emitting emoji as output
      onEmojiSelect: (emoji: any) => {
        this.emojiSelect.emit(emoji.native);
      },
    }

    const picker = new Picker(pickerOptions);
    // adding picker to DOM
    const container = this.elRef.nativeElement.querySelector('#emoji-picker-container');
    container.appendChild(picker);
  }

  hideEmojiPicker() {
    this.clickOutside.emit();
  }
}
