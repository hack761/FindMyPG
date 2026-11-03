import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { Listing } from '../core/models';
import { AuthService } from '../core/auth.service';
import { MarketplaceService } from '../core/marketplace.service';
import { ToastService } from '../core/toast.service';
@Component({selector:'app-listing-card',standalone:true,imports:[RouterLink,DecimalPipe],template:`
<article class="listing-card"><div class="listing-image"><a [routerLink]="['/stay',item.slug]" [attr.aria-label]="'View '+item.title"><img [src]="item.coverImage||fallback" [alt]="item.title + ' shared accommodation'" loading="lazy"></a>@if(item.verified){<span class="verified-pill">✓ Reviewed</span>}@if(item.demo){<span class="demo-pill">Demo</span>}<button class="save-button" (click)="save($event)" [attr.aria-label]="saved?'Remove from saved stays':'Save this stay'" [class.is-saved]="saved">{{saved?'♥':'♡'}}</button></div>
<div class="listing-body"><div class="listing-loc">{{item.locality}}, {{item.city}} <span class="rating">★ {{item.averageRating?item.averageRating.toFixed(1):'New'}} <small>({{item.reviewCount}})</small></span></div><a class="listing-title" [routerLink]="['/stay',item.slug]">{{item.title}}</a><div class="listing-meta">{{item.genderPreference}} · {{item.availableBeds}} beds available</div><div class="listing-amenities">@for(tag of item.amenities.slice(0,3);track tag){<span>{{tag}}</span>}</div><div class="listing-price"><strong>₹{{item.monthlyRent|number}}</strong><span> / bed · month</span><a [routerLink]="['/stay',item.slug]" aria-label="View stay details">↗</a></div></div></article>
`})
export class ListingCard implements OnChanges {
  @Input({required:true}) item!:Listing;
  readonly fallback='https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=900&q=80';
  saved=false;private key='findmypg.saved';
  constructor(private auth:AuthService,private api:MarketplaceService,private toast:ToastService){}
  ngOnChanges(changes:SimpleChanges){if(changes['item'])try{this.saved=JSON.parse(localStorage.getItem(this.key)||'[]').includes(this.item.id)}catch{this.saved=false}}
  save(event:Event){event.preventDefault();event.stopPropagation();const next=!this.saved;if(this.auth.user()){const request=next?this.api.save(this.item.id):this.api.unsave(this.item.id);request.subscribe({next:()=>{this.saved=next;this.persist(next);this.toast.show(next?'Stay saved.':'Stay removed from saved stays.')},error:()=>this.toast.show('Could not update saved stays. Please try again.')})}else{this.saved=next;this.persist(next);this.toast.show(next?'Saved on this device. Sign in to sync your shortlist.':'Removed from saved stays.')}}
  private persist(saved:boolean){try{const ids:number[]=JSON.parse(localStorage.getItem(this.key)||'[]');localStorage.setItem(this.key,JSON.stringify(saved?[...new Set([...ids,this.item.id])]:ids.filter(id=>id!==this.item.id)))}catch{}}
}
