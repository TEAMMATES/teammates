import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { vi } from 'vitest';
import { CommentEditFormComponent } from './comment-edit-form.component';
import { FeedbackQuestionType, ResponseInstructorComment, FeedbackResponseDetails } from '../../../../types/api-output';

describe('CommentEditFormComponent', () => {
  let component: CommentEditFormComponent;
  let fixture: ComponentFixture<CommentEditFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(CommentEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should not modify the response when response is not provided', () => {
      component.ngOnInit();
      expect(component.response).toBeUndefined();
    });

    it('should remove anonymous hash from giver and recipient when response is provided', () => {
      const feedbackResponseDetails: FeedbackResponseDetails = {
        questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
      };
      const commentOutputs: ResponseInstructorComment[] = [];
      component.response = {
        isMissingResponse: true,
        responseId: 'string',
        giver: 'Anonymous student 123',
        giverTeam: 'string',
        giverSection: 'string',
        recipient: 'Anonymous instructor 456',
        recipientTeam: 'string',
        recipientSection: 'string',
        responseDetails: feedbackResponseDetails,
        instructorComments: commentOutputs,
      };
      const response = component.response;

      component.ngOnInit();

      expect(response.giver).toEqual('Anonymous student');
      expect(response.recipient).toEqual('Anonymous instructor');
    });
  });

  it('should trigger model change with updated field', () => {
    const testField = 'commentText';
    const testData = 'Updated comment text';

    const emitSpy = vi.spyOn(component.modelChange, 'emit');

    component.triggerModelChange(testField, testData);

    expect(emitSpy).toHaveBeenCalledWith({
      ...component.model,
      [testField]: testData,
    });
  });

  it('should emit the closeCommentBoxEvent when triggerCloseCommentBoxEvent is called', () => {
    const closeCommentBoxEventSpy = vi.spyOn(component.closeCommentBoxEvent, 'emit');
    component.triggerCloseCommentBoxEvent();
    expect(closeCommentBoxEventSpy).toHaveBeenCalled();
  });

  it('should emit the saveCommentEvent when triggerSaveCommentEvent is called', () => {
    const saveCommentEventSpy = vi.spyOn(component.saveCommentEvent, 'emit');
    component.triggerSaveCommentEvent();
    expect(saveCommentEventSpy).toHaveBeenCalled();
  });
});
