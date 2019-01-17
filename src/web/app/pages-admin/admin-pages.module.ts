import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';
import { AdminAccountsPageComponent } from './admin-accounts-page/admin-accounts-page.component';
import { AdminHomePageComponent } from './admin-home-page/admin-home-page.component';
import { AdminSearchPageComponent } from './admin-search-page/admin-search-page.component';
import { AdminSessionsPageComponent } from './admin-sessions-page/admin-sessions-page.component';
import { AdminTimezonePageComponent } from './admin-timezone-page/admin-timezone-page.component';

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
    FormsModule,
    NgbModule,
    PageNotFoundModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    AdminAccountsPageComponent,
    AdminHomePageComponent,
    AdminSearchPageComponent,
    AdminSessionsPageComponent,
    AdminTimezonePageComponent,
  ],
})
export class AdminPagesModule {}
