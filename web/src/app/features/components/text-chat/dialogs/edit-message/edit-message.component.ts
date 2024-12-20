import {Component, Inject, signal, WritableSignal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent, MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatOption} from "@angular/material/core";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatSelect} from "@angular/material/select";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MessageService} from "../../../../services/message.service";
import {Message} from "../../../../models/message";
import {EditPermissionsComponent} from "../edit-permissions/edit-permissions.component";

@Component({
  selector: 'app-edit-message',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatInput,
    MatLabel,
    MatOption,
    MatProgressSpinner,
    MatSelect,
    ReactiveFormsModule,
    EditPermissionsComponent
  ],
  templateUrl: './edit-message.component.html',
})
export class EditMessageComponent {
  // we probably don't want to play with changing image and gif
  // only option for them is to simply delete them with message
  messageForm = new FormGroup({
    text: new FormControl('', Validators.required)
  });

  message: WritableSignal<Message>;

  constructor(private messageService: MessageService,
      @Inject(MAT_DIALOG_DATA) public data: {message: Message},
      public dialogRef: MatDialogRef<EditMessageComponent>) {
    this.messageForm.setValue({text: data.message.text});
    this.message = signal<Message>(data.message);
  }

  submitMessage(){
    if(this.messageForm.value.text){
      this.message.update(currentMessage => {
        return {
          ...currentMessage,
          text: this.messageForm.value.text!
        };
      });
      this.messageService.editMessage(this.message().id, this.message().text);
      this.dialogRef.close();
    }
  }
}
