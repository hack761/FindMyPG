export interface Listing { id:number; slug:string; title:string; city:string; locality:string; landmark?:string; description?:string; monthlyRent:number; deposit:number; availableBeds:number; genderPreference:string; verified:boolean; demo:boolean; coverImage?:string; amenities:string[]; ownerName:string; averageRating:number; reviewCount:number }
export interface ListingPage { content:Listing[]; page:number; size:number; totalElements:number; totalPages:number }
export interface User { id:number; name:string; email:string; role:'TENANT'|'OWNER'|'ADMIN' }
export interface AuthResponse { user:User }
export interface Booking { id:number; listingId:number; listingTitle:string; locality:string; moveInDate:string; monthlyRent:number; deposit:number; status:string; createdAt:string }
export interface Visit { id:number; listingTitle:string; locality:string; requestedFor:string; status:string; note?:string }
export interface Conversation { id:number; listingId:number; listingTitle:string; locality:string; otherParty:string; lastMessage:string; updatedAt:string }
export interface Message { id:number; conversationId:number; body:string; senderName:string; createdAt:string }
export interface Notification { id:number; title:string; body:string; href?:string; read:boolean; createdAt:string }
export interface Report { id:number; listingId:number; listingTitle:string; reason:string; details?:string; status:string; createdAt:string; reporterName:string }
export interface AuditEvent { id:number; actor:string; action:string; entityType:string; entityId:number; detail?:string; createdAt:string }
export interface AdminAccount { id:number; name:string; role:string; enabled:boolean; createdAt:string }
