import {Component, OnInit} from '@angular/core';
import {CommunityQuery} from "../../../store/community.query";
import {Observable} from "rxjs";
import {Community} from "../../../models/community";
import {AsyncPipe} from "@angular/common";

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [
    AsyncPipe
  ],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss'
})
export class OverviewComponent implements OnInit{
  community$: Observable<Community>;

  constructor(private communityQuery: CommunityQuery) {
    this.community$ = this.communityQuery.community$;
  }

  ngOnInit() {
  }

}
