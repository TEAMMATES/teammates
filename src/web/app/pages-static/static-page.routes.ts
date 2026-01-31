import { type Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';

const routes: Routes = [
  {
    path: 'home',
    loadComponent: () => import('./index-page/index-page.component').then((m: any) => m.IndexPageComponent),
  },
  {
    path: 'request',
    loadComponent: () => import('./request-page/request-page.component').then((m: any) => m.RequestPageComponent),
  },
  {
    path: 'features',
    loadComponent: () => import('./features-page/features-page.component').then((m: any) => m.FeaturesPageComponent),
  },
  {
    path: 'about',
    loadComponent: () => import('./about-page/about-page.component').then((m: any) => m.AboutPageComponent),
  },
  {
    path: 'contact',
    loadComponent: () => import('./contact-page/contact-page.component').then((m: any) => m.ContactPageComponent),
  },
  {
    path: 'terms',
    loadComponent: () => import('./terms-page/terms-page.component').then((m: any) => m.TermsPageComponent),
  },
  {
    path: 'help',
    children: [
      {
        path: 'student',
        loadComponent: () =>
          import('../pages-help/student-help-page/student-help-page.component').then(
            (m: any) => m.StudentHelpPageComponent,
          ),
      },
      {
        path: 'instructor',
        loadComponent: () =>
          import('../pages-help/instructor-help-page/instructor-help-page.component').then(
            (m: any) => m.InstructorHelpPageComponent,
          ),
        data: {
          instructorGettingStartedPath: '/web/front/help/getting-started',
        },
      },
      {
        path: 'getting-started',
        loadComponent: () =>
          import('../pages-help/instructor-help-page/instructor-help-getting-started/instructor-help-getting-started.component').then((m: any) => m.InstructorHelpGettingStartedComponent),
        data: {
          instructorHelpPath: '/web/front/help/instructor',
        },
      },
      {
        path: 'session-links-recovery',
        loadComponent: () =>
          import('../pages-help/session-links-recovery/session-links-recovery-page.component').then(
            (m: any) => m.SessionLinksRecoveryPageComponent,
          ),
      },
    ],
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
