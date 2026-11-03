import { Injectable, signal } from '@angular/core';
@Injectable({providedIn:'root'})
export class ToastService {
  readonly message=signal('');private timer?:ReturnType<typeof setTimeout>;
  show(message:string){this.message.set(message);if(this.timer)clearTimeout(this.timer);this.timer=setTimeout(()=>this.message.set(''),3500)}
  dismiss(){if(this.timer)clearTimeout(this.timer);this.message.set('')}
}
