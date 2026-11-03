import { Routes } from '@angular/router';
import { HomePage } from './pages/home.page';
import { SearchPage } from './pages/search.page';
import { ListingPageComponent } from './pages/listing.page';
import { AuthPage } from './pages/auth.page';
import { TenantPage } from './pages/tenant.page';
import { OwnerPage } from './pages/owner.page';
import { AdminPage } from './pages/admin.page';
import { InboxPage } from './pages/inbox.page';
import { InfoPage } from './pages/info.page';
import { AuthService } from './core/auth.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
export const routes:Routes=[
  {path:'',component:HomePage,title:'FindMyPG — a better place to land'},
  {path:'search',component:SearchPage,title:'Find a PG | FindMyPG'},
  {path:'stay/:slug',component:ListingPageComponent,title:'Stay details | FindMyPG'},
  {path:'login',component:AuthPage,data:{mode:'login'},title:'Sign in | FindMyPG'},
  {path:'signup',component:AuthPage,data:{mode:'signup'},title:'Create account | FindMyPG'},
  {path:'dashboard',component:TenantPage,canActivate:[()=>inject(AuthService).user()?true:inject(Router).createUrlTree(['/login'])],title:'Your stays | FindMyPG'},
  {path:'inbox',component:InboxPage,canActivate:[()=>inject(AuthService).user()?true:inject(Router).createUrlTree(['/login'])],title:'Inbox | FindMyPG'},
  {path:'owner',component:OwnerPage,canActivate:[()=>inject(AuthService).user()?.role==='OWNER'||inject(AuthService).user()?.role==='ADMIN'?true:inject(Router).createUrlTree(['/login'])],title:'Owner studio | FindMyPG'},
  {path:'admin',component:AdminPage,canActivate:[()=>inject(AuthService).user()?.role==='ADMIN'?true:inject(Router).createUrlTree(['/'])],title:'Moderation | FindMyPG'},
  {path:'support',component:InfoPage,data:{kind:'support'},title:'Support | FindMyPG'},
  {path:'legal/:kind',component:InfoPage,title:'FindMyPG policies'},
  {path:'**',redirectTo:''}
];
