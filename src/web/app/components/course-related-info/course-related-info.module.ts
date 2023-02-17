import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { CourseRelatedInfoComponent } from './course-related-info.component';

@NgModule({
  declarations: [
    CourseRelatedInfoComponent,
  ],
  exports: [
    CourseRelatedInfoComponent,
  ],
  imports: [
    CommonModule,
    TeammatesRouterModule,
  ],
})
export class CourseRelatedInfoModule { }
