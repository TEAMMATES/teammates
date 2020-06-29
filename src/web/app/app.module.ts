import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from '@angular/router';
import { ServiceWorkerModule } from '@angular/service-worker';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { environment } from '../environments/environment';
import { AppComponent } from './app.component';
import {
  StatusMesssageModalModule,
} from './components/status-message/status-messsage-modal/status-messsage-modal.module';

const routes: Routes = [
  {
    path: 'web',
    loadChildren: () => import('./pages.module').then((m: any) => m.PagesModule),
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
    HttpClientModule,
    NgbModule,
    RouterModule.forRoot(routes),
    StatusMesssageModalModule,
    NgxPageScrollCoreModule.forRoot(),
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: environment.production,
      registrationStrategy: 'registerImmediately',
    }),
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
