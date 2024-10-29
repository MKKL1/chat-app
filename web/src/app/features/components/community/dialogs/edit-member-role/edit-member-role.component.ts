import {Component, Inject, OnInit, signal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatTooltip} from "@angular/material/tooltip";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {Community} from "../../../../models/community";
import {Member} from "../../../../models/member";
import {RoleQuery} from "../../../../store/role/role.query";
import {Role} from "../../../../models/role";
import {MatOption, MatSelect} from "@angular/material/select";
import {RoleService} from "../../../../services/role.service";
import {CommunityQuery} from "../../../../store/community/community.query";

@Component({
  selector: 'app-edit-member-role',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatIcon,
    MatInput,
    MatLabel,
    MatTooltip,
    ReactiveFormsModule,
    MatSelect,
    MatOption
  ],
  templateUrl: './edit-member-role.component.html',
  styleUrl: './edit-member-role.component.scss'
})
export class EditMemberRoleComponent implements OnInit{

  roles = signal<Role[]>([]);
  memberRoles = new FormControl('');

  constructor(public dialogRef: MatDialogRef<EditMemberRoleComponent>,
              @Inject(MAT_DIALOG_DATA) public data: {member: Member},
              private roleQuery: RoleQuery,
              private roleService: RoleService,
              private communityQuery: CommunityQuery) {
    if(data){
      console.log(data.member);
    }
  }

  ngOnInit() {
    this.roles.set(this.roleQuery.getAll({
      filterBy: entity => entity.communityId === this.communityQuery.getActiveId()
    }));
  }

  addRoles(){
    // array of roles ids
    console.log(this.memberRoles.value);
    this.roleService.addRoleToMember();
  }

}
