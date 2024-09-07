import {Component, inject, OnInit} from '@angular/core';
import {CommunityQuery} from "../../../store/community.query";
import {Observable} from "rxjs";
import {Community} from "../../../models/community";
import {AsyncPipe} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {CommunityService} from "../../../services/community.service";
import {UserService} from "../../../../core/services/user.service";
import {MatDialog} from "@angular/material/dialog";
import {GenerateInvitationComponent} from "../dialogs/generate-invitation/generate-invitation.component";
import {environment} from "../../../../../environment";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [
    AsyncPipe,
    MatButton,
    MatIcon,
    MatTooltip
  ],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss'
})
export class OverviewComponent implements OnInit{
  readonly dialog: MatDialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  community: Community | undefined;
  linkCreated: boolean = false;
  link: string | undefined;

  constructor(
    private communityQuery: CommunityQuery,
    private communityService: CommunityService,
    protected userService: UserService) {
  }

  ngOnInit() {
    this.communityQuery.community$.subscribe(community => {
      this.community = community;
    });
  }

  deleteCommunity(id: string | undefined){
    if(id){
      this.communityService.deleteCommunity(id);
    }
  }

  openDialog(){
    const dialogRef = this.dialog.open(GenerateInvitationComponent);
    dialogRef.afterClosed().subscribe(result => {
      if(result.generated){
        this.createInvitation(result.days);
      }
    });
  }

  createInvitation(days: number){
    if(this.community?.id){
      this.communityService.createInvitation(this.community?.id, days).subscribe({
        next: res => {
          this.link = environment.domain + res.link;
          this.linkCreated = true;
        },
        error: err => {console.log(err)}
      });
    }
  }

  copyToClipboard(){
    if (typeof this.link === "string") {
      navigator.clipboard.writeText(this.link);
      this.snackBar.open('Copied to clipboard');
    }
  }

}
