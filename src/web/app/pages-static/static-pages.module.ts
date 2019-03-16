import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';
import {
  InstructorHelpGettingStartedComponent,
} from '../pages-help/instructor-help-page/instructor-help-getting-started/instructor-help-getting-started.component';
import { InstructorHelpPageComponent } from '../pages-help/instructor-help-page/instructor-help-page.component';
import { InstructorHelpPageModule } from '../pages-help/instructor-help-page/instructor-help-page.module';
import {
  SessionLinksRecoveryPageComponent,
} from '../pages-help/session-links-recovery/session-links-recovery-page.component';
import {
  SessionLinksRecoveryPageModule,
} from '../pages-help/session-links-recovery/session-links-recovery-page.module';
import { StudentHelpPageComponent } from '../pages-help/student-help-page/student-help-page.component';
import { StudentHelpPageModule } from '../pages-help/student-help-page/student-help-page.module';
import { AboutPageComponent } from './about-page/about-page.component';
import { AboutPageModule } from './about-page/about-page.module';
import { ContactPageComponent } from './contact-page/contact-page.component';
import { ContactPageModule } from './contact-page/contact-page.module';
import { FeaturesPageComponent } from './features-page/features-page.component';
import { FeaturesPageModule } from './features-page/features-page.module';
import { IndexPageComponent } from './index-page/index-page.component';
import { IndexPageModule } from './index-page/index-page.module';
import { RequestPageComponent } from './request-page/request-page.component';
import { RequestPageModule } from './request-page/request-page.module';
import { TermsPageComponent } from './terms-page/terms-page.component';
import { TermsPageModule } from './terms-page/terms-page.module';
import { UsermapPageComponent } from './usermap-page/usermap-page.component';
import { UsermapPageModule } from './usermap-page/usermap-page.module';

const routes: Routes = [
  {
    path: 'home',
    component: IndexPageComponent,
  },
  {
    path: 'request',
    component: RequestPageComponent,
  },
  {
    path: 'features',
    component: FeaturesPageComponent,
  },
  {
    path: 'about',
    component: AboutPageComponent,
  },
  {
    path: 'contact',
    component: ContactPageComponent,
  },
  {
    path: 'terms',
    component: TermsPageComponent,
  },
  {
    path: 'usermap',
    component: UsermapPageComponent,
  },
  {
    path: 'help',
    children: [
      {
        path: 'student',
        component: StudentHelpPageComponent,
      },
      {
        path: 'instructor',
        component: InstructorHelpPageComponent,
      },
      {
        path: 'getting-started',
        component: InstructorHelpGettingStartedComponent,
        data: {
          instructorHelpPath: '/web/front/help/instructor',
        },
      },
      {
        path: 'session-links-recovery',
        component: SessionLinksRecoveryPageComponent,
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
    AboutPageModule,
    ContactPageModule,
    FeaturesPageModule,
    IndexPageModule,
    RequestPageModule,
    TermsPageModule,
    UsermapPageModule,
    StudentHelpPageModule,
    InstructorHelpPageModule,
    SessionLinksRecoveryPageModule,
    RouterModule.forChild(routes),
  ],
})
export class StaticPagesModule {}
