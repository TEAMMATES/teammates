import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ActivatedRouteSnapshot, RouterModule, RouterStateSnapshot, Routes } from '@angular/router';
import { Intent } from '../Intent';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';

import {
  InstructorHelpGettingStartedComponent,
} from '../pages-help/instructor-help-page/instructor-help-getting-started/instructor-help-getting-started.component';
import { InstructorHelpPageComponent } from '../pages-help/instructor-help-page/instructor-help-page.component';
import { InstructorHelpPageModule } from '../pages-help/instructor-help-page/instructor-help-page.module';
import {
  SessionSubmissionPageComponent,
} from '../pages-session/session-submission-page/session-submission-page.component';
import { SessionSubmissionPageModule } from '../pages-session/session-submission-page/session-submission-page.module';
import {
  InstructorCourseDetailsPageComponent,
} from './instructor-course-details-page/instructor-course-details-page.component';
import {
  InstructorCourseDetailsPageModule,
} from './instructor-course-details-page/instructor-course-details-page.module';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page/instructor-course-edit-page.component';
import { InstructorCourseEditPageModule } from './instructor-course-edit-page/instructor-course-edit-page.module';
import {
  InstructorCourseEnrollPageComponent,
} from './instructor-course-enroll-page/instructor-course-enroll-page.component';
import {
  InstructorCourseEnrollPageModule,
} from './instructor-course-enroll-page/instructor-course-enroll-page.module';
import {
  InstructorCourseStudentDetailsPageComponent,
} from './instructor-course-student-details-page/instructor-course-student-details-page.component';
import {
  InstructorCourseStudentDetailsPageModule,
} from './instructor-course-student-details-page/instructor-course-student-details-page.module';
import {
  InstructorCourseStudentEditPageComponent,
} from './instructor-course-student-edit-page/instructor-course-student-edit-page.component';
import {
  InstructorCourseStudentEditPageModule,
} from './instructor-course-student-edit-page/instructor-course-student-edit-page.module';
import { InstructorCoursesPageComponent } from './instructor-courses-page/instructor-courses-page.component';
import { InstructorCoursesPageModule } from './instructor-courses-page/instructor-courses-page.module';
import { InstructorHomePageComponent } from './instructor-home-page/instructor-home-page.component';
import { InstructorHomePageModule } from './instructor-home-page/instructor-home-page.module';
import { InstructorSearchPageComponent } from './instructor-search-page/instructor-search-page.component';
import { InstructorSearchPageModule } from './instructor-search-page/instructor-search-page.module';
import {
  InstructorSessionEditPageComponent,
} from './instructor-session-edit-page/instructor-session-edit-page.component';
import { InstructorSessionEditPageModule } from './instructor-session-edit-page/instructor-session-edit-page.module';
import {
  InstructorSessionResultPageComponent,
} from './instructor-session-result-page/instructor-session-result-page.component';
import {
  InstructorSessionResultPageModule,
} from './instructor-session-result-page/instructor-session-result-page.module';
import { InstructorSessionsPageComponent } from './instructor-sessions-page/instructor-sessions-page.component';
import { InstructorSessionsPageModule } from './instructor-sessions-page/instructor-sessions-page.module';
import {
  InstructorStudentListPageComponent,
} from './instructor-student-list-page/instructor-student-list-page.component';
import {
  InstructorStudentListPageModule,
} from './instructor-student-list-page/instructor-student-list-page.module';
import {
  InstructorStudentRecordsPageComponent,
} from './instructor-student-records-page/instructor-student-records-page.component';
import {
  InstructorStudentRecordsPageModule,
} from './instructor-student-records-page/instructor-student-records-page.module';

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
            data: {
              pageTitle: 'Edit Student Details',
            },
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
        component: InstructorSessionResultPageComponent,
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
    canDeactivate: ['canDeactivateHelp'],
  },
  {
    path: 'getting-started',
    component: InstructorHelpGettingStartedComponent,
    data: {
      instructorHelpPath: '/web/instructor/help',
    },
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
    PageNotFoundModule,
    InstructorCoursesPageModule,
    InstructorCourseDetailsPageModule,
    InstructorCourseEditPageModule,
    InstructorCourseEnrollPageModule,
    InstructorCourseStudentDetailsPageModule,
    InstructorCourseStudentEditPageModule,
    InstructorHomePageModule,
    InstructorSessionEditPageModule,
    InstructorSessionsPageModule,
    InstructorSearchPageModule,
    InstructorHelpPageModule,
    InstructorStudentListPageModule,
    InstructorStudentRecordsPageModule,
    InstructorSessionResultPageModule,
    SessionSubmissionPageModule,
    RouterModule.forChild(routes),
  ],
  providers: [
    {
      provide: 'canDeactivateHelp',
      useValue:  (_component: InstructorHelpPageComponent, _currentRoute: ActivatedRouteSnapshot,
        _currentState: RouterStateSnapshot, nextState: RouterStateSnapshot): boolean =>
        /^\/web\/instructor\/(home|courses|sessions|students|search|getting-started)$/.test(nextState.url),
    },
  ],
})
export class InstructorPagesModule {}
