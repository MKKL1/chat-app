import {Component, Inject} from '@angular/core';

import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {FileUploadComponent} from "../../../../../shared/ui/file-upload/file-upload.component";
import {setBit} from "../../../../../shared/utils/binaryOperations";
import {RoleService} from "../../../../services/role.service";
import {Role} from "../../../../models/role";
import {Permission} from "../../../../models/permission";
import {SelectButtonModule} from "primeng/selectbutton";

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
    MatSlideToggle,
    FormsModule,
    SelectButtonModule
  ],
  templateUrl: './role-dialog.component.html',
  styleUrl: './role-dialog.component.scss'
})
export class RoleDialogComponent {
  roleToUpdate: Role | null;
  permissions: Permission;
  roleForm: FormGroup;
  permissionOverride: bigint = 0n;

  roleName = new FormControl('');

  editing: boolean = false;

  roleOptions: any[] = [
    {icon: 'pi pi-check', value: 'allow'},
    {icon: 'pi pi-minus', value: 'none'},
    {icon: 'pi pi-times', value: 'denied'}
  ];

  constructor(
    private fb: FormBuilder,
    private roleService: RoleService,
    public dialogRef: MatDialogRef<RoleDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {roleToUpdate: Role}) {
    if(data !== null){
      this.roleToUpdate = this.data.roleToUpdate;
      this.permissions = new Permission(this.roleToUpdate.permissionOverwrites);
      this.roleName.setValue(this.roleToUpdate.name);
      this.editing = true;
    } else {
      this.roleToUpdate = null;
      this.permissions = new Permission("0");
    }

    this.roleForm = this.fb.group({});

    for (const key of Object.keys(this.permissions)){
      if(key !== 'rawValue'){
        this.roleForm.addControl(key, new FormControl(this.permissions[key as keyof Permission]));
      }
    }

    console.log(this.roleToUpdate);
    console.log(this.permissions);
  }

  // TODO add denying

  onSubmit(){
    if(this.roleForm.valid){
      let currentBit = 0n;
      Object.keys(this.roleForm.controls).forEach(controlName => {
        const control = this.roleForm.get(controlName);
        //console.log(`Control Name: ${controlName}, Value: ${control?.value}, Status: ${control?.status}`);

        // if value is true, it means that some permission is checked
        // so it wil be added to permission override
        if(control?.value){
          this.permissionOverride = setBit(this.permissionOverride, currentBit);
        }

        currentBit++;
      });

      this.roleService.createRole(this.roleName.value!, this.permissionOverride)
        .subscribe(res => {
          console.log(res);
          this.dialogRef.close();
        });
    }
  }

  createRole(){

  }

  editRole(){

  }
}
