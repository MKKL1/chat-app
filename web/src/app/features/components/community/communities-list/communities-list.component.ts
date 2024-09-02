import {Component, OnInit} from '@angular/core';
import {CommunityCardComponent} from "../community-card/community-card.component";
import {RouterLink} from "@angular/router";
import {Community} from "../../../models/community";
import {AsyncPipe, NgForOf} from "@angular/common";
import {Observable} from "rxjs";
import {Store} from "@ngrx/store";
import * as CommunitySelectors from '../../../store/community/community.selector';
import * as CommunityActions from '../../../store/community/community.actions';

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

// ng store is create in components, but i wonder if it isn't too much boilerplate for simple things

export class CommunitiesListComponent implements OnInit {
  communities$: Observable<Community[]>;

  constructor(private store: Store) {
    this.communities$ = this.store.select(CommunitySelectors.selectCommunities);
  }

  ngOnInit() {
    this.store.dispatch(CommunityActions.loadCommunities());
  }


}
