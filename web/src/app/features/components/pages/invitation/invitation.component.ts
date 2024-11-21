import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CommunityService} from "../../../services/community.service";
import {CommunityQuery} from "../../../store/community/community.query";
import {Community} from "../../../models/community";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatCard, MatCardActions, MatCardContent, MatCardHeader} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {Subscription} from "rxjs";

// TODO
// add displaying community image somewhere
// catch more info than just community (owner, number of members)
// first discover how to do it in db (mapping)

@Component({
  selector: 'app-invitation',
  standalone: true,
  imports: [
    MatProgressSpinner,
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatCardActions,
    MatButton
  ],
  templateUrl: './invitation.component.html',
  styleUrl: './invitation.component.scss'
})
export class InvitationComponent  implements OnInit, OnDestroy{
  loadedInvitation: boolean = false;
  communityId: string | null | undefined;
  invitationId: string | null | undefined;
  community: Community | undefined;

  private communitySubscription: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private communityService: CommunityService,
    private communityQuery: CommunityQuery) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.communityId = params.get('communityId');
      this.invitationId = params.get('invitationId');

      console.log('Community ID:', params.get('communityId'));
      console.log('Invite ID:', params.get('inviteId'));


      if (this.communityId != null) {
        this.communityService.changeCommunity(this.communityId);
      }

      // shouldn't be loaded from store, but there isn't any endpoint for this yet
      this.community = this.communityQuery.getActive();
      this.loadedInvitation = true;
    });
  }

  acceptInvitation(){
    if (this.communityId != null && this.invitationId != null) {
      this.communitySubscription = this.communityService
        .acceptInvitation(this.communityId, this.invitationId)
        .subscribe(res => {
          console.log(res);
          this.router.navigate(["/app/communities"]);
        }
      );
    }
  }

  ngOnDestroy() {
    if (this.communitySubscription) {
      this.communitySubscription.unsubscribe();
    }
  }

}
