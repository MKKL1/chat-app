import {Component, inject} from '@angular/core';
import {MatTableModule} from "@angular/material/table";
import {MatFabButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateCommunityComponent} from "../modals/create-community/create-community.component";
import {RoleDialogComponent} from "../role-dialog/role-dialog.component";

export interface Role{
  name: string;
  members: number;
}

@Component({
  selector: 'app-roles',
  standalone: true,
  imports: [
    MatTableModule,
    MatIconButton,
    MatIcon,
    MatFabButton
  ],
  templateUrl: './roles.component.html',
  styleUrl: './roles.component.scss'
})
export class RolesComponent {
  displayedColumns: string[] = ['name', 'members', 'edit'];

  roles: Role[] = [
    {name: 'Admin', members: 3},
    {name: 'Guest', members: 12},
    {name: 'Moderator', members: 5},
    {name: 'Owner', members: 1}
  ];

  readonly dialog: MatDialog = inject(MatDialog);

  openDialog(){
    const dialogRef = this.dialog.open(RoleDialogComponent, {width: '60vw'});
    dialogRef.afterClosed().subscribe(result => {
      // here handle creating new community
      console.log("Dialog result: ");
      console.log(result);
    })
  }
}
