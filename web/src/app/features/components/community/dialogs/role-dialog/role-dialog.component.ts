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
import {clearBit, isBitSet, setBit} from "../../../../../shared/utils/binaryOperations";
import {RoleService} from "../../../../services/role.service";
import {Role} from "../../../../models/role";
import {Permission} from "../../../../models/permission";
import {SelectButtonModule} from "primeng/selectbutton";
import {formatRoleName} from "../../../../../shared/utils/utils";
import {MessageService} from "primeng/api";

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

  roleName = new FormControl('', [
    Validators.required,
    Validators.minLength(3)
  ]);

  editing: boolean = false;

  roleOptions: any[] = [
    {icon: 'pi pi-check', value: 'allow'},
    {icon: 'pi pi-minus', value: 'none'},
    {icon: 'pi pi-times', value: 'denied'}
  ];

  constructor(
    private fb: FormBuilder,
    private roleService: RoleService,
    private messageService: MessageService,
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

    const permissionKeys = this.permissions.getPermissionsNames();
    const permissionsCount = Object.keys(this.permissions).length - 1;

    let allowBitValue: boolean;
    let denyBitValue: boolean;
    let startValue: any;

    for(let i=0n; i<permissionsCount; i++){
      allowBitValue = isBitSet(this.permissions.rawValue, i);
      denyBitValue = isBitSet(this.permissions.rawValue, i + 32n);

      if(allowBitValue && !denyBitValue){
        startValue = {icon: 'pi pi-check', value: 'allow'};
      } else if(!allowBitValue && denyBitValue){
        startValue = {icon: 'pi pi-times', value: 'denied'};
      } else {
        startValue = {icon: 'pi pi-minus', value: 'none'};
      }

      this.roleForm.addControl(permissionKeys[Number(i)], new FormControl(startValue));
    }
  }

  onSubmit(){
    if(this.roleForm.valid){
      let currentBit = 0n;
      Object.keys(this.roleForm.controls).forEach(controlName => {
        const control = this.roleForm.get(controlName);

        // status can be -> allow, denied or none
        // it has to replace boolean value, because of p-select element
        const status = control?.value.value;
        if(status === 'allow'){
          this.permissionOverride = setBit(this.permissionOverride, currentBit);
        } else if(status === 'denied'){
          this.permissionOverride = setBit(this.permissionOverride, currentBit + 32n);
          this.permissionOverride = clearBit(this.permissionOverride, currentBit);
        } else {
          this.permissionOverride = clearBit(this.permissionOverride, currentBit);
          this.permissionOverride = clearBit(this.permissionOverride, currentBit + 32n);
        }

        currentBit++;
      });

      if(this.editing){
        this.editRole();
      } else {
        this.createRole();
      }
    }
  }

  createRole(){
    this.roleService.createRole(this.roleName.value!, this.permissionOverride)
      .subscribe(role => {
        this.messageService.add({severity: 'info', summary: `Added new role: ${role.role.name}`});
        this.dialogRef.close();
      }
    );
  }

  editRole(){
    let nameChanged = false;
    let permissionChanged = false;

    if(this.roleName.value !== this.roleToUpdate?.name){
      nameChanged = true;
    }

    if(this.permissionOverride !== BigInt(this.roleToUpdate?.permissionOverwrites!)){
      permissionChanged = true;
    }

    this.roleService.editRole(
      this.roleToUpdate?.id!,
      nameChanged ? this.roleName.value! : undefined,
      permissionChanged ? String(this.permissionOverride!) : undefined
    ).subscribe(role => {
      this.messageService.add({severity: 'info', summary: `Modified role ${role.role.name}`});
      this.dialogRef.close()
    });
  }

  protected readonly Object = Object;
  protected readonly formatRoleName = formatRoleName;
}
