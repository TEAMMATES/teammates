import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import {
  CustomPrivilegeSettingPanelComponent,
} from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import {
  InstructorEditPanelComponent,
} from './instructor-edit-panel/instructor-edit-panel.component';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal/view-role-privileges-modal.component';

describe('InstructorCourseEditPageComponent', () => {
  let component: InstructorCourseEditPageComponent;
  let fixture: ComponentFixture<InstructorCourseEditPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorCourseEditPageComponent,
        InstructorEditPanelComponent,
        ViewRolePrivilegesModalComponent,
        CustomPrivilegeSettingPanelComponent,
      ],
      imports: [
        NgbModule,
        FormsModule,
        AjaxLoadingModule,
        TeammatesCommonModule,
        RouterTestingModule,
        HttpClientTestingModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        TeammatesRouterModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseEditPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
