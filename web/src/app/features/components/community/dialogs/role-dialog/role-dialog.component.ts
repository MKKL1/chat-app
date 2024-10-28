import { Component } from '@angular/core';

import {MatButton} from "@angular/material/button";
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {FileUploadComponent} from "../../../../../shared/ui/file-upload/file-upload.component";
import {setBit} from "../../../../../shared/utils/binaryOperations";
import {RoleService} from "../../../../services/role.service";

@Component({
  selector: 'app-role-dialog',
  standalone: true,
  imports: [
    FileUploadComponent,
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatInputModule,
    ReactiveFormsModule,
    MatSlideToggle
  ],
  templateUrl: './role-dialog.component.html',
  styleUrl: './role-dialog.component.scss'
})
export class RoleDialogComponent {
  roleForm: FormGroup;

  permissionOverride: bigint = 0n;

  constructor(
    private fb: FormBuilder,
    private roleService: RoleService,
    public dialogRef: MatDialogRef<RoleDialogComponent>) {
    this.roleForm = this.fb.group({
      name: ['', Validators.required],
      isAdministrator: [false],
      canModifyRole: [false],
      canCreateInvitation: [false],
      canCreateChannel: [false],
      canModifyChannel: [false],
      canCreateMessage: [false],
      canDeleteMessage: [false],
      canCreateReaction: [false]
    })
  }

  // TODO add denying

  onSubmit(){
    if(this.roleForm.valid){
      let currentBit = 0n;
      Object.keys(this.roleForm.controls).forEach(controlName => {
        const control = this.roleForm.get(controlName);
        //console.log(`Control Name: ${controlName}, Value: ${control?.value}, Status: ${control?.status}`);

        // Only perform logical operations on boolean values
        if(controlName !== "name"){

          // if value is true, it means that some permission is checked
          // so it wil be added to permission override
          if(control?.value){
            this.permissionOverride = setBit(this.permissionOverride, currentBit);
          }

          currentBit++;
        }

      });

      this.roleService.createRole(this.roleForm.get("name")?.value, this.permissionOverride)
        .subscribe(res => {
          console.log(res);
          this.dialogRef.close();
        });
    }
  }
}
