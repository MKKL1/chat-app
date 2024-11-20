import {Component, computed, inject, Inject, OnInit, signal} from '@angular/core';
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
import {RoleService} from "../../../../services/role.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MessageService} from "primeng/api";

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

  private _snackBar = inject(MatSnackBar);

  constructor(public dialogRef: MatDialogRef<RoleMembersComponent>,
              @Inject(MAT_DIALOG_DATA) public data: {role: Role},
              private memberQuery: MemberQuery,
              private communityQuery: CommunityQuery,
              private roleService: RoleService,
              private messageService: MessageService) {
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
    const idsToMove: string[] = this.membersToAdd.value!;

    if(idsToMove.length === 0){
      this._snackBar.open("There is no member to add!", "OK");
      return;
    }

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
    // must filter it once again to get state of members with role before changes
    const currentMembers = this.members().filter(member => member.roles.includes(this.role()?.id!));
    const updatedMembersWithRole = this.membersWithRole();

    const addedMembers = updatedMembersWithRole.filter(
      updatedMember => !currentMembers.some(currentMember => currentMember.id === updatedMember.id)
    );

    const removedMembers = currentMembers.filter(
      currentMember => !updatedMembersWithRole.some(updatedMember => updatedMember.id === currentMember.id)
    );

    this.roleService.changeRoleMembers(this.role()!, addedMembers, removedMembers).subscribe(_ => {
      this.dialogRef.close();
      this.messageService.add({severity: 'success', summary: 'Role reassigned successfully'});
    });
  }

}
