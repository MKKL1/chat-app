import {ChangeDetectionStrategy, Component, Inject, inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent, MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton, MatFabButton, MatMiniFabButton} from "@angular/material/button";
import {MatFormField} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {FileUploadComponent} from "../../../shared/ui/file-upload/file-upload.component";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {CreateCommunityData} from "../../../shared/interfaces/CreateCommunityData";

@Component({
  selector: 'app-create-community',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    MatInputModule,
    MatIcon,
    MatMiniFabButton,
    MatFabButton,
    FileUploadComponent,
    ReactiveFormsModule
  ],
  templateUrl: './create-community.component.html',
  styleUrl: './create-community.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateCommunityComponent {
  communityForm = new FormGroup({
    name: new FormControl('', Validators.required),
    entryCode: new FormControl('', Validators.required)
  })

  file: File | undefined;

  constructor(public dialogRef: MatDialogRef<CreateCommunityComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  setFile(file: File){
    this.file = file;
  }

  closeDialog(): void {
    this.dialogRef.close({
      file: this.file,
      form: this.communityForm.value
    });
  }
}
