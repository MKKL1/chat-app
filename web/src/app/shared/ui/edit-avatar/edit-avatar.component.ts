import {Component, inject, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {FileUploadComponent} from "../file-upload/file-upload.component";
import {UserService} from "../../../core/services/user.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {previewImage} from "../../utils/image-preview";

@Component({
  selector: 'app-edit-avatar',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    FileUploadComponent
  ],
  templateUrl: './edit-avatar.component.html',
  styleUrl: './edit-avatar.component.scss'
})
export class EditAvatarComponent {
  fileToUpload?: File;
  imageUrl?: string;
  newImagePreview?: string;

  snackbar = inject(MatSnackBar);

  constructor(
    private userService: UserService,
    @Inject(MAT_DIALOG_DATA) public data: {imageUrl: string},
    public dialogRef: MatDialogRef<EditAvatarComponent>) {
  }

  updatePicture(file: File){
    this.fileToUpload = file;
    previewImage(file).then(image => {
      this.newImagePreview = image;
    });
  }

  uploadNewPicture(){
    if(this.fileToUpload !== undefined){
      // todo upload picture
    } else {
      this.snackbar.open("Before uploading you must choose your picture!",'Ok', {duration: 3000})
    }
  }

}
