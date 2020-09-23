import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RedirectBannerModule } from '../../components/redirect-banner/redirect-banner.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { IndexPageComponent } from './index-page.component';

const routes: Routes = [
  {
    path: '',
    component: IndexPageComponent,
  },
];

/**
 * Module for index page.
 */
@NgModule({
  declarations: [
    IndexPageComponent,
  ],
  exports: [
    IndexPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    RedirectBannerModule,
    TeammatesRouterModule,
  ],
})
export class IndexPageModule { }
