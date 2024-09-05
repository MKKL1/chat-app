import {Component, Inject} from '@angular/core';
import {FileUploadComponent} from "../../../../../shared/ui/file-upload/file-upload.component";
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";

@Component({
  selector: 'app-generate-invitation',
  standalone: true,
    imports: [
        FileUploadComponent,
        MatButton,
        MatDialogActions,
        MatDialogClose,
        MatDialogContent,
        MatDialogTitle,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule
    ],
  templateUrl: './generate-invitation.component.html'
})
export class GenerateInvitationComponent {
  daysForm = new FormGroup({
    days: new FormControl('',[
      Validators.required,
      Validators.min(1),
      Validators.max(365),
      Validators.pattern('^[0-9]*$')
    ])
  });

  constructor(public dialogRef: MatDialogRef<GenerateInvitationComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  generate(){
    this.dialogRef.close({days: this.daysForm.value.days, generated: true});
  }

}
