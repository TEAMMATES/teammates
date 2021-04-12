import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import {
  QuestionSubmissionFormModule,
} from '../../components/question-submission-form/question-submission-form.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { SessionSubmissionPageComponent } from './session-submission-page.component';

describe('SessionSubmissionPageComponent', () => {
  let component: SessionSubmissionPageComponent;
  let fixture: ComponentFixture<SessionSubmissionPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SessionSubmissionPageComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        NgxPageScrollCoreModule,
        TeammatesCommonModule,
        FormsModule,
        AjaxLoadingModule,
        QuestionSubmissionFormModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionSubmissionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
