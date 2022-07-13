import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Intent } from '../../types/api-request';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';

const routes: Routes = [
  {
    path: 'home',
    loadChildren: () => import('./instructor-home-page/instructor-home-page.module')
        .then((m: any) => m.InstructorHomePageModule),
  },
  {
    path: 'courses',
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Courses',
        },
        loadChildren: () => import('./instructor-courses-page/instructor-courses-page.module')
            .then((m: any) => m.InstructorCoursesPageModule),
      },
      {
        path: 'edit',
        loadChildren: () => import('./instructor-course-edit-page/instructor-course-edit-page.module')
            .then((m: any) => m.InstructorCourseEditPageModule),
        data: {
          pageTitle: 'Edit Course Details',
        },
      },
      {
        path: 'details',
        loadChildren: () => import('./instructor-course-details-page/instructor-course-details-page.module')
            .then((m: any) => m.InstructorCourseDetailsPageModule),
      },
      {
        path: 'enroll',
        loadChildren: () => import('./instructor-course-enroll-page/instructor-course-enroll-page.module')
            .then((m: any) => m.InstructorCourseEnrollPageModule),
      },
      {
        path: 'student-activity-logs',
        loadChildren: () => import('./instructor-student-activity-logs/instructor-student-activity-logs.module')
            .then((m: any) => m.InstructorStudentActivityLogsModule),
      },
      {
        path: 'student',
        children: [
          {
            path: 'details',
            loadChildren: () =>
                import('./instructor-course-student-details-page/instructor-course-student-details-page.module')
                    .then((m: any) => m.InstructorCourseStudentDetailsPageModule),
          },
          {
            path: 'edit',
            loadChildren: () =>
                import('./instructor-course-student-edit-page/instructor-course-student-edit-page.module')
                    .then((m: any) => m.InstructorCourseStudentEditPageModule),
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
        loadChildren: () => import('./instructor-sessions-page/instructor-sessions-page.module')
            .then((m: any) => m.InstructorSessionsPageModule),
        data: {
          pageTitle: 'Feedback Sessions',
        },
      },
      {
        path: 'individual-extension',
        loadChildren: () =>
          import('./instructor-session-individual-extension-page/instructor-session-individual-extension-page.module')
            .then((m: any) => m.InstructorSessionIndividualExtensionPageModule),
        data: {
          pageTitle: 'Individual Deadline Extensions',
        },
      },
      {
        path: 'edit',
        loadChildren: () => import('./instructor-session-edit-page/instructor-session-edit-page.module')
            .then((m: any) => m.InstructorSessionEditPageModule),
        data: {
          pageTitle: 'Edit Feedback Session',
        },
      },
      {
        path: 'submission',
        loadChildren: () => import('../pages-session/session-submission-page/session-submission-page.module')
            .then((m: any) => m.SessionSubmissionPageModule),
        data: {
          pageTitle: 'Submit Feedback',
          intent: Intent.INSTRUCTOR_SUBMISSION,
        },
      },
      {
        path: 'result',
        loadChildren: () => import('../pages-session/session-result-page/session-result-page.module')
            .then((m: any) => m.SessionResultPageModule),
        data: {
          intent: Intent.INSTRUCTOR_RESULT,
        },
      },
      {
        path: 'report',
        loadChildren: () => import('./instructor-session-result-page/instructor-session-result-page.module')
            .then((m: any) => m.InstructorSessionResultPageModule),
      },
    ],
  },
  {
    path: 'students',
    children: [
      {
        path: '',
        loadChildren: () => import('./instructor-student-list-page/instructor-student-list-page.module')
            .then((m: any) => m.InstructorStudentListPageModule),
      },
      {
        path: 'records',
        loadChildren: () => import('./instructor-student-records-page/instructor-student-records-page.module')
            .then((m: any) => m.InstructorStudentRecordsPageModule),
      },
    ],
  },
  {
    path: 'search',
    loadChildren: () => import('./instructor-search-page/instructor-search-page.module')
        .then((m: any) => m.InstructorSearchPageModule),
    data: {
      pageTitle: 'Search Students',
    },
  },
  {
    path: 'notifications',
    loadChildren: () => import('./instructor-notifications-page/instructor-notifications-page.module')
        .then((m: any) => m.InstructorNotificationsPageModule),
  },
  {
    path: 'help',
    loadChildren: () => import('../pages-help/instructor-help-page/instructor-help-page.module')
        .then((m: any) => m.InstructorHelpPageModule),
    data: {
      instructorGettingStartedPath: '/web/instructor/getting-started',
    },
  },
  {
    path: 'getting-started',
    loadChildren: () => import('../pages-help/instructor-help-page/instructor-help-getting-started-page.module')
        .then((m: any) => m.InstructorHelpGettingStartedPageModule),
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
    RouterModule.forChild(routes),
  ],
})
export class InstructorPagesModule {}
