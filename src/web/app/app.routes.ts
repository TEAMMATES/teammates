import { type Routes } from '@angular/router';
import { AdminPageComponent } from './pages-admin/admin-page.component';
import { InstructorPageComponent } from './pages-instructor/instructor-page.component';
import { MaintainerPageComponent } from './pages-maintainer/maintainer-page.component';
import { SessionKeyGuard } from '../route-guards/session-key.guard';
import { StaticPageComponent } from './pages-static/static-page.component';
import { StudentPageComponent } from './pages-student/student-page.component';
import { RoleGuard, UserRole } from '../route-guards/role.guard';
import { PageComponent } from './page.component';
import { SessionKeyType } from '../types/api-request';

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
        component: PageComponent,
        children: [
          {
            path: '',
            loadComponent: () => import('./user-join-page.component').then((m) => m.UserJoinPageComponent),
          },
        ],
        canActivate: [RoleGuard],
        canActivateChild: [RoleGuard],
      },
      {
        path: 'sessions',
        component: PageComponent,
        children: [
          {
            path: ':feedbackSessionId/result',
            canActivate: [SessionKeyGuard],
            loadComponent: () =>
              import('./pages-session/session-result-page/session-result-page.component').then(
                (m) => m.SessionResultPageComponent,
              ),
            data: {
              entityType: 'student',
              sessionKeyType: SessionKeyType.RESULTS,
            },
          },
          {
            path: ':feedbackSessionId/submission',
            canActivate: [SessionKeyGuard],
            loadComponent: () =>
              import('./pages-session/session-submission-page/session-submission-page.component').then(
                (m) => m.SessionSubmissionPageComponent,
              ),
            data: {
              entityType: 'student',
              sessionKeyType: SessionKeyType.SUBMISSION,
            },
          },
        ],
      },
      {
        path: 'student',
        component: StudentPageComponent,
        loadChildren: () => import('./pages-student/student.routes'),
        canActivate: [RoleGuard],
        canActivateChild: [RoleGuard],
        data: {
          role: UserRole.STUDENT,
        },
      },
      {
        path: 'instructor',
        component: InstructorPageComponent,
        loadChildren: () => import('./pages-instructor/instructor.routes'),
        canActivate: [RoleGuard],
        canActivateChild: [RoleGuard],
        data: {
          role: UserRole.INSTRUCTOR,
        },
      },
      {
        path: 'admin',
        component: AdminPageComponent,
        loadChildren: () => import('./pages-admin/admin.routes'),
        canActivate: [RoleGuard],
        canActivateChild: [RoleGuard],
        data: {
          role: UserRole.ADMIN,
        },
      },
      {
        path: 'maintainer',
        component: MaintainerPageComponent,
        loadChildren: () => import('./pages-maintainer/maintainer.routes'),
        canActivate: [RoleGuard],
        canActivateChild: [RoleGuard],
        data: {
          role: UserRole.MAINTAINER,
        },
      },
      {
        path: 'unauthorized',
        component: PageComponent,
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./page-unauthorized-warning/unauthorized-warning-page.component').then(
                (m) => m.UnauthorizedWarningPageComponent,
              ),
          },
        ],
        canActivate: [RoleGuard],
        canActivateChild: [RoleGuard],
      },
      {
        path: 'login',
        component: PageComponent,
        children: [
          {
            path: '',
            loadComponent: () => import('./page-login/login-page.component').then((m) => m.LoginPageComponent),
          },
        ],
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
