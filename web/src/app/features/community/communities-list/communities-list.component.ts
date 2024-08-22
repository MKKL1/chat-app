import { Component } from '@angular/core';
import {CommunityCardComponent} from "../community-card/community-card.component";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-communities-list',
  standalone: true,
  imports: [
    CommunityCardComponent,
    RouterLink
  ],
  templateUrl: './communities-list.component.html',
  styleUrl: './communities-list.component.scss'
})
export class CommunitiesListComponent {
  communities: any[] = [{},{},{},{},{}];
}
