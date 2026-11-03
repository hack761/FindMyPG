import { HttpInterceptorFn } from '@angular/common/http';
export const apiInterceptor: HttpInterceptorFn = (request,next) => {
  const base=window.location.hostname==='localhost'?'http://localhost:8080':'';
  const url=request.url.startsWith('/api/') ? `${base}${request.url}` : request.url;
  return next(request.clone({url,withCredentials:true}));
};
