import { Component, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';
import { ToastService } from './core/toast.service';
@Component({selector:'findmypg-root',standalone:true,imports:[RouterOutlet,RouterLink],template:`
<header class="site-header"><div class="topbar"><a class="brand" routerLink="/" aria-label="FindMyPG home"><span class="brand-mark">f</span>findmy<span class="brand-light">pg</span></a>
<nav class="desktop-nav" aria-label="Main navigation"><a routerLink="/search">Find a stay</a><a routerLink="/search" [queryParams]="{verified:true}">Verified homes</a><a routerLink="/" fragment="how-it-works">How it works</a>@if(auth.user()){<a routerLink="/inbox">Inbox</a>}</nav>
<div class="header-actions"><a class="owner-link" routerLink="/owner">List your place <span aria-hidden="true">↗</span></a>@if(auth.user(); as user){<a class="sign-in" routerLink="/dashboard">Hi, {{user.name.split(' ')[0]}}</a><button class="icon-button" (click)="auth.logout()" aria-label="Sign out">↗</button>}@else{<a class="sign-in" routerLink="/login">Sign in</a>}<button class="menu-toggle" (click)="menu.update(v=>!v)" [attr.aria-expanded]="menu()" aria-label="Toggle navigation">☰</button></div>
@if(menu()){<nav class="mobile-nav"><a routerLink="/search" (click)="menu.set(false)">Find a stay</a><a routerLink="/owner" (click)="menu.set(false)">List your place</a><a routerLink="/dashboard" (click)="menu.set(false)">My dashboard</a><a routerLink="/inbox" (click)="menu.set(false)">Inbox & notifications</a></nav>}
</div></header>
<main><router-outlet/></main>
<footer class="site-footer"><div class="footer-inner"><a class="brand brand-footer" routerLink="/"><span class="brand-mark">f</span>findmy<span class="brand-light">pg</span></a><p>Thoughtful stays for wherever life takes you.</p><div class="footer-links"><a routerLink="/search">Explore stays</a><a routerLink="/owner">For owners</a><a routerLink="/support">Support</a><a routerLink="/legal/privacy">Privacy</a><a routerLink="/legal/terms">Terms</a></div><small>© 2026 FindMyPG. Demo listings are clearly marked.</small></div></footer>
<div class="toast-region" aria-live="polite">@if(toast.message()){<div class="toast-message" role="status"><span>{{toast.message()}}</span><button (click)="toast.dismiss()" aria-label="Dismiss notification">×</button></div>}</div>`})
export class AppComponent { readonly menu=signal(false);constructor(public auth:AuthService,public toast:ToastService){} }
