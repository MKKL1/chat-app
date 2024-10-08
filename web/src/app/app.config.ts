import {
  APP_INITIALIZER,
  ApplicationConfig,
  ErrorHandler,
  importProvidersFrom,
  provideZoneChangeDetection
} from '@angular/core';
import {PreloadAllModules, provideRouter, withPreloading} from '@angular/router';

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {provideHttpClient, withInterceptors} from "@angular/common/http";
import {loggingInterceptor} from "./core/interceptors/logging.interceptor";
import {authInterceptor} from "./core/interceptors/auth.interceptor";
import {KeycloakAngularModule, KeycloakService} from "keycloak-angular";
import {environment} from "../environment";
import {GlobalErrorHandler} from "./core/global.error.handler";
import {numbersDeserializationInterceptor} from "./core/interceptors/numbers.deserialization.interceptor";
import {persistState} from "@datorama/akita";

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
    provideRouter(
      routes,
      withPreloading(PreloadAllModules)
      ),
    provideAnimationsAsync(),
    provideHttpClient(withInterceptors([
      numbersDeserializationInterceptor,
      loggingInterceptor,
      authInterceptor
    ])),
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
    importProvidersFrom(KeycloakAngularModule),
    {
        provide: APP_INITIALIZER,
        useFactory: initializeKeycloak,
        multi: true,
        deps: [KeycloakService]
    },

    // {
    //   provide: 'persistStorage',
    //   useValue: persistState()
    // }
]
};
