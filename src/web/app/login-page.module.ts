import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { LoadingSpinnerModule } from './components/loading-spinner/loading-spinner.module';
import { LoginPageComponent } from './login-page.component';

const routes: Routes = [
  {
    path: '',
    component: LoginPageComponent,
  },
];

/**
 * Module for login page.
 */
@NgModule({
  declarations: [
    LoginPageComponent,
  ],
  exports: [
    LoginPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    FormsModule,
  ],
})
export class LoginPageModule {}
