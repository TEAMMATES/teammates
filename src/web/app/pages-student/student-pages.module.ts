import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';
import { StudentHomePageComponent } from './student-home-page/student-home-page.component';

const routes: Routes = [
  {
    path: 'home',
    component: StudentHomePageComponent,
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
 * Module for student pages.
 */
@NgModule({
  imports: [
    CommonModule,
    PageNotFoundModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    StudentHomePageComponent,
  ],
})
export class StudentPagesModule {}
