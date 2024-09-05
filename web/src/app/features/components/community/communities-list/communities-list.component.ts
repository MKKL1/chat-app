import {Component, OnInit} from '@angular/core';
import {CommunityCardComponent} from "../community-card/community-card.component";
import {RouterLink} from "@angular/router";
import {Community} from "../../../models/community";
import {AsyncPipe, NgForOf} from "@angular/common";
import {Observable} from "rxjs";
import {CommunityService} from "../../../services/community.service";

@Component({
  selector: 'app-communities-list',
  standalone: true,
  imports: [
    CommunityCardComponent,
    RouterLink,
    NgForOf,
    AsyncPipe
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

  selectCommunity(id: string){
    this.communityService.fetchCommunity(id);
  }

}
