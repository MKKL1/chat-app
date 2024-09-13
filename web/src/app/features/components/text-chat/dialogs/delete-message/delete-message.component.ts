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
import {ReactiveFormsModule} from "@angular/forms";
import {MessageService} from "../../../../services/message.service";

@Component({
  selector: 'app-delete-message',
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
  templateUrl: './delete-message.component.html',
})
export class DeleteMessageComponent {

  constructor(private messageService: MessageService,
      @Inject(MAT_DIALOG_DATA) public data: {id: string},
      public dialogRef: MatDialogRef<DeleteMessageComponent>) {
  }

  onDelete(){
    this.messageService.deleteMessage(this.data.id);
    this.dialogRef.close();
  }

}
