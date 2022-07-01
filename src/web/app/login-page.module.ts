import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FirebaseUIModule } from 'firebaseui-angular';
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
    FirebaseUIModule,
  ],
})
export class LoginPageModule {}
