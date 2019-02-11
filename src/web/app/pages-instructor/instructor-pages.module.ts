import { AjaxPreloadModule } from '../components/ajax-preload/ajax-preload.module';

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { HotTableModule } from '@handsontable/angular';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ClipboardModule } from 'ngx-clipboard';
import { Intent } from '../Intent';
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
import {
  InstructorHelpGettingStartedComponent,
} from '../pages-help/instructor-help-page/instructor-help-getting-started/instructor-help-getting-started.component';
import { InstructorHomePageModule } from './instructor-home-page/instructor-home-page.module';

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
        data: {
          pageTitle: 'Add New Course',
        },
        component: InstructorCoursesPageComponent,
      },
      {
        path: 'edit',
        component: InstructorCourseEditPageComponent,
        data: {
          pageTitle: 'Edit Course Details',
        },
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
        data: {
          pageTitle: 'Add New Feedback Session',
        },
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
        data: {
          pageTitle: 'Submit Feedback',
          intent: Intent.INSTRUCTOR_SUBMISSION,
        },
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
 * Module for instructor pages.
 */
@NgModule({
  imports: [
    AjaxPreloadModule,
    CommonModule,
    FormsModule,
    NgbModule,
    ReactiveFormsModule,
    PageNotFoundModule,
    InstructorHomePageModule,
    InstructorSessionEditPageModule,
    InstructorSessionsPageModule,
    InstructorHelpPageModule,
    SessionSubmissionPageModule,
    StatusMessageModule,
    HotTableModule,
    RouterModule.forChild(routes),
    ClipboardModule,
  ],
  declarations: [
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
