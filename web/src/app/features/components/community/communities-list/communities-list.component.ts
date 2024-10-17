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
    private communityQuery: CommunityQuery) {
  }

  ngOnInit() {
    this.communityService.getCommunities();
  }

  get communities$(){
    console.log(this.communityQuery.getAll());
    return this.communityQuery.selectAll();
  }

  // TODO implement again
  // get ownedCommunities$(){
  //   return this.communityService.getOwnedCommunities();
  // }

  selectCommunity(id: string){
    this.communityService.fetchCommunity(id);
  }

  onToggleChange(value: string){
    this.showOnlyOwned = value !== 'all';
  }

}
