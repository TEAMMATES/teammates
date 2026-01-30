import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserJoinPageComponent } from './user-join-page.component';

const routes: Routes = [
  {
    path: '',
    component: UserJoinPageComponent,
  },
];

/**
 * Module for user join page.
 */
@NgModule({
  exports: [
    UserJoinPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    UserJoinPageComponent,
],
})
export class UserJoinPageModule { }
