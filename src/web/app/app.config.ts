import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { provideProtractorTestingSupport } from '@angular/platform-browser';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap/dropdown';
import routes from './app.routes';
import { customUrlSerializerProvider } from './providers/custom-url-serializer';

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(NgbDropdownModule, FormsModule, NgbDatepickerModule),
    customUrlSerializerProvider,
    provideHttpClient(withInterceptorsFromDi()),
    provideZoneChangeDetection(),
    provideProtractorTestingSupport(),
    provideRouter(routes, withComponentInputBinding()),
  ],
};
