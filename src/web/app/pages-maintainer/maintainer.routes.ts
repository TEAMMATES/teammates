import { type Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';

const routes: Routes = [
  {
    path: 'home',
    data: {
      isAdmin: false,
    },
    loadComponent: () => import('../pages-monitoring/logs-page/logs-page.component')
        .then((m: any) => m.LogsPageComponent),
  },
  {
    path: 'timezone',
    loadComponent: () => import('../pages-monitoring/timezone-page/timezone-page.component')
        .then((m: any) => m.TimezonePageComponent),
  },
  {
    path: 'stats',
    loadComponent: () => import('../pages-monitoring/usage-stats-page/usage-statistics-page.component')
        .then((m: any) => m.UsageStatisticsPageComponent),
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
