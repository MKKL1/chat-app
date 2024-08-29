import {Component, OnInit} from '@angular/core';
import {CommunityCardComponent} from "../community-card/community-card.component";
import {RouterLink} from "@angular/router";
import {CommunityService} from "../../../services/community.service";
import {Community} from "../../../models/community";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-communities-list',
  standalone: true,
  imports: [
    CommunityCardComponent,
    RouterLink,
    NgForOf
  ],
  templateUrl: './communities-list.component.html',
  styleUrl: './communities-list.component.scss'
})
export class CommunitiesListComponent implements OnInit {
  communities: Community[] = [];

  constructor(private communityService: CommunityService) {
  }

  ngOnInit() {
    this.loadCommunities();
  }

  // I have no idea why it doesn't want to work
  loadCommunities() {
    this.communityService.getAllCommunities().subscribe({
      next: (communities: Array<Community>) => {
        console.log('Fetched communities:', communities); // Debugowanie
        this.communities = communities;
      },
      error: (err) => {
        console.error('Error fetching communities:', err);
      }
    });
  }

}
