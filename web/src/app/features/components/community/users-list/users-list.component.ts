import {Component, inject, OnDestroy, OnInit, signal} from '@angular/core';
import {MatAccordion, MatExpansionModule} from '@angular/material/expansion';
import {MatList, MatListItem, MatNavList} from "@angular/material/list";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {UserBasicInfoComponent} from "../../../../core/components/user-basic-info/user-basic-info.component";
import {AvatarComponent} from "../../../../shared/ui/avatar/avatar.component";
import {MemberQuery} from "../../../store/member/member.query";
import {Member} from "../../../models/member";
import {MatCardModule} from '@angular/material/card';
import {RoleQuery} from "../../../store/role/role.query";
import {Subscription} from "rxjs";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {Order} from "@datorama/akita";
import {CommunityQuery} from "../../../store/community/community.query";

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    MatAccordion,
    MatExpansionModule,
    AvatarComponent,
    MatChipSet,
    MatChip,
    UserBasicInfoComponent,
    MatNavList,
    MatList,
    MatListItem,
    MatCardModule,
    MatIconButton,
    MatIcon
  ],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.scss'
})
export class UsersListComponent implements OnInit, OnDestroy{

  members = signal<Member[]>([]);

  private communitySubscription: Subscription;

  constructor(
    private memberQuery: MemberQuery,
    private roleQuery: RoleQuery,
    private communityQuery: CommunityQuery
  ) {}

  ngOnInit() {
    this.communitySubscription = this.communityQuery
      .selectActiveId()
      .subscribe(
        communityId => {
          this.members.set(this.memberQuery.getAll({
            filterBy: entity => entity.communityId === communityId
          }));
        }
      );
  }

  getRoleName(id: string){
    return this.roleQuery.getEntity(id)?.name;
  }

  ngOnDestroy() {
    this.communitySubscription.unsubscribe();
  }

}
