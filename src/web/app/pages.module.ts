import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { PageComponent } from './page.component';
import { StaticPageComponent } from './pages-static/static-page.component';

const routes: Routes = [
  {
    path: 'front',
    component: StaticPageComponent,
    loadChildren: './pages-static/static-pages.module#StaticPagesModule',
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'front',
  },
];

/**
 * Base module for pages.
 */
@NgModule({
  imports: [
    CommonModule,
    NgbModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    StaticPageComponent,
    PageComponent,
  ],
  exports: [
    PageComponent,
  ],
})
export class PagesModule {}
