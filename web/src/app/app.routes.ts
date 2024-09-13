import { Routes } from '@angular/router';
import {LandingComponent} from "./features/components/pages/landing/landing.component";
import {LoginComponent} from "./features/components/pages/login/login.component";
import {RegisterComponent} from "./features/components/pages/register/register.component";
import {MainComponent} from "./features/components/pages/main/main.component";
import {AuthGuard} from "./core/guards/auth.guard";
import {TextChannelComponent} from "./features/components/pages/text-channel/text-channel.component";
import {VoiceChannelComponent} from "./features/components/pages/voice-channel/voice-channel.component";
import {CommunityComponent} from "./features/components/pages/community/community.component";
import {CommunityDetailsComponent} from "./features/components/community/community-details/community-details.component";
import {ProfileComponent} from "./features/components/pages/profile/profile.component";
import {InvitationComponent} from "./features/components/pages/invitation/invitation.component";
import {communityGuard} from "./core/guards/community.guard";

// TODO to select community I need to click it two times, but only when I see all (not only owned)
// community guard is missing??

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
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
        path: 'communities/details',
        component: CommunityDetailsComponent,
      },
      {
        path: 'profile',
        component: ProfileComponent
      }
    ]
  }
];
