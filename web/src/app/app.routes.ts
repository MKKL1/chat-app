import { Routes } from '@angular/router';
import {LoginComponent} from "./features/pages/login/login.component";
import {RegisterComponent} from "./features/pages/register/register.component";
import {AppComponent} from "./app.component";
import {CommunityComponent} from "./features/pages/community/community.component";
import {ChannelComponent} from "./features/pages/channel/channel.component";
import {ProfileComponent} from "./features/pages/profile/profile.component";

export const routes: Routes = [
  {
    path: '',
    component: AppComponent
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
    path: 'profile',
    component: ProfileComponent
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
