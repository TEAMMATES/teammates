import { Provider, importProvidersFrom } from '@angular/core';
import { UrlSerializer, provideRouter, Routes } from '@angular/router';
import { CustomUrlSerializer } from './app/custom-url-serializer';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { BrowserModule, bootstrapApplication } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { NgbDropdownModule, NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { environment } from './environments/environment';
import { PublicPageComponent } from './app/public-page.component';
import { StaticPageComponent } from './app/pages-static/static-page.component';
import { Intent } from './types/api-request';
import { StudentPageComponent } from './app/pages-student/student-page.component';
import { InstructorPageComponent } from './app/pages-instructor/instructor-page.component';
import { AdminPageComponent } from './app/pages-admin/admin-page.component';
import { MaintainerPageComponent } from './app/pages-maintainer/maintainer-page.component';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { ServiceWorkerModule } from '@angular/service-worker';
import { FormsModule } from '@angular/forms';
import { SessionEditFormModule } from './app/components/session-edit-form/session-edit-form.module';
import { AngularFireModule } from '@angular/fire/compat';
import { AngularFireAuthModule } from '@angular/fire/compat/auth';
import { AppComponent } from './app/app.component';

const customUrlSerializer: CustomUrlSerializer = new CustomUrlSerializer();
const customUrlSerializerProvider: Provider = {
  provide: UrlSerializer,
  useValue: customUrlSerializer,
};

let routes: Routes = [
  {
    path: 'web',
    children: [
      {
        path: 'front',
        component: StaticPageComponent,
        loadChildren: () => import("./app/pages-static/static-pages.module").then((m: any) => m.StaticPagesModule),
      },
      {
        path: 'join',
        component: PublicPageComponent,
        loadChildren: () => import("./app/user-join-page.module").then((m: any) => m.UserJoinPageModule),
      },
      {
        path: 'login',
        component: PublicPageComponent,
        loadChildren: () => import("./app/login-page.module").then((m: any) => m.LoginPageModule),
      },
      {
        path: 'sessions',
        component: PublicPageComponent,
        children: [
          {
            path: 'result',
            loadChildren: () => import("./app/pages-session/session-result-page/session-result-page.module")
              .then((m: any) => m.SessionResultPageModule),
            data: {
              intent: Intent.STUDENT_RESULT,
            },
          },
          {
            path: 'submission',
            loadChildren: () => import("./app/pages-session/session-submission-page/session-submission-page.module")
              .then((m: any) => m.SessionSubmissionPageModule),
            data: {
              pageTitle: 'Submit Feedback',
              intent: Intent.STUDENT_SUBMISSION,
            },
          },
        ],
      },
      {
        path: 'student',
        component: StudentPageComponent,
        loadChildren: () => import("./app/pages-student/student-pages.module").then((m: any) => m.StudentPagesModule),
      },
      {
        path: 'instructor',
        component: InstructorPageComponent,
        loadChildren: () => import("./app/pages-instructor/instructor-pages.module")
          .then((m: any) => m.InstructorPagesModule),
      },
      {
        path: 'admin',
        component: AdminPageComponent,
        loadChildren: () => import("./app/pages-admin/admin-pages.module").then((m: any) => m.AdminPagesModule),
      },
      {
        path: 'maintainer',
        component: MaintainerPageComponent,
        loadChildren: () => import("./app/pages-maintainer/maintainer-page.module")
          .then((m: any) => m.MaintainerPageModule),
      },
      {
        path: '**',
        pathMatch: 'full',
        redirectTo: 'front',
      },
    ],
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'web',
  },
];

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(
      BrowserModule,
      NgbDropdownModule,
      NgxPageScrollCoreModule.forRoot(),
      ServiceWorkerModule.register("ngsw-worker.js", {
        enabled: environment.production,
        registrationStrategy: "registerImmediately",
      }),
      FormsModule,
      NgbDatepickerModule,
      SessionEditFormModule,
      environment.allowFirebaseLogin && environment.firebaseConfig?.projectId
        ? AngularFireModule.initializeApp(environment.firebaseConfig)
        : [], 
      environment.allowFirebaseLogin && environment.firebaseConfig?.projectId
        ? AngularFireAuthModule
        : []),
    customUrlSerializerProvider,
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimations(),
    provideRouter(routes),
  ]
}).catch((err: any) => console.error(err));
