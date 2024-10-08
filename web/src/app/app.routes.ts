import { Routes } from '@angular/router';
import {LandingComponent} from "./features/components/pages/landing/landing.component";
import {MainComponent} from "./features/components/pages/main/main.component";
import {AuthGuard} from "./core/guards/auth.guard";
import {TextChannelComponent} from "./features/components/pages/text-channel/text-channel.component";
import {VoiceChannelComponent} from "./features/components/pages/voice-channel/voice-channel.component";
import {CommunityComponent} from "./features/components/pages/community/community.component";
import {ProfileComponent} from "./features/components/pages/profile/profile.component";
import {InvitationComponent} from "./features/components/pages/invitation/invitation.component";
import {communityGuard} from "./core/guards/community.guard";


export const routes: Routes = [
  {
    path: '',
    component: LandingComponent
  },
  {
    path: 'community/:communityId/join/:invitationId',
    component: InvitationComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'app',
    component: MainComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'text',
        component: TextChannelComponent,
        canActivate: [communityGuard]
      },
      {
        path: 'voice',
        component: VoiceChannelComponent,
        canActivate: [communityGuard]
      },
      {
        path: 'communities',
        component: CommunityComponent,
      },
      {
        path: 'profile',
        component: ProfileComponent
      }
    ]
  }
];
