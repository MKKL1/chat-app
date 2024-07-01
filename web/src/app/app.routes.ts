import { Routes } from '@angular/router';
import {LoginComponent} from "./features/pages/login/login.component";
import {RegisterComponent} from "./features/pages/register/register.component";
import {CommunityComponent} from "./features/pages/community/community.component";
import {ChannelComponent} from "./features/pages/channel/channel.component";
import {ProfileComponent} from "./features/pages/profile/profile.component";
import {MainComponent} from "./features/pages/main/main.component";

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
        component: ChannelComponent
      },
      {
        path: 'communities/voice',
        component: ChannelComponent
      }
    ]
  },
  {
    path: 'community',
    component: CommunityComponent
  },
  {
    path: 'community/channel',
    component: ChannelComponent
  }
];
