import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { MatSnackBarModule } from '@angular/material';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes, UrlSerializer } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppComponent } from './app.component';
import { CustomUrlSerializer } from './custom-url-serializer';

const customUrlSerializer: CustomUrlSerializer = new CustomUrlSerializer();
const customUrlSerializerProvider: any = {
  provide: UrlSerializer,
  useValue: customUrlSerializer,
};

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
  ],
  providers: [customUrlSerializerProvider],
  bootstrap: [AppComponent],
})
export class AppModule {}
