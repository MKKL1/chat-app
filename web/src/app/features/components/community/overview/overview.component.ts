import {Component, inject, OnDestroy, OnInit, signal, WritableSignal} from '@angular/core';
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
import {CreateCommunityComponent} from "../dialogs/create-community/create-community.component";
import {Subscription} from "rxjs";
import {PermissionService} from "../../../../core/services/permission.service";

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

export class OverviewComponent implements OnInit, OnDestroy{
  readonly dialog: MatDialog = inject(MatDialog);

  selectedCommunity: WritableSignal<Community>;

  private communitySubscription: Subscription;

  constructor(
    protected communityQuery: CommunityQuery,
    private permissionService: PermissionService,
    private communityService: CommunityService,
    protected userService: UserService) {
  }

  ngOnInit() {
    this.communitySubscription =this.communityQuery.selectActive()
      .subscribe(community => {
        // when rendering this component active community must be defined
        this.selectedCommunity = signal(community!);
      }
    );

    console.log(this.permissionService.getPermission());
  }

  deleteCommunity(id: string){
    this.communityService.deleteCommunity(id);
  }

  editCommunity(){
    this.dialog.open(CreateCommunityComponent, {data: {editing: true, community: this.selectedCommunity()}});
  }

  createInvitation(){
    this.dialog.open(GenerateInvitationComponent, {data: {id: this.selectedCommunity().id}});
  }

  checkInvitationPermission(): boolean{
    const permission = this.permissionService.getPermission();
    return permission.canCreateInvitation;
  }

  ngOnDestroy() {
    this.communitySubscription.unsubscribe();
  }
}
