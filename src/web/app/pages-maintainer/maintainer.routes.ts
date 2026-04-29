import { type Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';

const routes: Routes = [
  {
    path: 'timezone',
    loadComponent: () =>
      import('../pages-monitoring/timezone-page/timezone-page.component').then((m) => m.TimezonePageComponent),
  },
  {
    path: 'home',
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
