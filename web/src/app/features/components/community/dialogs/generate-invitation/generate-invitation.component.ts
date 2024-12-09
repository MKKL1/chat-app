import {Component, inject, Inject, signal} from '@angular/core';
import {FileUploadComponent} from "../../../../../shared/ui/file-upload/file-upload.component";
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {environment} from "../../../../../../environment";
import {CommunityService} from "../../../../services/community.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";

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
    ReactiveFormsModule,
    MatIcon,
    MatTooltip
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

  linkCreated = signal<boolean>(false);
  link = signal<string>('');

  private snackBar = inject(MatSnackBar);

  constructor(private communityService: CommunityService,
              @Inject(MAT_DIALOG_DATA) public data: {id: string}) {
  }

  generate(){
    if(this.data.id){
      let days = 0;

      if(this.daysForm.valid){
        let daysString = this.daysForm.get('days')?.value;
        if (daysString != null) {
          days = parseInt(daysString);
        }
      }

      this.communityService.createInvitation(this.data.id, days).subscribe({
        next: res => {
          this.link.set(environment.domain + res.link);
          this.linkCreated.set(true);
        },
        error: err => {console.log(err)}
      });
    }
  }

  copyToClipboard(){
    navigator.clipboard.writeText(this.link());
    this.snackBar.open('Copied to clipboard', 'Ok');
  }

}
