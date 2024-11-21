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
import {Permission} from "../../../models/permission";
import {toSignal} from "@angular/core/rxjs-interop";
import {ConfirmationService} from "primeng/api";
import {CommunityStats} from "../../../models/community-stats";

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

  selectedCommunity = toSignal(this.communityQuery.selectActive());
  permission = toSignal(this.permissionService.permissions$);

  stats = signal<CommunityStats | undefined>(undefined);

  constructor(
    protected communityQuery: CommunityQuery,
    private permissionService: PermissionService,
    private communityService: CommunityService,
    protected userService: UserService,
    private confirmationService: ConfirmationService) {
  }

  ngOnInit() {
    this.stats.set(this.communityQuery.getStats());
  }

  confirmDeleteCommunity(event: Event, id: string){
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Are you sure you want to delete this community?',
      header: 'Confirmation',
      accept: () => this.deleteCommunity(id)
    })
  }

  deleteCommunity(id: string){
    this.communityService.deleteCommunity(id);
  }

  editCommunity(){
    this.dialog.open(CreateCommunityComponent, {
      width: '60vw',
      data: {
        editing: true,
        community: this.selectedCommunity()
      }
    });
  }

  createInvitation(){
    this.dialog.open(GenerateInvitationComponent, {data: {id: this.selectedCommunity()?.id}});
  }
}
