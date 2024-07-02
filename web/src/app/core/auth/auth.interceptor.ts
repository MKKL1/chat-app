import { HttpInterceptorFn } from '@angular/common/http';
import {inject} from "@angular/core";
import {AuthService} from "./auth.service";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const service = inject(AuthService);

  const authReq = req.clone({
    setHeaders: {
      Authorization: 'Bearer ' + service.getToken()
    }
  });

  return next(authReq);
};
