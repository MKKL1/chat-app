import {Component, EventEmitter, Input, OnInit, Output, signal} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatIconButton} from "@angular/material/button";
import {NgClass, NgStyle} from "@angular/common";
import {Message} from "../../../models/message";
import {MessageService} from "../../../services/message.service";
import {MatDialog} from "@angular/material/dialog";
import {EditMessageComponent} from "../dialogs/edit-message/edit-message.component";
import {DeleteMessageComponent} from "../dialogs/delete-message/delete-message.component";
import {EmojiPickerComponent} from "../../../../shared/ui/emoji-picker/emoji-picker.component";
import {AvatarComponent} from "../../../../shared/ui/avatar/avatar.component";
import {UserBasicInfoComponent} from "../../../../core/components/user-basic-info/user-basic-info.component";
import {BottomSheetComponent} from "../../../../shared/ui/bottom-sheet/bottom-sheet.component";
import {MatListModule} from "@angular/material/list";
import {UserService} from "../../../../core/services/user.service";
import {User} from "../../../models/user";

@Component({
  selector: 'app-message',
  standalone: true,
  imports: [
    AvatarComponent,
    MatIcon,
    MatMenuTrigger,
    MatIconButton,
    MatMenu,
    MatMenuItem,
    NgClass,
    NgStyle,
    EmojiPickerComponent,
    UserBasicInfoComponent,
    BottomSheetComponent,
    MatListModule
  ],
  templateUrl: './message.component.html',
  styleUrl: './message.component.scss'
})
export class MessageComponent implements OnInit{
  @Input() message: Message;
  @Input() userId?: string;
  // if user decides to respond to this message by clicking respond
  // it emits event to component with text input, so it will know that
  // message which will be sent is responding to another message
  @Output() respondsTo = new EventEmitter<{
    text: string, id: string
  }>();

  openedOptions = signal<boolean>(false);
  fromClient = signal<boolean>(false);
  showReactionPicker = signal<boolean>(false);

  imagePath: string = '';
  // TODO get user info from members store
  user: User = {
    id: "", username: ""

  };

  constructor(
    private messageService: MessageService,
    private dialog: MatDialog,
    private userService: UserService) {
  }

  ngOnInit() {
    // add another case when message comes from other user
    if(this.message.userId === this.userId){
      this.fromClient.set(true);
      this.imagePath = this.userService.getUser().imageUrl!;
    }
  }

  openOptions(){
    this.openedOptions.update(value => !value);
  }

  respondToMessage(){
    this.openedOptions.set(false);
    this.respondsTo.emit({
      id: this.message.id,
      text: this.message.text
    });
  }

  appendReaction(emoji: string){
    console.log(emoji);
    this.showReactionPicker.set(false);
    this.messageService.addReaction(emoji, this.message.id, this.userId ?? '');
  }

  updateReactionPicker(){
    this.openedOptions.set(false);
    this.showReactionPicker.set(true);
  }

  edit(){
    this.openedOptions.set(false);
    this.dialog.open(EditMessageComponent, {
      width: '60vw',
      data: {message: this.message}
    });
  }

  delete(){
    this.openedOptions.set(false);
    this.dialog.open(DeleteMessageComponent, {
      width: '60vw',
      data: {
        id: this.message.id
      }
    });
  }
}
