import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { provideProtractorTestingSupport } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { ServiceWorkerModule } from '@angular/service-worker';
import { NgbDropdownModule, NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import routes from './app.routes';
import { environment } from '../environments/environment';
import { customUrlSerializerProvider } from './providers/custom-url-serializer';

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(
      NgbDropdownModule,
      NgxPageScrollCoreModule.forRoot(),
      ServiceWorkerModule.register('ngsw-worker.js', {
        enabled: environment.production,
        registrationStrategy: 'registerImmediately',
      }),
      FormsModule,
      NgbDatepickerModule,
    ),
    customUrlSerializerProvider,
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimations(),
    provideProtractorTestingSupport(),
    provideRouter(routes),
  ],
};
