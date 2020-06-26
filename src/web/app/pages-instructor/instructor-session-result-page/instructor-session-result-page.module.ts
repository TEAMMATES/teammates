import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { InstructorSessionResultPageComponent } from './instructor-session-result-page.component';
import { InstructorSessionResultViewModule } from './instructor-session-result-view.module';
import { SectionTypeDescriptionPipe } from './section-type-description.pipe';

const routes: Routes = [
  {
    path: '',
    component: InstructorSessionResultPageComponent,
  },
];

/**
 * Module for instructor sessions result page.
 */
@NgModule({
  declarations: [
    InstructorSessionResultPageComponent,
    SectionTypeDescriptionPipe,
  ],
  exports: [
    InstructorSessionResultPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    TeammatesCommonModule,
    RouterModule.forChild(routes),
    InstructorSessionResultViewModule,
  ],
})
export class InstructorSessionResultPageModule { }
