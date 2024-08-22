import { Routes } from '@angular/router';
import {LoginComponent} from "./features/pages/login/login.component";
import {RegisterComponent} from "./features/pages/register/register.component";
import {CommunityComponent} from "./features/pages/community/community.component";
import {VoiceChannelComponent} from "./features/pages/voice-channel/voice-channel.component";
import {ProfileComponent} from "./features/pages/profile/profile.component";
import {MainComponent} from "./features/pages/main/main.component";
import {TextChannelComponent} from "./features/pages/text-channel/text-channel.component";
import {CommunityDetailsComponent} from "./features/community/community-details/community-details.component";
import {OverviewComponent} from "./features/community/overview/overview.component";
import {RolesComponent} from "./features/community/roles/roles.component";
import {UsersListComponent} from "./features/community/users-list/users-list.component";
import {AuthGuard} from "./core/auth/auth.guard";
import {LandingComponent} from "./features/pages/landing/landing.component";

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
