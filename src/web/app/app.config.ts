import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { provideProtractorTestingSupport } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { NgbDropdownModule, NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import routes from './app.routes';
import { customUrlSerializerProvider } from './providers/custom-url-serializer';

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(NgbDropdownModule, NgxPageScrollCoreModule.forRoot(), FormsModule, NgbDatepickerModule),
    customUrlSerializerProvider,
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimations(),
    provideZoneChangeDetection(),
    provideProtractorTestingSupport(),
    provideRouter(routes),
  ],
};
