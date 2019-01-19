import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { HotTableModule } from '@handsontable/angular';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';

import { InstructorHelpPageComponent } from '../pages-help/instructor-help-page/instructor-help-page.component';
import { InstructorHelpPageModule } from '../pages-help/instructor-help-page/instructor-help-page.module';
import {
  SessionSubmissionPageComponent,
} from '../pages-session/session-submission-page/session-submission-page.component';
import { SessionSubmissionPageModule } from '../pages-session/session-submission-page/session-submission-page.module';
import {
  InstructorCourseDetailsPageComponent,
} from './instructor-course-details-page/instructor-course-details-page.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page/instructor-course-edit-page.component';
import {
  InstructorCourseEnrollPageComponent,
} from './instructor-course-enroll-page/instructor-course-enroll-page.component';
import {
  InstructorCourseStudentDetailsPageComponent,
} from './instructor-course-student-details-page/instructor-course-student-details-page.component';
import {
  InstructorCourseStudentEditPageComponent,
} from './instructor-course-student-edit-page/instructor-course-student-edit-page.component';
import { InstructorCoursesPageComponent } from './instructor-courses-page/instructor-courses-page.component';
import { InstructorHomePageComponent } from './instructor-home-page/instructor-home-page.component';
import { InstructorSearchPageComponent } from './instructor-search-page/instructor-search-page.component';
import {
  InstructorSessionEditPageComponent,
} from './instructor-session-edit-page/instructor-session-edit-page.component';
import { InstructorSessionEditPageModule } from './instructor-session-edit-page/instructor-session-edit-page.module';
import { InstructorSessionsPageComponent } from './instructor-sessions-page/instructor-sessions-page.component';
import { InstructorSessionsPageModule } from './instructor-sessions-page/instructor-sessions-page.module';
import {
  InstructorSessionsResultPageComponent,
} from './instructor-sessions-result-page/instructor-sessions-result-page.component';
import {
  InstructorStudentListPageComponent,
} from './instructor-student-list-page/instructor-student-list-page.component';
import {
  InstructorStudentRecordsPageComponent,
} from './instructor-student-records-page/instructor-student-records-page.component';
import { StudentListComponent } from './student-list/student-list.component';
import { StudentProfileComponent } from './student-profile/student-profile.component';

import { StatusMessageModule } from '../components/status-message/status-message.module';

const routes: Routes = [
  {
    path: 'home',
    component: InstructorHomePageComponent,
  },
  {
    path: 'courses',
    children: [
      {
        path: '',
        component: InstructorCoursesPageComponent,
      },
      {
        path: 'edit',
        component: InstructorCourseEditPageComponent,
      },
      {
        path: 'details',
        component: InstructorCourseDetailsPageComponent,
      },
      {
        path: 'enroll',
        component: InstructorCourseEnrollPageComponent,
      },
      {
        path: 'student',
        children: [
          {
            path: 'details',
            component: InstructorCourseStudentDetailsPageComponent,
          },
          {
            path: 'edit',
            component: InstructorCourseStudentEditPageComponent,
          },
        ],
      },
    ],
  },
  {
    path: 'sessions',
    children: [
      {
        path: '',
        component: InstructorSessionsPageComponent,
      },
      {
        path: 'edit',
        component: InstructorSessionEditPageComponent,
        data: {
          pageTitle: 'Edit Feedback Session',
        },
      },
      {
        path: 'submission',
        component: SessionSubmissionPageComponent,
      },
      {
        path: 'result',
        component: InstructorSessionsResultPageComponent,
      },
    ],
  },
  {
    path: 'students',
    children: [
      {
        path: '',
        component: InstructorStudentListPageComponent,
      },
      {
        path: 'records',
        component: InstructorStudentRecordsPageComponent,
      },
    ],
  },
  {
    path: 'search',
    component: InstructorSearchPageComponent,
    data: {
      pageTitle: 'Search',
    },
  },
  {
    path: 'help',
    component: InstructorHelpPageComponent,
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
 * Module for instructor pages.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    ReactiveFormsModule,
    PageNotFoundModule,
    InstructorSessionEditPageModule,
    InstructorSessionsPageModule,
    InstructorHelpPageModule,
    SessionSubmissionPageModule,
    StatusMessageModule,
    HotTableModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    InstructorHomePageComponent,
    InstructorSearchPageComponent,
    InstructorSessionsResultPageComponent,
    InstructorStudentListPageComponent,
    InstructorStudentRecordsPageComponent,
    InstructorCoursesPageComponent,
    InstructorCourseDetailsPageComponent,
    InstructorCourseEditPageComponent,
    InstructorCourseEnrollPageComponent,
    InstructorCourseStudentEditPageComponent,
    InstructorCourseStudentDetailsPageComponent,
    StudentListComponent,
    StudentProfileComponent,
  ],
})
export class InstructorPagesModule {}
