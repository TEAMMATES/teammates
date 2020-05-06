import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { MatSnackBarModule } from '@angular/material';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppComponent } from './app.component';
import {
  StatusMesssageModalModule,
} from './components/status-message/status-messsage-modal/status-messsage-modal.module';

const routes: Routes = [
  {
    path: 'web',
    loadChildren: './pages.module#PagesModule',
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'web',
  },
];

/**
 * Root module.
 */
@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatSnackBarModule,
    HttpClientModule,
    NgbModule,
    RouterModule.forRoot(routes),
    StatusMesssageModalModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
