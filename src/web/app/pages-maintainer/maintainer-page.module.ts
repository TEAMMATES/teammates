import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';
import { LogsPageModule } from '../pages-logs/logs-page.module';

const routes: Routes = [
  {
    path: 'home',
    loadChildren: () => import('../pages-logs/logs-page.module')
        .then((m: any) => m.LogsPageModule),
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
    LogsPageModule,
  ],
})

export class MaintainerPageModule {}
