import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter, withInMemoryScrolling } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { apiInterceptor } from './app/core/api.interceptor';

bootstrapApplication(AppComponent, {
  providers: [provideRouter(routes, withInMemoryScrolling({scrollPositionRestoration:'enabled'})), provideHttpClient(withInterceptors([apiInterceptor]))]
}).catch(error => console.error(error));
