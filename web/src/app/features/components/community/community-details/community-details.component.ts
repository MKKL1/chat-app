import { Component } from '@angular/core';
import {MatList, MatListItem} from "@angular/material/list";
import {RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {LayoutComponent} from "../../../../core/components/layout/layout.component";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {OverviewComponent} from "../overview/overview.component";
import {RolesComponent} from "../roles/roles.component";
import {UsersListComponent} from "../users-list/users-list.component";

@Component({
  selector: 'app-community-details',
  standalone: true,
  imports: [
    LayoutComponent,
    MatList,
    MatListItem,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatTabGroup,
    MatTab,
    OverviewComponent,
    RolesComponent,
    UsersListComponent
  ],
  templateUrl: './community-details.component.html',
  styleUrl: './community-details.component.scss'
})
export class CommunityDetailsComponent {

}
