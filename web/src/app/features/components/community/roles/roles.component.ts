import {Component, inject, OnDestroy, signal} from '@angular/core';
import {MatTableModule} from "@angular/material/table";
import {MatFabButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {RoleDialogComponent} from "../dialogs/role-dialog/role-dialog.component";
import {RoleQuery} from "../../../store/role/role.query";
import {Subscription} from "rxjs";
import {Role} from "../../../models/role";
import {MatList, MatListModule} from "@angular/material/list";
import {MatCard, MatCardModule} from "@angular/material/card";
import {RoleService} from "../../../services/role.service";
import {CommunityQuery} from "../../../store/community/community.query";
import {RoleMembersComponent} from "../dialogs/role-members/role-members.component";

@Component({
  selector: 'app-roles',
  standalone: true,
  imports: [
    MatTableModule,
    MatIconButton,
    MatIcon,
    MatFabButton,
    MatListModule,
    MatCardModule
  ],
  templateUrl: './roles.component.html',
  styleUrl: './roles.component.scss'
})
export class RolesComponent implements OnDestroy{
  displayedColumns: string[] = ['name', 'members', 'edit'];

  roles = signal<Role[]>([]);

  readonly dialog: MatDialog = inject(MatDialog);

  private roleSubscription: Subscription;
  private communitySubscription: Subscription;

  constructor(private roleQuery: RoleQuery, private roleService: RoleService, private communityQuery: CommunityQuery) {
    this.communitySubscription = this.communityQuery
      .selectActiveId()
      .subscribe(
        communityId => {
          this.roles.set(this.roleQuery.getAll({
            filterBy: entity => entity.communityId === communityId
          }));
        }
      );

    this.roleSubscription = this.roleQuery.selectAll({
      filterBy: entity => entity.communityId === this.communityQuery.getActiveId()
    }).subscribe(roles => this.roles.set(roles));
  }

  addRole(){
    this.dialog.open(RoleDialogComponent, {width: '60vw'});
  }

  editRole(id: string){

  }

  deleteRole(id: string){
    this.roleService.deleteRole(id);
  }

  addMembersToRole(role: Role){
    this.dialog.open(RoleMembersComponent, {
      width: '60vw',
      data: {
        role: role
      }
    });
  }

  ngOnDestroy() {
    this.communitySubscription.unsubscribe();
    this.roleSubscription.unsubscribe();
  }
}
