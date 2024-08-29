import { HttpInterceptorFn } from '@angular/common/http';
import {inject} from "@angular/core";
import {AuthService} from "./auth.service";
import {KeycloakService} from "keycloak-angular";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const service = inject(KeycloakService);

  console.log(`Setting headers: ${service.getKeycloakInstance().idToken}`);

  const authReq = req.clone({
    setHeaders: {
      Authorization: 'Bearer ' + service.getKeycloakInstance().idToken
    }
  });

  return next(authReq);
};
