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
import {FileUploadComponent} from "../../../../../shared/ui/file-upload/file-upload.component";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {CommunityService} from "../../../../services/community.service";


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
    name: new FormControl('', Validators.required)
  });

  file: File | undefined;

  constructor(private communityService: CommunityService,
    public dialogRef: MatDialogRef<CreateCommunityComponent>) {
  }

  setFile(file: File){
    this.file = file;
  }

  closeDialog(): void {
    if(typeof this.communityForm.value.name === 'string'){
      this.communityService.createCommunity({name: this.communityForm.value.name});
    }
    this.dialogRef.close();
  }
}
