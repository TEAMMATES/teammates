import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Intent } from '../types/api-request';
import { ErrorReportModule } from './components/error-report/error-report.module';
import { LoaderBarModule } from './components/loader-bar/loader-bar.module';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';
import { StatusMessageModule } from './components/status-message/status-message.module';
import { ToastModule } from './components/toast/toast.module';
import { PageNotFoundModule } from './page-not-found/page-not-found.module';
import { ClickOutsideDirective, PageComponent } from './page.component';
import { AdminPageComponent } from './pages-admin/admin-page.component';
import { InstructorPageComponent } from './pages-instructor/instructor-page.component';
import { SessionResultPageComponent } from './pages-session/session-result-page/session-result-page.component';
import { SessionResultPageModule } from './pages-session/session-result-page/session-result-page.module';
import {
  SessionSubmissionPageComponent,
} from './pages-session/session-submission-page/session-submission-page.component';
import { SessionSubmissionPageModule } from './pages-session/session-submission-page/session-submission-page.module';
import { StaticPageComponent } from './pages-static/static-page.component';
import { StudentPageComponent } from './pages-student/student-page.component';
import { PublicPageComponent } from './public-page.component';
import { UserJoinPageComponent } from './user-join-page.component';

const routes: Routes = [
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
    loadChildren: () => import('./pages-instructor/instructor-pages.module').then((m: any) => m.InstructorPagesModule),
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
];

/**
 * Base module for pages.
 */
@NgModule({
  imports: [
    CommonModule,
    LoaderBarModule,
    NgbModule,
    ErrorReportModule,
    PageNotFoundModule,
    StatusMessageModule,
    SessionResultPageModule,
    SessionSubmissionPageModule,
    RouterModule.forChild(routes),
    ToastModule,
  ],
  declarations: [
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
})
export class PagesModule {}
