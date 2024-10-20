import {Component, OnInit} from '@angular/core';
import {CommunityCardComponent} from "../community-card/community-card.component";
import {RouterLink} from "@angular/router";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {CommunityService} from "../../../services/community.service";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {FormsModule} from "@angular/forms";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {CommunityQuery} from "../../../store/community/community.query";
import {UserService} from "../../../../core/services/user.service";

@Component({
  selector: 'app-communities-list',
  standalone: true,
  imports: [
    CommunityCardComponent,
    RouterLink,
    NgForOf,
    AsyncPipe,
    MatTabGroup,
    MatTab,
    MatSlideToggle,
    MatButtonToggleGroup,
    MatButtonToggle,
    FormsModule,
    NgIf,
    MatProgressSpinner
  ],
  templateUrl: './communities-list.component.html',
  styleUrl: './communities-list.component.scss'
})

export class CommunitiesListComponent implements OnInit {
  showOnlyOwned: boolean = false;
  errorMessage: string | null = null;
  isLoading: boolean = true;

  constructor(
    private communityService: CommunityService,
    private communityQuery: CommunityQuery,
    private userService: UserService) {
  }

  ngOnInit() {
    this.communityService.getCommunities();
  }

  get communities$(){
    return this.communityQuery.selectAll();
  }

  get ownedCommunities$(){
    return this.communityQuery.selectAll({
      filterBy: entity => entity.ownerId === this.userService.getUser().id
    });
  }

  selectCommunity(id: string){
    this.communityService.fetchCommunity(id);
  }

  onToggleChange(value: string){
    this.showOnlyOwned = value !== 'all';
  }

}
