import {Component, Inject, OnInit, signal} from '@angular/core';
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
import {MatOption} from "@angular/material/core";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatSelect} from "@angular/material/select";
import {PaginatorModule} from "primeng/paginator";
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {RoleService} from "../../../../services/role.service";
import {Role} from "../../../../models/role";
import {RoleQuery} from "../../../../store/role/role.query";
import {MatListModule} from "@angular/material/list";
import {SelectButtonModule} from "primeng/selectbutton";
import {Permission} from "../../../../models/permission";

export interface RoleValue {
  label: string;
  bit: number;
}

@Component({
  selector: 'app-edit-permissions',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatInput,
    MatLabel,
    MatOption,
    MatProgressSpinner,
    MatSelect,
    PaginatorModule,
    ReactiveFormsModule,
    MatListModule,
    SelectButtonModule
  ],
  templateUrl: './edit-permissions.component.html',
  styleUrl: './edit-permissions.component.scss'
})
export class EditPermissionsComponent implements OnInit{
  roles = signal<Role[]>([]);
  selectedRole = signal<Role | null>(null);
  permissions: Permission;

  roleForm: FormGroup;

  roleOptions: any[] = [
    {icon: 'pi pi-check', value: 'allow'},
    {icon: 'pi pi-minus', value: 'none'},
    {icon: 'pi pi-times', value: 'denied'}
  ];

  constructor(private roleService: RoleService,
              private roleQuery: RoleQuery,
              @Inject(MAT_DIALOG_DATA) public data: {communityId: string},
              private fb: FormBuilder) {
    this.roleForm = this.fb.group({});
  }

  ngOnInit() {
    this.roles.set(
      this.roleQuery.getByCommunityId(this.data.communityId)
    );

    console.log(this.roles());
  }

  // TODO add patch for channel in api
  selectRole(role: Role){
    this.selectedRole.set(role);

    // get ovewrties from overwrites in channel
  }

  onSubmit(){

  }

}
