import {Component, inject, OnInit} from '@angular/core';
import {CommunityQuery} from "../../../store/community/community.query";
import {Community} from "../../../models/community";
import {AsyncPipe, NgIf} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {CommunityService} from "../../../services/community.service";
import {MatDialog} from "@angular/material/dialog";
import {GenerateInvitationComponent} from "../dialogs/generate-invitation/generate-invitation.component";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {UserService} from "../../../../core/services/user.service";

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [
    AsyncPipe,
    MatButton,
    MatIcon,
    MatTooltip,
    NgIf
  ],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss'
})

export class OverviewComponent implements OnInit{
  readonly dialog: MatDialog = inject(MatDialog);

  selectedCommunity: Community | undefined;

  constructor(
    protected communityQuery: CommunityQuery,
    protected userService: UserService,
    private communityService: CommunityService) {
  }

  ngOnInit() {
    this.communityQuery.selectActive().subscribe(community => {
      this.selectedCommunity = community;
    });
  }

  deleteCommunity(id: string | undefined){
    if(id){
      this.communityService.deleteCommunity(id);
    }
  }

  openDialog(){
    this.dialog.open(GenerateInvitationComponent, {data: {id: this.selectedCommunity?.id}});
  }

}
