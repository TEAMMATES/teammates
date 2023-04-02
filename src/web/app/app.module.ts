import { HttpClientModule } from '@angular/common/http';
import { NgModule, Provider } from '@angular/core';
import { AngularFireModule } from '@angular/fire/compat';
import { AngularFireAuthModule } from '@angular/fire/compat/auth';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes, UrlSerializer } from '@angular/router';
import { ServiceWorkerModule } from '@angular/service-worker';
import { NgbDatepickerModule, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { environment } from '../environments/environment';
import { Intent } from '../types/api-request';
import { AppComponent } from './app.component';
import { ErrorReportModule } from './components/error-report/error-report.module';
import { LoaderBarModule } from './components/loader-bar/loader-bar.module';
import { LoadingSpinnerModule } from './components/loading-spinner/loading-spinner.module';
import { NotificationBannerModule } from './components/notification-banner/notification-banner.module';
import { SessionEditFormModule } from './components/session-edit-form/session-edit-form.module';
import { SimpleModalModule } from './components/simple-modal/simple-modal.module';
import { TeammatesRouterModule } from './components/teammates-router/teammates-router.module';
import { ToastModule } from './components/toast/toast.module';
import { CustomUrlSerializer } from './custom-url-serializer';
import { MaintenancePageComponent } from './maintenance-page.component';
import { ClickOutsideDirective, PageComponent } from './page.component';
import { AdminPageComponent } from './pages-admin/admin-page.component';
import { InstructorPageComponent } from './pages-instructor/instructor-page.component';
import { MaintainerPageComponent } from './pages-maintainer/maintainer-page.component';
import { StaticPageComponent } from './pages-static/static-page.component';
import { StudentPageComponent } from './pages-student/student-page.component';
import { PublicPageComponent } from './public-page.component';

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
        loadChildren: () => import('./pages-static/static-pages.module').then((m: any) => m.StaticPagesModule),
      },
      {
        path: 'join',
        component: PublicPageComponent,
        loadChildren: () => import('./user-join-page.module').then((m: any) => m.UserJoinPageModule),
      },
      {
        path: 'login',
        component: PublicPageComponent,
        loadChildren: () => import('./login-page.module').then((m: any) => m.LoginPageModule),
      },
      {
        path: 'sessions',
        component: PublicPageComponent,
        children: [
          {
            path: 'result',
            loadChildren: () => import('./pages-session/session-result-page/session-result-page.module')
                .then((m: any) => m.SessionResultPageModule),
            data: {
              intent: Intent.STUDENT_RESULT,
            },
          },
          {
            path: 'submission',
            loadChildren: () => import('./pages-session/session-submission-page/session-submission-page.module')
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
        loadChildren: () => import('./pages-student/student-pages.module').then((m: any) => m.StudentPagesModule),
      },
      {
        path: 'instructor',
        component: InstructorPageComponent,
        loadChildren: () => import('./pages-instructor/instructor-pages.module')
            .then((m: any) => m.InstructorPagesModule),
      },
      {
        path: 'admin',
        component: AdminPageComponent,
        loadChildren: () => import('./pages-admin/admin-pages.module').then((m: any) => m.AdminPagesModule),
      },
      {
        path: 'maintainer',
        component: MaintainerPageComponent,
        loadChildren: () => import('./pages-maintainer/maintainer-page.module')
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

if (environment.maintenance) {
  routes = [
    {
      path: 'web',
      component: PublicPageComponent,
      children: [
        {
          path: '',
          component: MaintenancePageComponent,
        },
        {
          path: '**',
          pathMatch: 'full',
          redirectTo: '',
        },
      ],
    },
    {
      path: '**',
      pathMatch: 'full',
      redirectTo: 'web',
    },
  ];
}

/**
 * Root module.
 */
@NgModule({
  declarations: [
    AppComponent,
    PageComponent,
    ClickOutsideDirective,
    PublicPageComponent,
    StaticPageComponent,
    StudentPageComponent,
    InstructorPageComponent,
    AdminPageComponent,
    MaintenancePageComponent,
    MaintainerPageComponent,
  ],
  imports: [
    SimpleModalModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    NgbDropdownModule,
    RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' }),
    ErrorReportModule,
    ToastModule,
    LoaderBarModule,
    NotificationBannerModule,
    NgxPageScrollCoreModule.forRoot(),
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: environment.production,
      registrationStrategy: 'registerImmediately',
    }),
    LoadingSpinnerModule,
    TeammatesRouterModule,
    FormsModule,
    NgbDatepickerModule,
    SessionEditFormModule,
    environment.allowFirebaseLogin && environment.firebaseConfig?.projectId
        ? AngularFireModule.initializeApp(environment.firebaseConfig) : [],
    environment.allowFirebaseLogin && environment.firebaseConfig?.projectId
        ? AngularFireAuthModule : [],
  ],
  providers: [customUrlSerializerProvider],
  bootstrap: [AppComponent],
})
export class AppModule {}
