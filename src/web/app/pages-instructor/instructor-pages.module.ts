import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';
import { InstructorHomePageComponent } from './instructor-home-page/instructor-home-page.component';

const routes: Routes = [
  {
    path: 'home',
    component: InstructorHomePageComponent,
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
 * Module for instructor pages.
 */
@NgModule({
  imports: [
    CommonModule,
    PageNotFoundModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    InstructorHomePageComponent,
  ],
})
export class InstructorPagesModule {}
