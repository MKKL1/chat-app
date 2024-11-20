import {ChangeDetectionStrategy, Component, Inject, OnInit, signal} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent, MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton, MatFabButton, MatMiniFabButton} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {FileUploadComponent} from "../../../../../shared/ui/file-upload/file-upload.component";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {CommunityService} from "../../../../services/community.service";
import {Community} from "../../../../models/community";
import {previewImage} from "../../../../../shared/utils/utils";

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
export class CreateCommunityComponent implements OnInit{
  editing = signal<boolean>(false);
  communityId: string;

  communityForm = new FormGroup({
    name: new FormControl('', Validators.required)
  });

  file?: File;
  imagePreview = signal<string | null>(null);

  constructor(private communityService: CommunityService,
    public dialogRef: MatDialogRef<CreateCommunityComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {editing: boolean, community: Community}) {
  }

  ngOnInit() {
    if(this.data === null){
      return;
    }

    this.imagePreview.set(this.data.community.imageUrl);
    this.editing.set(this.data.editing);
    this.communityId = this.data.community.id;
    this.communityForm.setValue({
      name: this.data.community.name
    });
  }

  resetImage(){
    this.imagePreview.set(null);
    this.file = undefined;
  }

  setFile(file: File){
    previewImage(file).then(image => {
      this.imagePreview.set(image);
    });
    this.file = file;
  }

  submitForm(): void {
    if(this.editing()){
      this.editCommunity();
    } else {
      this.createCommunity()
    }
  }

  private createCommunity(){
    if(typeof this.communityForm.value.name === 'string'){
      this.communityService.createCommunity({name: this.communityForm.value.name}, this.file);
    }
    this.dialogRef.close();
  }

  private editCommunity(){
    // todo send also image
    this.communityService.editCommunity();
  }
}
