import {Component, inject, Inject, OnInit, signal} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef, MatDialogTitle
} from "@angular/material/dialog";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {FileUploadComponent} from "../../../../../shared/ui/file-upload/file-upload.component";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {ChannelService} from "../../../../services/channel.service";
import {MatProgressBar} from "@angular/material/progress-bar";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {NgIf} from "@angular/common";
import {Channel, ChannelType} from "../../../../models/channel";
import {catchError, of} from "rxjs";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-create-channel',
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
    ReactiveFormsModule,
    MatOption,
    MatSelect,
    MatProgressBar,
    MatProgressSpinner,
    NgIf
  ],
  templateUrl: './create-channel.component.html'
})
export class CreateChannelComponent implements OnInit{
  loading = signal<boolean>(false);
  editing = signal<boolean>(false);
  id: string;
  communityId: string;

  channelForm = new FormGroup({
    name: new FormControl('', [Validators.required]),
    type: new FormControl('', Validators.required)
  });

  private snackBar = inject(MatSnackBar);

  constructor(
    public dialogRef: MatDialogRef<CreateChannelComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {editing: boolean, channel: Channel},
    private channelService: ChannelService) {
  }

  ngOnInit() {
    if(this.data !== null){
      this.editing.set(this.data.editing);
      this.id = this.data.channel.id;
      this.communityId = this.data.channel.communityId;

      this.channelForm.setValue({
        name: this.data.channel.name,
        type: this.data.channel.type.toString()
      })
    }
  }

  submitChannelForm(){
    this.loading.set(true);

    if(this.editing()){
      this.editChannel();
    } else {
      this.createChannel();
    }
  }

  createChannel(){
    this.channelService.createChannel(this.channelForm.value).pipe(
      catchError(error => {
        this.loading.set(false);
        this.snackBar.open("Cannot create new channel", "Ok");

        return of(null);
      })).subscribe(channel => {
      this.loading.set(false);
      this.dialogRef.close();
    });
  }

  editChannel() {
    if(this.editing() && this.id){
      const channel = {
        id: this.id,
        communityId: this.communityId,
        name: this.channelForm.value.name ?? '',
        // ☠☠☠
        type: this.channelForm.value.type == '0' ? ChannelType.Text : ChannelType.Voice ?? ChannelType.Text
      }

      this.channelService.updateChannel(channel).subscribe(channel => {
        this.loading.set(false);
        this.dialogRef.close();
      });
    }
  }

  deleteChannel() {
    if(this.editing() && this.id){
      this.channelService.deleteChannel(this.id).subscribe(channel => {
        this.dialogRef.close();
      });
    }
  }

}
