import {Component, Inject} from '@angular/core';
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
        ReactiveFormsModule
    ],
  templateUrl: './edit-message.component.html',
})
export class EditMessageComponent {
  // we probably don't want to play with changing image and gif
  // only option for them is to simply delete them with message
  messageForm = new FormGroup({
    text: new FormControl('', Validators.required)
  });
  message?: Message;

  constructor(private messageService: MessageService,
      @Inject(MAT_DIALOG_DATA) public data: {message: Message},
      public dialogRef: MatDialogRef<EditMessageComponent>) {
    console.log(data.message);
    this.messageForm.setValue({text: data.message.text});
    this.message = data.message;
  }

  submitMessage(){
    if(this.message && this.messageForm.value.text){
      this.message.text = this.messageForm.value.text;
      this.messageService.editMessage(this.message);
      this.dialogRef.close();
    }
  }
}
