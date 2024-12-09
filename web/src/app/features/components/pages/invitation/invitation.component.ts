import {Component, OnDestroy, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CommunityService} from "../../../services/community.service";
import {CommunityQuery} from "../../../store/community/community.query";
import {Community} from "../../../models/community";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatCard, MatCardActions, MatCardContent, MatCardHeader} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {Subscription} from "rxjs";
import {filePathMapping} from "../../../../shared/utils/utils";

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
  communityId: string | null | undefined;
  invitationId: string | null | undefined;
  community = signal<Community | null>(null);

  private communitySub: Subscription;
  private invitationSub: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private communityService: CommunityService) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.communityId = params.get('communityId');
      this.invitationId = params.get('invitationId');

      this.communitySub = this.communityService
        .getInvitationInfo(this.communityId!, this.invitationId!)
        .subscribe(community => {
          this.community.set(community);
        }
      );
    });
  }

  acceptInvitation(){
    if (this.communityId != null && this.invitationId != null) {
      this.invitationSub = this.communityService
        .acceptInvitation(this.communityId, this.invitationId)
        .subscribe(res => {
          this.router.navigate(["/app/communities"]);
        }
      );
    }
  }

  ngOnDestroy() {
    this.communitySub.unsubscribe();
    if (this.communitySub) {
      this.invitationSub.unsubscribe();
    }
  }

  protected readonly filePathMapping = filePathMapping;
}
