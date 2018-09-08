import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
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
];

/**
 * Module for student pages.
 */
@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    StudentHomePageComponent,
  ],
})
export class StudentPagesModule {}
