import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  ComponentFixture,
  TestBed,
  waitForAsync,
} from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../loading-spinner/loading-spinner.module';
import { CourseCopyComponent } from './course-copy.component';

describe('CourseEditFormComponent', () => {
  let component: CourseCopyComponent;
  let fixture: ComponentFixture<CourseCopyComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CourseCopyComponent],
      imports: [
        HttpClientTestingModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        NgbModule,
        AjaxLoadingModule,
        CommonModule,
        FormsModule,
        AjaxLoadingModule,
        LoadingRetryModule,
        LoadingSpinnerModule,
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseCopyComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
