import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CourseRelatedInfoComponent } from './course-related-info.component';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';

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
