import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CommunityService} from "../../../services/community.service";
import {CommunityQuery} from "../../../store/community/community.query";
import {Community} from "../../../models/community";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

// TODO
// add styling to invitation, center spinner
// catch more info than just community (owner, number of members)
// first discover how to do it in db (mapping)

// wait with adding signal for change in api

@Component({
  selector: 'app-invitation',
  standalone: true,
  imports: [
    MatProgressSpinner
  ],
  templateUrl: './invitation.component.html',
  styleUrl: './invitation.component.scss'
})
export class InvitationComponent  implements OnInit{
  loadedInvitation: boolean = false;
  communityId: string | null | undefined;
  invitationId: string | null | undefined;
  community: Community | undefined;

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
        this.communityService.fetchCommunity(this.communityId);
      }

      // shouldn't be loaded from store, but there isn't any endpoint for this yet
      this.community = this.communityQuery.getActive();
      this.loadedInvitation = true;
    });
  }

  acceptInvitation(){
    if (this.communityId != null && this.invitationId != null) {
      this.communityService.acceptInvitation(this.communityId, this.invitationId).subscribe(res => {
        console.log(res);
        this.router.navigate(["/app/communities"]);
      })
    }
  }

}
