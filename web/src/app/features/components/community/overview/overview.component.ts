import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {CommunityQuery} from "../../../store/community/community.query";
import {Observable} from "rxjs";
import {Community} from "../../../models/community";
import {AsyncPipe, NgIf} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {CommunityService} from "../../../services/community.service";
import {UserService} from "../../../services/user.service";
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
    MatTooltip,
    NgIf
  ],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss'
})

// changes in this component are messed up
// maybe turn it back into routes?

export class OverviewComponent implements OnInit{
  readonly dialog: MatDialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  selectedCommunity: Community | undefined;
  linkCreated: boolean = false;
  link: string | undefined;

  constructor(
    protected communityQuery: CommunityQuery,
    private communityService: CommunityService,
    protected userService: UserService) {
  }

  ngOnInit() {
    this.communityQuery.community$.subscribe(community => {
      this.selectedCommunity = community;
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
    if(this.selectedCommunity?.id){
      this.communityService.createInvitation(this.selectedCommunity?.id, days).subscribe({
        next: res => {
          console.log(res.link);
          this.link = environment.domain + res.link;
          this.linkCreated = true;
          console.log(this.linkCreated);
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
