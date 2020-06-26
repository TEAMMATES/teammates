import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from '@angular/router';
import { ServiceWorkerModule } from '@angular/service-worker';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { environment } from '../environments/environment';
import { Intent } from '../types/api-request';
import { AppComponent } from './app.component';
import { ErrorReportModule } from './components/error-report/error-report.module';
import { LoaderBarModule } from './components/loader-bar/loader-bar.module';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';
import {
  StatusMesssageModalModule,
} from './components/status-message/status-messsage-modal/status-messsage-modal.module';
import { ToastModule } from './components/toast/toast.module';
import { ClickOutsideDirective, PageComponent } from './page.component';
import { AdminPageComponent } from './pages-admin/admin-page.component';
import { InstructorPageComponent } from './pages-instructor/instructor-page.component';
import { SessionResultPageComponent } from './pages-session/session-result-page/session-result-page.component';
import { SessionSubmissionPageComponent } from './pages-session/session-submission-page/session-submission-page.component';
import { StaticPageComponent } from './pages-static/static-page.component';
import { StudentPageComponent } from './pages-student/student-page.component';
import { PublicPageComponent } from './public-page.component';
import { UserJoinPageComponent } from './user-join-page.component';

const routes: Routes = [
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
        children: [
          {
            path: '',
            component: UserJoinPageComponent,
          },
        ],
      },
      {
        path: 'sessions',
        component: PublicPageComponent,
        children: [
          {
            path: 'result',
            component: SessionResultPageComponent,
          },
          {
            path: 'submission',
            component: SessionSubmissionPageComponent,
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

/**
 * Root module.
 */
@NgModule({
  declarations: [
    AppComponent,
    PageComponent,
    ClickOutsideDirective,
    PublicPageComponent,
    UserJoinPageComponent,
    StaticPageComponent,
    StudentPageComponent,
    InstructorPageComponent,
    AdminPageComponent,
    LoadingSpinnerComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    NgbModule,
    RouterModule.forRoot(routes),
    StatusMesssageModalModule,
    ErrorReportModule,
    ToastModule,
    LoaderBarModule,
    NgxPageScrollCoreModule.forRoot(),
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: environment.production,
      registrationStrategy: 'registerImmediately',
    }),
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
