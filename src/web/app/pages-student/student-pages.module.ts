import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Intent } from '../../types/api-request';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';

const routes: Routes = [
  {
    path: 'home',
    loadChildren: () => import('./student-home-page/student-home-page.module')
        .then((m: any) => m.StudentHomePageModule),
    data: {
      pageTitle: 'Student Home',
    },
  },
  {
    path: 'course',
    loadChildren: () => import('./student-course-details-page/student-course-details-page.module')
        .then((m: any) => m.StudentCourseDetailsPageModule),
  },
  {
    path: 'sessions',
    children: [
      {
        path: 'result',
        loadChildren: () => import('../pages-session/session-result-page/session-result-page.module')
            .then((m: any) => m.SessionResultPageModule),
        data: {
          intent: Intent.STUDENT_RESULT,
        },
      },
      {
        path: 'submission',
        loadChildren: () => import('../pages-session/session-submission-page/session-submission-page.module')
            .then((m: any) => m.SessionSubmissionPageModule),
        data: {
          pageTitle: 'Submit Feedback',
          intent: Intent.STUDENT_SUBMISSION,
        },
      },
    ],
  },
  {
    path: 'notifications',
    loadChildren: () => import('./student-notifications-page/student-notifications-page.module')
        .then((m: any) => m.StudentNotificationsPageModule),
  },
  {
    path: 'help',
    loadChildren: () => import('../pages-help/student-help-page/student-help-page.module')
        .then((m: any) => m.StudentHelpPageModule),
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'home',
  },
  {
    path: '**',
    pathMatch: 'full',
    component: PageNotFoundComponent,
  },
];

/**
 * Module for student pages.
 */
@NgModule({
  imports: [
    CommonModule,
    PageNotFoundModule,
    RouterModule.forChild(routes),
  ],
})
export class StudentPagesModule {}
