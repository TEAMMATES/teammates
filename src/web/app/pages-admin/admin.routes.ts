import { type Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';

const routes: Routes = [
  {
    path: 'home',
    loadComponent: () => import('./admin-home-page/admin-home-page.component').then((m) => m.AdminHomePageComponent),
    data: {
      pageTitle: 'Admin Home Page',
    },
  },
  {
    path: 'accounts/:accountId',
    loadComponent: () =>
      import('./admin-accounts-page/admin-accounts-page.component').then((m) => m.AdminAccountsPageComponent),
    data: {
      pageTitle: 'Account Details',
    },
  },
  {
    path: 'search',
    loadComponent: () =>
      import('./admin-search-page/admin-search-page.component').then((m) => m.AdminSearchPageComponent),
    data: {
      pageTitle: 'Admin Search',
    },
  },
  {
    path: 'account-verification-requests',
    loadComponent: () =>
      import('./admin-account-verification-requests-page/admin-account-verification-requests-page.component').then(
        (m) => m.AdminAccountVerificationRequestsPageComponent,
      ),
    data: {
      pageTitle: 'Account Verification Requests',
    },
  },
  {
    path: 'account-verification-requests/:accountVerificationRequestId',
    loadComponent: () =>
      import('./admin-account-verification-request-page/admin-account-verification-request-page.component').then(
        (m) => m.AdminAccountVerificationRequestPageComponent,
      ),
  },
  {
    path: 'sessions',
    loadComponent: () =>
      import('./admin-sessions-page/admin-sessions-page.component').then((m) => m.AdminSessionsPageComponent),
    data: {
      pageTitle: 'Ongoing Sessions',
    },
  },
  {
    path: 'timezone',
    loadComponent: () =>
      import('../pages-monitoring/timezone-page/timezone-page.component').then((m) => m.TimezonePageComponent),
  },
  {
    path: 'notifications',
    loadComponent: () =>
      import('./admin-notifications-page/admin-notifications-page.component').then(
        (m) => m.AdminNotificationsPageComponent,
      ),
  },
  {
    path: 'stats',
    loadComponent: () =>
      import('../pages-monitoring/usage-stats-page/usage-statistics-page.component').then(
        (m) => m.UsageStatisticsPageComponent,
      ),
    data: {
      pageTitle: 'Usage Statistics',
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
