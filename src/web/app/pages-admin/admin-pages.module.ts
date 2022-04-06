import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';

const routes: Routes = [
  {
    path: 'home',
    loadChildren: () => import('./admin-home-page/admin-home-page.module')
        .then((m: any) => m.AdminHomePageModule),
    data: {
      pageTitle: 'Add New Instructor',
    },
  },
  {
    path: 'accounts',
    loadChildren: () => import('./admin-accounts-page/admin-accounts-page.module')
        .then((m: any) => m.AdminAccountsPageModule),
    data: {
      pageTitle: 'Account Details',
    },
  },
  {
    path: 'search',
    loadChildren: () => import('./admin-search-page/admin-search-page.module')
        .then((m: any) => m.AdminSearchPageModule),
    data: {
      pageTitle: 'Admin Search',
    },
  },
  {
    path: 'sessions',
    loadChildren: () => import('./admin-sessions-page/admin-sessions-page.module')
        .then((m: any) => m.AdminSessionsPageModule),
    data: {
      pageTitle: 'Ongoing Sessions',
    },
  },
  {
    path: 'timezone',
    loadChildren: () => import('../pages-monitoring/timezone-page/timezone-page.module')
        .then((m: any) => m.TimezonePageModule),
  },
  {
    path: 'notifications',
    loadChildren: () => import('./admin-notifications-page/admin-notifications-page.module')
        .then((m: any) => m.AdminNotificationsPageModule),
  },
  {
    path: 'logs',
    data: {
      isAdmin: true,
    },
    loadChildren: () => import('../pages-monitoring/logs-page/logs-page.module')
        .then((m: any) => m.LogsPageModule),
  },
  {
    path: 'stats',
    loadChildren: () => import('../pages-monitoring/usage-stats-page/usage-statistics-page.module')
        .then((m: any) => m.UsageStatisticsPageModule),
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

/**
 * Module for admin pages.
 */
@NgModule({
  imports: [
    CommonModule,
    PageNotFoundModule,
    RouterModule.forChild(routes),
  ],
})
export class AdminPagesModule {}
