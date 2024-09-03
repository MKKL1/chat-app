import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {UsersListComponent} from "../../community/users-list/users-list.component";
import {UserPanelComponent} from "../../voice-chat/user-panel/user-panel.component";
import {MatFabButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateCommunityComponent} from "../../community/create-community/create-community.component";
import {CommunitiesListComponent} from "../../community/communities-list/communities-list.component";
import {CommunityService} from "../../../services/community.service";
import {LayoutComponent} from "../../../../core/components/layout/layout.component";

@Component({
  selector: 'app-community',
  standalone: true,
  imports: [
    UsersListComponent,
    UserPanelComponent,
    MatFabButton,
    MatIcon,
    CommunitiesListComponent,
    LayoutComponent
  ],
  templateUrl: './community.component.html',
  styleUrl: './community.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CommunityComponent {
    readonly dialog: MatDialog = inject(MatDialog);

    constructor(private communityService: CommunityService) {}

    // TODO specify types and handle response
    // I tried to add new created community to list with ngrx, but amount of boilerplate
    // is killing me
    // I will try to change it back to plain service
    openDialog(){
      const dialogRef = this.dialog.open(CreateCommunityComponent, {width: '60vw'});
      dialogRef.afterClosed().subscribe(result => {
        console.log("Dialog result: ");
        console.log(result);
        this.communityService.createCommunity(result.form);
      });
    }
}
