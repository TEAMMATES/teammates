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
