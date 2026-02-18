import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app/app.component';
import { authInterceptor } from './app/auth/auth.interceptor';
import { initKeycloak } from './app/auth/keycloak';

initKeycloak()
  .then(() =>
    bootstrapApplication(AppComponent, {
      providers: [provideHttpClient(withInterceptors([authInterceptor])), provideRouter([])]
    })
  )
  .catch((err) => console.error(err));
