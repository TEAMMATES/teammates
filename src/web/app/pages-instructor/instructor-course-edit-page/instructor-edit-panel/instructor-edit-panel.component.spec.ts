import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../../components/ajax-loading/ajax-loading.module';
import { LoadingSpinnerModule } from '../../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../../components/teammates-common/teammates-common.module';
import {
  CustomPrivilegeSettingPanelComponent,
} from '../custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import { ViewRolePrivilegesModalComponent } from '../view-role-privileges-modal/view-role-privileges-modal.component';
import { InstructorEditPanelComponent } from './instructor-edit-panel.component';

describe('InstructorEditPanelComponent', () => {
  let component: InstructorEditPanelComponent;
  let fixture: ComponentFixture<InstructorEditPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorEditPanelComponent,
        InstructorEditPanelComponent,
        ViewRolePrivilegesModalComponent,
        CustomPrivilegeSettingPanelComponent,
      ],
      imports: [
        FormsModule,
        NgbModule,
        AjaxLoadingModule,
        TeammatesCommonModule,
        LoadingSpinnerModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorEditPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
