import { Routes } from '@angular/router';
import {LoginComponent} from "./features/pages/login/login.component";
import {RegisterComponent} from "./features/pages/register/register.component";
import {CommunityComponent} from "./features/pages/community/community.component";
import {VoiceChannelComponent} from "./features/pages/voice-channel/voice-channel.component";
import {ProfileComponent} from "./features/pages/profile/profile.component";
import {MainComponent} from "./features/pages/main/main.component";
import {TextChannelComponent} from "./features/pages/text-channel/text-channel.component";
import {AuthGuard} from "./core/auth/auth.guard";

export const routes: Routes = [
  {
    path: '',
    component: LoginComponent
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
        path: 'profile',
        component: ProfileComponent
      },
      {
        path: 'communities',
        component: CommunityComponent
      },
      {
        path: 'communities/text',
        component: TextChannelComponent
      },
      {
        path: 'communities/voice',
        component: VoiceChannelComponent
      }
    ]
  },
  {
    path: 'community',
    component: CommunityComponent
  },
  {
    path: 'community/channel',
    component: VoiceChannelComponent
  }
];
