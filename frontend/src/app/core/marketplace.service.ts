import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AdminAccount, AuditEvent, Booking, Conversation, Listing, ListingPage, Message, Notification, Report, Visit } from './models';
@Injectable({providedIn:'root'})
export class MarketplaceService {
  constructor(private http:HttpClient){}
  search(filters:Record<string,string|number|boolean|string[]>){let params=new HttpParams();Object.entries(filters).forEach(([k,v])=>{if(Array.isArray(v))v.forEach(x=>params=params.append(k,x));else if(v!==''&&v!==false&&v!==null)params=params.set(k,String(v))});return this.http.get<ListingPage>('/api/listings',{params})}
  detail(slug:string){return this.http.get<Listing>(`/api/listings/${encodeURIComponent(slug)}`)}
  favorites(){return this.http.get<Listing[]>('/api/favorites')}
  save(id:number){return this.http.post<void>(`/api/listings/${id}/favorite`,{})}
  unsave(id:number){return this.http.delete<void>(`/api/listings/${id}/favorite`)}
  book(listingId:number,moveInDate:string){return this.http.post<Booking>('/api/bookings',{listingId,moveInDate})}
  requestVisit(listingId:number,requestedFor:string,note:string){return this.http.post<Visit>('/api/visits',{listingId,requestedFor,note})}
  bookings(){return this.http.get<Booking[]>('/api/bookings')}
  visits(){return this.http.get<Visit[]>('/api/visits')}
  ownerListings(){return this.http.get<Listing[]>('/api/owner/listings')}
  createListing(payload:unknown){return this.http.post<Listing>('/api/owner/listings',payload)}
  updateListing(id:number,payload:unknown){return this.http.put<Listing>(`/api/owner/listings/${id}`,payload)}
  ownerBookings(){return this.http.get<Booking[]>('/api/owner/bookings')}
  ownerVisits(){return this.http.get<Visit[]>('/api/owner/visits')}
  updateBooking(id:number,status:string){return this.http.patch<Booking>(`/api/owner/bookings/${id}`,null,{params:{status}})}
  updateVisit(id:number,status:string){return this.http.patch<Visit>(`/api/owner/visits/${id}`,null,{params:{status}})}
  allListings(){return this.http.get<Listing[]>('/api/admin/listings')}
  verifyListing(id:number,verified:boolean){return this.http.patch<Listing>(`/api/admin/listings/${id}/verification`,null,{params:{verified}})}
  reports(){return this.http.get<Report[]>('/api/admin/reports')}
  updateReport(id:number,status:string){return this.http.patch<Report>(`/api/admin/reports/${id}`,null,{params:{status}})}
  auditTrail(){return this.http.get<AuditEvent[]>('/api/admin/audit')}
  adminAccounts(){return this.http.get<AdminAccount[]>('/api/admin/accounts')}
  setAccountEnabled(id:number,enabled:boolean){return this.http.patch<AdminAccount>(`/api/admin/accounts/${id}/enabled`,null,{params:{enabled}})}
  uploadImage(file:File){const form=new FormData();form.append('file',file);return this.http.post<{url:string}>('/api/owner/uploads',form)}
  report(listingId:number,reason:string,details:string){return this.http.post('/api/reports',{listingId,reason,details})}
  conversations(){return this.http.get<Conversation[]>('/api/conversations')}
  messages(id:number){return this.http.get<Message[]>(`/api/conversations/${id}/messages`)}
  startConversation(listingId:number,body:string){return this.http.post<Message>('/api/conversations',{listingId,body})}
  sendMessage(id:number,listingId:number,body:string){return this.http.post<Message>(`/api/conversations/${id}/messages`,{listingId,body})}
  notifications(){return this.http.get<Notification[]>('/api/notifications')}
  markNotificationRead(id:number){return this.http.patch<void>(`/api/notifications/${id}/read`,null)}
  createReview(listingId:number,rating:number,body:string){return this.http.post('/api/reviews',{listingId,rating,body})}
}
