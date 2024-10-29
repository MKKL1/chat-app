import {Component, computed, Inject, OnInit, signal} from '@angular/core';
import {MatButton, MatIconButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {Member} from "../../../../models/member";
import {Role} from "../../../../models/role";
import {MemberQuery} from "../../../../store/member/member.query";
import {CommunityQuery} from "../../../../store/community/community.query";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {AvatarComponent} from "../../../../../shared/ui/avatar/avatar.component";
import {MatCard, MatCardModule} from "@angular/material/card";

@Component({
  selector: 'app-role-members',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatLabel,
    MatOption,
    MatSelect,
    ReactiveFormsModule,
    MatIconButton,
    MatIcon,
    AvatarComponent,
    MatCardModule
  ],
  templateUrl: './role-members.component.html',
  styleUrl: './role-members.component.scss'
})
export class RoleMembersComponent implements OnInit{
  role = signal<Role | null>(null);
  members = signal<Member[]>([]);
  membersWithRole = signal<Member[]>([]);
  membersWithoutRole = signal<Member[]>([]);

  membersToAdd = new FormControl<string[]>([]);

  constructor(public dialogRef: MatDialogRef<RoleMembersComponent>,
              @Inject(MAT_DIALOG_DATA) public data: {role: Role},
              private memberQuery: MemberQuery,
              private communityQuery: CommunityQuery) {
    if(data){
      this.role.set(data.role);
    }
  }

  ngOnInit() {
    const communityId = this.communityQuery.getActiveId()!;
    this.members.set(this.memberQuery.getAll({
      filterBy: entity => entity.communityId === communityId
    }));

    this.membersWithRole.set(
      this.members().filter(member => member.roles.includes(this.role()?.id!))
    );

    this.membersWithoutRole.set(
      this.members().filter(member => !member.roles.includes(this.role()?.id!))
    );
  }

  transferMembersBetweenLists(){
    console.log(this.membersToAdd.value);
    const idsToMove: string[] = this.membersToAdd.value!;
    const membersToTransfer = this.membersWithoutRole()
      .filter(member => idsToMove.includes(member.id));

    console.log(membersToTransfer);
    // removing members from list of members which don't have role
    this.membersWithoutRole.update(
      members => members.filter(member => !idsToMove.includes(member.id))
    );

    // adding new members to list of members which have role
    this.membersWithRole.update(members => [...members, ...membersToTransfer]);
  }

  removeRoleFromMember(member: Member){
    this.membersWithoutRole.update(members => [...members, member]);
    this.membersWithRole.update(members =>
      members.filter(m => m.id !== member.id));
    this.membersToAdd.reset();
  }

  persistChanges(){
    // TODO compare changes in membersWithRole and members
    // and decided which should be sent to api
  }

}
