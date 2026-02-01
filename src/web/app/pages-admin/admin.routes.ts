import { type Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';

const routes: Routes = [
  {
    path: 'home',
    loadComponent: () => import('./admin-home-page/admin-home-page.component')
      .then((m) => m.AdminHomePageComponent),
    data: {
      pageTitle: 'Add New Instructor',
    },
  },
  {
    path: 'accounts',
    loadComponent: () => import('./admin-accounts-page/admin-accounts-page.component')
      .then((m) => m.AdminAccountsPageComponent),
    data: {
      pageTitle: 'Account Details',
    },
  },
  {
    path: 'search',
    loadComponent: () => import('./admin-search-page/admin-search-page.component')
      .then((m) => m.AdminSearchPageComponent),
    data: {
      pageTitle: 'Admin Search',
    },
  },
  {
    path: 'sessions',
    loadComponent: () => import('./admin-sessions-page/admin-sessions-page.component')
      .then((m) => m.AdminSessionsPageComponent),
    data: {
      pageTitle: 'Ongoing Sessions',
    },
  },
  {
    path: 'timezone',
    loadComponent: () => import('../pages-monitoring/timezone-page/timezone-page.component')
      .then((m) => m.TimezonePageComponent),
  },
  {
    path: 'notifications',
    loadComponent: () => import('./admin-notifications-page/admin-notifications-page.component')
      .then((m) => m.AdminNotificationsPageComponent),
  },
  {
    path: 'logs',
    data: {
      isAdmin: true,
    },
    loadComponent: () => import('../pages-monitoring/logs-page/logs-page.component')
      .then((m) => m.LogsPageComponent),
  },
  {
    path: 'stats',
    loadComponent: () => import('../pages-monitoring/usage-stats-page/usage-statistics-page.component')
      .then((m) => m.UsageStatisticsPageComponent),
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
