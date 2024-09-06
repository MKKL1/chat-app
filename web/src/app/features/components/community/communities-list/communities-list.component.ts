import {Component, OnInit} from '@angular/core';
import {CommunityCardComponent} from "../community-card/community-card.component";
import {RouterLink} from "@angular/router";
import {Community} from "../../../models/community";
import {AsyncPipe, NgForOf} from "@angular/common";
import {Observable} from "rxjs";
import {CommunityService} from "../../../services/community.service";
import {MatTab, MatTabGroup} from "@angular/material/tabs";

@Component({
  selector: 'app-communities-list',
  standalone: true,
  imports: [
    CommunityCardComponent,
    RouterLink,
    NgForOf,
    AsyncPipe,
    MatTabGroup,
    MatTab
  ],
  templateUrl: './communities-list.component.html',
  styleUrl: './communities-list.component.scss'
})

export class CommunitiesListComponent implements OnInit {

  constructor(private communityService: CommunityService) {
  }

  ngOnInit() {
    this.communityService.fetchCommunities();
  }

  get communities$(){
    return this.communityService.getUserCommunities();
  }

  get ownedCommunities$(){
    return this.communityService.getOwnedCommunities();
  }

  selectCommunity(id: string){
    this.communityService.fetchCommunity(id);
  }

}
