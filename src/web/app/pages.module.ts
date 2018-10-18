import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { PageNotFoundModule } from './page-not-found/page-not-found.module';
import { PageComponent } from './page.component';
import { AdminPageComponent } from './pages-admin/admin-page.component';
import { InstructorPageComponent } from './pages-instructor/instructor-page.component';
import { StaticPageComponent } from './pages-static/static-page.component';
import { StudentPageComponent } from './pages-student/student-page.component';

const routes: Routes = [
  {
    path: 'front',
    component: StaticPageComponent,
    loadChildren: './pages-static/static-pages.module#StaticPagesModule',
  },
  {
    path: 'student',
    component: StudentPageComponent,
    loadChildren: './pages-student/student-pages.module#StudentPagesModule',
  },
  {
    path: 'instructor',
    component: InstructorPageComponent,
    loadChildren: './pages-instructor/instructor-pages.module#InstructorPagesModule',
  },
  {
    path: 'admin',
    component: AdminPageComponent,
    loadChildren: './pages-admin/admin-pages.module#AdminPagesModule',
  },
  {
    path: '**',
    pathMatch: 'full',
    redirectTo: 'front',
  },
];

/**
 * Base module for pages.
 */
@NgModule({
  imports: [
    CommonModule,
    NgbModule,
    PageNotFoundModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    PageComponent,
    StaticPageComponent,
    StudentPageComponent,
    InstructorPageComponent,
    AdminPageComponent,
  ],
})
export class PagesModule {}
