import { type Routes } from '@angular/router';
import { AdminPageComponent } from './pages-admin/admin-page.component';
import { InstructorPageComponent } from './pages-instructor/instructor-page.component';
import { MaintainerPageComponent } from './pages-maintainer/maintainer-page.component';
import { StaticPageComponent } from './pages-static/static-page.component';
import { PublicPageComponent } from './public-page.component';
import { Intent } from '../types/api-request';
import { StudentPageComponent } from './pages-student/student-page.component';

const routes: Routes = [
  {
    path: 'web',
    children: [
      {
        path: 'front',
        component: StaticPageComponent,
        loadChildren: () => import('./pages-static/static.routes'),
      },
      {
        path: 'join',
        component: PublicPageComponent,
        children: [
          {
            path: '',
            loadComponent: () => import('./user-join-page.component').then(m => m.UserJoinPageComponent),
          },
        ],
      },
      {
        path: 'login',
        component: PublicPageComponent,
        children: [
          {
            path: '',
            loadComponent: () => import('./login-page.component').then(m => m.LoginPageComponent),
          },
        ],
      },
      {
        path: 'sessions',
        component: PublicPageComponent,
        children: [
          {
            path: 'result',
            loadComponent: () => import('./pages-session/session-result-page/session-result-page.component')
              .then(m => m.SessionResultPageComponent),
            data: {
              intent: Intent.STUDENT_RESULT,
            },
          },
          {
            path: 'submission',
            loadComponent: () => import('./pages-session/session-submission-page/session-submission-page.component')
              .then(m => m.SessionSubmissionPageComponent),
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
        loadChildren: () => import('./pages-student/student.routes'),
      },
      {
        path: 'instructor',
        component: InstructorPageComponent,
        loadChildren: () => import('./pages-instructor/instructor.routes'),
      },
      {
        path: 'admin',
        component: AdminPageComponent,
        loadChildren: () => import('./pages-admin/admin.routes'),
      },
      {
        path: 'maintainer',
        component: MaintainerPageComponent,
        loadChildren: () => import('./pages-maintainer/maintainer.routes'),
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

export default routes;
