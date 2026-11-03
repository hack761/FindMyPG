import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { AuthResponse, User } from './models';
@Injectable({providedIn:'root'})
export class AuthService {
  private readonly key='findmypg.session';
  private readonly current=signal<User|null>(this.readUser());
  readonly user=this.current.asReadonly();
  constructor(private http:HttpClient,private router:Router){}
  login(email:string,password:string){return this.http.post<AuthResponse>('/api/auth/login',{email,password}).pipe(tap(result=>this.accept(result)))}
  register(name:string,email:string,password:string,role:'TENANT'|'OWNER'){return this.http.post<AuthResponse>('/api/auth/register',{name,email,password,role}).pipe(tap(result=>this.accept(result)))}
  logout(){this.http.post('/api/auth/logout',{}).subscribe({complete:()=>this.finishLogout(),error:()=>this.finishLogout()})}
  private finishLogout(){sessionStorage.removeItem(this.key+'.user');this.current.set(null);this.router.navigateByUrl('/')}
  private accept(result:AuthResponse){sessionStorage.setItem(this.key+'.user',JSON.stringify(result.user));this.current.set(result.user);try{const ids:number[]=JSON.parse(localStorage.getItem('findmypg.saved')||'[]');ids.forEach(id=>this.http.post(`/api/listings/${id}/favorite`,{}).subscribe({error:()=>{}}))}catch{}}
  private readUser():User|null{try{const raw=sessionStorage.getItem(this.key+'.user');return raw?JSON.parse(raw) as User:null}catch{return null}}
}
