import { Routes } from '@angular/router';
import {LandingComponent} from "./features/components/pages/landing/landing.component";
import {LoginComponent} from "./features/components/pages/login/login.component";
import {RegisterComponent} from "./features/components/pages/register/register.component";
import {MainComponent} from "./features/components/pages/main/main.component";
import {AuthGuard} from "./core/auth/auth.guard";
import {TextChannelComponent} from "./features/components/pages/text-channel/text-channel.component";
import {VoiceChannelComponent} from "./features/components/pages/voice-channel/voice-channel.component";
import {CommunityComponent} from "./features/components/pages/community/community.component";
import {CommunityDetailsComponent} from "./features/components/community/community-details/community-details.component";
import {OverviewComponent} from "./features/components/community/overview/overview.component";
import {RolesComponent} from "./features/components/community/roles/roles.component";
import {UsersListComponent} from "./features/components/community/users-list/users-list.component";
import {ProfileComponent} from "./features/components/pages/profile/profile.component";
import {InvitationComponent} from "./features/components/pages/invitation/invitation.component";

// train at 21:50
// bus at 21:04 :(

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
        component: TextChannelComponent
      },
      {
        path: 'voice',
        component: VoiceChannelComponent
      },
      {
        path: 'communities',
        component: CommunityComponent,
      },
      {
        path: 'communities/details',
        component: CommunityDetailsComponent,
        children: [
          {
            path: 'overview',
            component: OverviewComponent
          },
          {
            path: 'roles',
            component: RolesComponent
          },
          {
            path: 'members',
            component: UsersListComponent
          }
        ]
      },
      {
        path: 'profile',
        component: ProfileComponent
      }
    ]
  }
];
