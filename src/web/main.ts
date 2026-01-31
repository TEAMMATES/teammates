import { Provider, importProvidersFrom } from '@angular/core';
import { UrlSerializer, provideRouter } from '@angular/router';
import { CustomUrlSerializer } from './app/custom-url-serializer';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { bootstrapApplication, provideProtractorTestingSupport } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { NgbDropdownModule, NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { environment } from './environments/environment';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { ServiceWorkerModule } from '@angular/service-worker';
import { FormsModule } from '@angular/forms';
import { AngularFireModule } from '@angular/fire/compat';
import { AngularFireAuthModule } from '@angular/fire/compat/auth';
import { AppComponent } from './app/app.component';
import routes from './app/app.routes';

const customUrlSerializer: CustomUrlSerializer = new CustomUrlSerializer();
const customUrlSerializerProvider: Provider = {
  provide: UrlSerializer,
  useValue: customUrlSerializer,
};

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(
      NgbDropdownModule,
      NgxPageScrollCoreModule.forRoot(),
      ServiceWorkerModule.register("ngsw-worker.js", {
        enabled: environment.production,
        registrationStrategy: "registerImmediately",
      }),
      FormsModule,
      NgbDatepickerModule,
      environment.allowFirebaseLogin && environment.firebaseConfig?.projectId
        ? AngularFireModule.initializeApp(environment.firebaseConfig)
        : [], 
      environment.allowFirebaseLogin && environment.firebaseConfig?.projectId
        ? AngularFireAuthModule
        : []),
    customUrlSerializerProvider,
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimations(),
    provideProtractorTestingSupport(),
    provideRouter(routes),
  ]
}).catch((err: any) => console.error(err));
