import { type Routes } from '@angular/router';
import { Intent } from '../../types/api-request';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';

const routes: Routes = [
  {
    path: 'home',
    loadComponent: () => import('./student-home-page/student-home-page.component')
      .then((m) => m.StudentHomePageComponent),
    data: {
      pageTitle: 'Student Home',
    },
  },
  {
    path: 'course',
    loadComponent: () => import('./student-course-details-page/student-course-details-page.component')
      .then((m) => m.StudentCourseDetailsPageComponent),
  },
  {
    path: 'sessions',
    children: [
      {
        path: 'result',
        loadComponent: () => import('../pages-session/session-result-page/session-result-page.component')
          .then((m) => m.SessionResultPageComponent),
        data: {
          intent: Intent.STUDENT_RESULT,
        },
      },
      {
        path: 'submission',
        loadComponent: () => import('../pages-session/session-submission-page/session-submission-page.component')
          .then((m) => m.SessionSubmissionPageComponent),
        data: {
          pageTitle: 'Submit Feedback',
          intent: Intent.STUDENT_SUBMISSION,
        },
      },
    ],
  },
  {
    path: 'notifications',
    loadComponent: () => import('./student-notifications-page/student-notifications-page.component')
      .then((m) => m.StudentNotificationsPageComponent),
  },
  {
    path: 'help',
    loadComponent: () => import('../pages-help/student-help-page/student-help-page.component')
      .then((m) => m.StudentHelpPageComponent),
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

export default routes;
