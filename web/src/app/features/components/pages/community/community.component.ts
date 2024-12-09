import {ChangeDetectionStrategy, Component, inject, OnInit, signal} from '@angular/core';
import {UsersListComponent} from "../../community/users-list/users-list.component";
import {UserPanelComponent} from "../../voice-chat/user-panel/user-panel.component";
import {MatFabButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateCommunityComponent} from "../../community/dialogs/create-community/create-community.component";
import {CommunitiesListComponent} from "../../community/communities-list/communities-list.component";
import {LayoutComponent} from "../../../../core/components/layout/layout.component";
import {CommunityDetailsComponent} from "../../community/community-details/community-details.component";
import {CommunityQuery} from "../../../store/community/community.query";
import {AsyncPipe, NgIf} from "@angular/common";

@Component({
  selector: 'app-community',
  standalone: true,
  imports: [
    UsersListComponent,
    UserPanelComponent,
    MatFabButton,
    MatIcon,
    CommunitiesListComponent,
    LayoutComponent,
    CommunityDetailsComponent,
    NgIf,
    AsyncPipe
  ],
  templateUrl: './community.component.html',
  styleUrl: './community.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class CommunityComponent implements OnInit{
    readonly dialog: MatDialog = inject(MatDialog);
    isCommunitySelected = signal<boolean>(false);

    constructor(private communityQuery: CommunityQuery) {}

    ngOnInit() {
      this.communityQuery.selectActive().subscribe(community => {
        this.isCommunitySelected.set(community !== undefined);
      });
    }

    openDialog(){
      this.dialog.open(CreateCommunityComponent, {width: '60vw'});
    }
}
