import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HotTableModule } from '@handsontable/angular';
import { AjaxPreloadModule } from '../../components/ajax-preload/ajax-preload.module';
import { StatusMessageModule } from '../../components/status-message/status-message.module';
import { InstructorCourseEnrollPageComponent } from './instructor-course-enroll-page.component';

describe('InstructorCourseEnrollPageComponent', () => {
  let component: InstructorCourseEnrollPageComponent;
  let fixture: ComponentFixture<InstructorCourseEnrollPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorCourseEnrollPageComponent],
      imports: [
        HttpClientTestingModule,
        HotTableModule,
        RouterTestingModule,
        AjaxPreloadModule,
        StatusMessageModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseEnrollPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
