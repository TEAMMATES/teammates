import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';

const routes: Routes = [
  {
    path: 'home',
    loadChildren: () => import('./index-page/index-page.module').then((m: any) => m.IndexPageModule),
  },
  {
    path: 'request',
    loadChildren: () => import('./request-page/request-page.module').then((m: any) => m.RequestPageModule),
  },
  {
    path: 'features',
    loadChildren: () => import('./features-page/features-page.module').then((m: any) => m.FeaturesPageModule),
  },
  {
    path: 'about',
    loadChildren: () => import('./about-page/about-page.module').then((m: any) => m.AboutPageModule),
  },
  {
    path: 'contact',
    loadChildren: () => import('./contact-page/contact-page.module').then((m: any) => m.ContactPageModule),
  },
  {
    path: 'terms',
    loadChildren: () => import('./terms-page/terms-page.module').then((m: any) => m.TermsPageModule),
  },
  {
    path: 'usermap',
    loadChildren: () => import('./usermap-page/usermap-page.module').then((m: any) => m.UsermapPageModule),
  },
  {
    path: 'help',
    children: [
      {
        path: 'student',
        loadChildren: () => import('../pages-help/student-help-page/student-help-page.module')
            .then((m: any) => m.StudentHelpPageModule),
      },
      {
        path: 'instructor',
        loadChildren: () => import('../pages-help/instructor-help-page/instructor-help-page.module')
            .then((m: any) => m.InstructorHelpPageModule),
        data: {
          instructorGettingStartedPath: '/web/front/help/getting-started',
        },
      },
      {
        path: 'getting-started',
        loadChildren: () => import('../pages-help/instructor-help-page/instructor-help-getting-started-page.module')
            .then((m: any) => m.InstructorHelpGettingStartedPageModule),
        data: {
          instructorHelpPath: '/web/front/help/instructor',
        },
      },
      {
        path: 'session-links-recovery',
        loadChildren: () => import('../pages-help/session-links-recovery/session-links-recovery-page.module')
            .then((m: any) => m.SessionLinksRecoveryPageModule),
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

/**
 * Module for all static pages.
 */
@NgModule({
  imports: [
    CommonModule,
    PageNotFoundModule,
    RouterModule.forChild(routes),
  ],
})
export class StaticPagesModule {}
