import { type Routes } from '@angular/router';
import { Intent } from 'src/web/types/api-request';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';

const routes: Routes = [
  {
    path: 'home',
    loadComponent: () => import('./instructor-home-page/instructor-home-page.component')
      .then((m: any) => m.InstructorHomePageComponent),
  },
  {
    path: 'courses',
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Courses',
        },
        loadComponent: () => import('./instructor-courses-page/instructor-courses-page.component')
          .then((m: any) => m.InstructorCoursesPageComponent),
      },
      {
        path: 'edit',
        loadComponent: () => import('./instructor-course-edit-page/instructor-course-edit-page.component')
          .then((m: any) => m.InstructorCourseEditPageComponent),
        data: {
          pageTitle: 'Edit Course Details',
        },
      },
      {
        path: 'details',
        loadComponent: () => import('./instructor-course-details-page/instructor-course-details-page.component')
          .then((m: any) => m.InstructorCourseDetailsPageComponent),
      },
      {
        path: 'enroll',
        loadComponent: () => import('./instructor-course-enroll-page/instructor-course-enroll-page.component')
          .then((m: any) => m.InstructorCourseEnrollPageComponent),
      },
      {
        path: 'student-activity-logs',
        loadComponent: () => import('./instructor-student-activity-logs/instructor-student-activity-logs.component')
          .then((m: any) => m.InstructorStudentActivityLogsComponent),
      },
      {
        path: 'student',
        children: [
          {
            path: 'details',
            loadComponent: () =>
              import('./instructor-course-student-details-page/instructor-course-student-details-page.component')
                .then((m: any) => m.InstructorCourseStudentDetailsPageComponent),
          },
          {
            path: 'edit',
            loadComponent: () =>
              import('./instructor-course-student-edit-page/instructor-course-student-edit-page.component')
                .then((m: any) => m.InstructorCourseStudentEditPageComponent),
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
        loadComponent: () => import('./instructor-sessions-page/instructor-sessions-page.component')
          .then((m: any) => m.InstructorSessionsPageComponent),
        data: {
          pageTitle: 'Feedback Sessions',
        },
      },
      {
        path: 'individual-extension',
        loadComponent: () =>
          import('./instructor-session-individual-extension-page/instructor-session-individual-extension-page.component')
            .then((m: any) => m.InstructorSessionIndividualExtensionPageComponent),
        data: {
          pageTitle: 'Individual Deadline Extensions',
        },
      },
      {
        path: 'edit',
        loadComponent: () => import('./instructor-session-edit-page/instructor-session-edit-page.component')
          .then((m: any) => m.InstructorSessionEditPageComponent),
        data: {
          pageTitle: 'Edit Feedback Session',
        },
      },
      {
        path: 'submission',
        loadComponent: () => import('../pages-session/session-submission-page/session-submission-page.component')
          .then((m: any) => m.SessionSubmissionPageComponent),
        data: {
          pageTitle: 'Submit Feedback',
          intent: Intent.INSTRUCTOR_SUBMISSION,
        },
      },
      {
        path: 'result',
        loadComponent: () => import('../pages-session/session-result-page/session-result-page.component')
          .then((m: any) => m.SessionResultPageComponent),
        data: {
          intent: Intent.INSTRUCTOR_RESULT,
        },
      },
      {
        path: 'report',
        loadComponent: () => import('./instructor-session-result-page/instructor-session-result-page.component')
          .then((m: any) => m.InstructorSessionResultPageComponent),
      },
    ],
  },
  {
    path: 'students',
    children: [
      {
        path: '',
        loadComponent: () => import('./instructor-student-list-page/instructor-student-list-page.component')
          .then((m: any) => m.InstructorStudentListPageComponent),
      },
      {
        path: 'records',
        loadComponent: () => import('./instructor-student-records-page/instructor-student-records-page.component')
          .then((m: any) => m.InstructorStudentRecordsPageComponent),
      },
    ],
  },
  {
    path: 'search',
    loadComponent: () => import('./instructor-search-page/instructor-search-page.component')
      .then((m: any) => m.InstructorSearchPageComponent),
    data: {
      pageTitle: 'Search Students',
    },
  },
  {
    path: 'notifications',
    loadComponent: () => import('./instructor-notifications-page/instructor-notifications-page.component')
      .then((m: any) => m.InstructorNotificationsPageComponent),
  },
  {
    path: 'help',
    loadComponent: () => import('../pages-help/instructor-help-page/instructor-help-page.component')
      .then((m: any) => m.InstructorHelpPageComponent),
    data: {
      instructorGettingStartedPath: '/web/instructor/getting-started',
    },
  },
  {
    path: 'getting-started',
    loadComponent: () => import('../pages-help/instructor-help-page/instructor-help-getting-started/instructor-help-getting-started.component')
      .then((m: any) => m.InstructorHelpGettingStartedComponent),
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

export default routes;
