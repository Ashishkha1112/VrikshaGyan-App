import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class interceptorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const myToken = localStorage.getItem('jwtToken');
    if (myToken && !req.url.startsWith('http://localhost:8080/resources/static/**' )){
      const clonereq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${myToken}`
        }
      });
      return next.handle(clonereq);
    } else {
      // If there is no token, forward the original request without modifying it
      return next.handle(req);
    }
  }
}
