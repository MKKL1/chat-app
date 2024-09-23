import {Component, OnInit} from '@angular/core';
import {CommunityCardComponent} from "../community-card/community-card.component";
import {RouterLink} from "@angular/router";
import {Community} from "../../../models/community";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {catchError, finalize, Observable, of} from "rxjs";
import {CommunityService} from "../../../services/community.service";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {FormsModule} from "@angular/forms";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

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

  constructor(private communityService: CommunityService) {
  }

  ngOnInit() {
    this.communityService.fetchCommunities();
  }

  get communities$(){
    return this.communityService.getUserCommunities().pipe(
      catchError(error => {
        this.errorMessage = "Cannot load communities"
        return of([]);
      }),
      finalize(() => this.isLoading = false)
    );
  }

  get ownedCommunities$(){
    return this.communityService.getOwnedCommunities();
  }

  selectCommunity(id: string){
    console.log("Select community component");
    this.communityService.fetchCommunity(id);
  }

  onToggleChange(value: string){
    this.showOnlyOwned = value !== 'all';
  }

}
