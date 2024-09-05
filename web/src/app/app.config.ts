import {APP_INITIALIZER, ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {provideHttpClient, withInterceptors} from "@angular/common/http";
import {loggingInterceptor} from "./core/interceptors/logging.interceptor";
import {authInterceptor} from "./core/interceptors/auth.interceptor";
import {KeycloakAngularModule, KeycloakService} from "keycloak-angular";
import {environment} from "../environment";

// Using keycloack auth system:
// 1. Sign up/ sign in in keycloack form after being redirected from angular app
// 2. Get your user token (I get it from di service in angular app beacuse I don't see any other way)
// 3. Make request to spring backend (/api/users) with token and username
// 4. New account is created

function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      // TODO get config from env file
      config: {
        url: environment.keycloackUrl,
        realm: 'szampchat',
        clientId: 'angular-web',
      },
      initOptions: {
        //onLoad: 'login-required',
        checkLoginIframe: true,
        checkLoginIframeInterval: 3000,
      },
      loadUserProfileAtStartUp: true,
    });
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAnimationsAsync(),
    provideHttpClient(withInterceptors([loggingInterceptor, authInterceptor])),
    importProvidersFrom(KeycloakAngularModule),
    {
        provide: APP_INITIALIZER,
        useFactory: initializeKeycloak,
        multi: true,
        deps: [KeycloakService]
    }
]
};
