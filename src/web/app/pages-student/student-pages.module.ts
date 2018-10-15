import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';
import { StudentHelpPageComponent } from '../pages-help/student-help-page/student-help-page.component';
import { StudentHelpPageModule } from '../pages-help/student-help-page/student-help-page.module';
import { StudentCourseDetailsPageComponent } from './student-course-details-page/student-course-details-page.component';
import { StudentHomePageComponent } from './student-home-page/student-home-page.component';
import { StudentProfilePageComponent } from './student-profile-page/student-profile-page.component';

const routes: Routes = [
  {
    path: 'home',
    component: StudentHomePageComponent,
  },
  {
    path: 'profile',
    component: StudentProfilePageComponent,
  },
  {
    path: 'course',
    component: StudentCourseDetailsPageComponent,
  },
  {
    path: 'help',
    component: StudentHelpPageComponent,
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
    StudentHelpPageModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    StudentHomePageComponent,
    StudentProfilePageComponent,
    StudentCourseDetailsPageComponent,
  ],
})
export class StudentPagesModule {}
