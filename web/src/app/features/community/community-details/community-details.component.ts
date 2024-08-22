import { Component } from '@angular/core';
import {LayoutComponent} from "../../../core/components/layout/layout.component";
import {MatList, MatListItem} from "@angular/material/list";
import {RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-community-details',
  standalone: true,
  imports: [
    LayoutComponent,
    MatList,
    MatListItem,
    RouterOutlet,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './community-details.component.html',
  styleUrl: './community-details.component.scss'
})
export class CommunityDetailsComponent {

}
