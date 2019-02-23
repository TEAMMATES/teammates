import { CommonModule } from '@angular/common';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../../../components/ajax-loading/ajax-loading.module';
import { TeammatesCommonModule } from '../../../../components/teammates-common/teammates-common.module';
import { InstructorSectionPrivilegesFormFormComponent } from './instructor-section-privileges-form.component';

describe('InstructorSectionPrivilegesFormFormComponent', () => {
  let component: InstructorSectionPrivilegesFormFormComponent;
  let fixture: ComponentFixture<InstructorSectionPrivilegesFormFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSectionPrivilegesFormFormComponent],
      imports: [
        AjaxLoadingModule,
        FormsModule,
        NgbModule,
        ReactiveFormsModule,
        TeammatesCommonModule,
        CommonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSectionPrivilegesFormFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
