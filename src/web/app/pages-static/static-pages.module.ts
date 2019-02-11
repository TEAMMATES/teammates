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
import { StudentHelpPageComponent } from '../pages-help/student-help-page/student-help-page.component';
import { StudentHelpPageModule } from '../pages-help/student-help-page/student-help-page.module';
import { AboutPageComponent } from './about-page/about-page.component';
import { ContactPageComponent } from './contact-page/contact-page.component';
import { FeaturesPageComponent } from './features-page/features-page.component';
import { IndexPageComponent } from './index-page/index-page.component';
import { RequestPageComponent } from './request-page/request-page.component';
import { TermsPageComponent } from './terms-page/terms-page.component';
import { UsermapPageComponent } from './usermap-page/usermap-page.component';

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
      },
    ],
  },
  {
    path: 'getting-started',
    component: InstructorHelpGettingStartedComponent,
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
    StudentHelpPageModule,
    InstructorHelpPageModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    IndexPageComponent,
    FeaturesPageComponent,
    ContactPageComponent,
    AboutPageComponent,
    TermsPageComponent,
    RequestPageComponent,
    UsermapPageComponent,
  ],
})
export class StaticPagesModule {}
