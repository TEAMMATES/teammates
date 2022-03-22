import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';

const routes: Routes = [
  {
    path: 'home',
    data: {
      isAdmin: false,
    },
    loadChildren: () => import('../pages-monitoring/logs-page/logs-page.module')
        .then((m: any) => m.LogsPageModule),
  },
  {
    path: 'timezone',
    loadChildren: () => import('../pages-monitoring/timezone-page/timezone-page.module')
        .then((m: any) => m.TimezonePageModule),
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
 * Module for maintainer pages.
 */
@NgModule({
  imports: [
    CommonModule,
    PageNotFoundModule,
    RouterModule.forChild(routes),
  ],
})

export class MaintainerPageModule {}
