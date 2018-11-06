import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';
import { AdminEmailPageComponent } from './admin-email-page/admin-email-page.component';
import { AdminHomePageComponent } from './admin-home-page/admin-home-page.component';
import { AdminSearchPageComponent } from './admin-search-page/admin-search-page.component';
import { AdminSessionsPageComponent } from './admin-sessions-page/admin-sessions-page.component';
import { AdminTimezonePageComponent } from './admin-timezone-page/admin-timezone-page.component';

const routes: Routes = [
  {
    path: 'home',
    component: AdminHomePageComponent,
  },
  {
    path: 'search',
    component: AdminSearchPageComponent,
  },
  {
    path: 'sessions',
    component: AdminSessionsPageComponent,
  },
  {
    path: 'email',
    component: AdminEmailPageComponent,
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
    PageNotFoundModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    AdminHomePageComponent,
    AdminSearchPageComponent,
    AdminSessionsPageComponent,
    AdminEmailPageComponent,
    AdminTimezonePageComponent,
  ],
})
export class AdminPagesModule {}
