import { HttpInterceptorFn } from '@angular/common/http';
import { from } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { getValidToken } from './keycloak';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const isGatewayCall = req.url.startsWith('http://localhost:8081') || req.url.startsWith('/');

  if (!isGatewayCall) {
    return next(req);
  }

  return from(getValidToken()).pipe(
    switchMap((token) => {
      if (!token) {
        return next(req);
      }

      return next(req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      }));
    })
  );
};
