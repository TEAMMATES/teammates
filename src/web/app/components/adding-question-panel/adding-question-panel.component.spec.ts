import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackQuestionType } from '../../../types/api-output';

import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { QuestionTypeHelpPathPipe } from '../teammates-common/question-type-help-path.pipe';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { AddingQuestionPanelComponent } from './adding-question-panel.component';

describe('AddingQuestionPanelComponent', () => {
  let component: AddingQuestionPanelComponent;
  let fixture: ComponentFixture<AddingQuestionPanelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        AddingQuestionPanelComponent,
      ],
      imports: [
        AjaxLoadingModule,
        RouterTestingModule,
        NgbDropdownModule,
        TeammatesCommonModule,
        TeammatesRouterModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddingQuestionPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain anchor element with corresponding search query', () => {
    expect(Object.keys(component.FeedbackQuestionType)[0]).not.toBe(component.FeedbackQuestionType.CONSTSUM);
    const firstQuestionType = Object.keys(component.FeedbackQuestionType)[0] as FeedbackQuestionType;
    const questionTypeHelpPathPipe = new QuestionTypeHelpPathPipe();
    const questionId = questionTypeHelpPathPipe.transform(firstQuestionType);
    const firstAnchorElement = document.querySelector('.btn-group')?.children[1] as HTMLAnchorElement;
    const expectedSearchQuery = `?questionId=${questionId}&section=questions`;
    expect(firstAnchorElement).toHaveProperty('search', expectedSearchQuery);
  });
});
