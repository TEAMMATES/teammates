import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';
import { AdminAccountsPageComponent } from './admin-accounts-page/admin-accounts-page.component';
import { AdminAccountsPageModule } from './admin-accounts-page/admin-accounts-page.module';
import { AdminHomePageComponent } from './admin-home-page/admin-home-page.component';
import { AdminHomePageModule } from './admin-home-page/admin-home-page.module';
import { AdminSearchPageComponent } from './admin-search-page/admin-search-page.component';
import { AdminSearchPageModule } from './admin-search-page/admin-search-page.module';
import { AdminSessionsPageComponent } from './admin-sessions-page/admin-sessions-page.component';
import { AdminSessionsPageModule } from './admin-sessions-page/admin-sessions-page.module';
import { AdminTimezonePageComponent } from './admin-timezone-page/admin-timezone-page.component';
import { AdminTimezonePageModule } from './admin-timezone-page/admin-timezone-page.module';

const routes: Routes = [
  {
    path: 'home',
    component: AdminHomePageComponent,
    data: {
      pageTitle: 'Add New Instructor',
    },
  },
  {
    path: 'accounts',
    component: AdminAccountsPageComponent,
    data: {
      pageTitle: 'Account Details',
    },
  },
  {
    path: 'search',
    component: AdminSearchPageComponent,
    data: {
      pageTitle: 'Admin Search',
    },
  },
  {
    path: 'sessions',
    component: AdminSessionsPageComponent,
    data: {
      pageTitle: 'Ongoing Sessions',
    },
  },
  {
    path: 'timezone',
    component: AdminTimezonePageComponent,
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
    AdminAccountsPageModule,
    AdminHomePageModule,
    AdminSearchPageModule,
    AdminSessionsPageModule,
    AdminTimezonePageModule,
    PageNotFoundModule,
    RouterModule.forChild(routes),
  ],
})
export class AdminPagesModule {}
