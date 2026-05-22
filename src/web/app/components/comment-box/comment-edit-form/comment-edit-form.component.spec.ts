import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommentEditFormComponent } from './comment-edit-form.component';
import {
  CommentVisibilityType,
  FeedbackQuestionType,
  FeedbackResponseComment,
  FeedbackResponseDetails,
} from '../../../../types/api-output';
import { CommentVisibilityControl } from '../../../../types/comment-visibility-control';

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
        questionType: FeedbackQuestionType.CONSTSUM,
      };
      const commentOutputs: FeedbackResponseComment[] = [];
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

  it('should initialize with the visibility table collapsed', () => {
    expect(component.isVisibilityTableExpanded).toBeFalsy();
  });

  it('should toggle visibility table from collapsed to expanded', () => {
    expect(component.isVisibilityTableExpanded).toBeFalsy();
    component.toggleVisibilityTable();
    expect(component.isVisibilityTableExpanded).toBeTruthy();
  });

  it('should toggle visibility table from expanded to collapsed', () => {
    component.isVisibilityTableExpanded = true;
    expect(component.isVisibilityTableExpanded).toBeTruthy();
    component.toggleVisibilityTable();
    expect(component.isVisibilityTableExpanded).toBeFalsy();
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

  it('should emit the updated model when triggerModelChangeBatch is called', () => {
    component.model = {
      commentText: 'Initial Comment',
      isUsingCustomVisibilities: false,
      showCommentTo: [],
      showGiverNameTo: [],
    };
    fixture.detectChanges();

    const updatedModel = {
      commentText: 'Updated Comment',
      isUsingCustomVisibilities: true,
      showCommentTo: ['Public'],
      showGiverNameTo: ['Team'],
    };

    const modelChangeSpy = vi.spyOn(component.modelChange, 'emit');

    component.triggerModelChangeBatch(updatedModel);

    expect(modelChangeSpy).toHaveBeenCalledWith(updatedModel);
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

  it('should allow and disallow visibility control and trigger model change', () => {
    const isAllowed = true;
    const visibilityType = CommentVisibilityType.GIVER;
    const visibilityControl = CommentVisibilityControl.SHOW_COMMENT;

    const allowToSee = vi.spyOn(component.visibilityStateMachine, 'allowToSee');
    const disallowToSee = vi.spyOn(component.visibilityStateMachine, 'disallowToSee');
    const triggerModelChangeBatch = vi.spyOn(component, 'triggerModelChangeBatch');

    component.modifyVisibilityControl(isAllowed, visibilityType, visibilityControl);

    expect(allowToSee).toHaveBeenCalledWith(visibilityType, visibilityControl);
    expect(disallowToSee).not.toHaveBeenCalled();
    expect(triggerModelChangeBatch).toHaveBeenCalledWith({
      showCommentTo: [visibilityType],
      showGiverNameTo: [],
    });

    component.modifyVisibilityControl(false, visibilityType, visibilityControl);

    expect(component.visibilityStateMachine.disallowToSee).toHaveBeenCalledWith(visibilityType, visibilityControl);
    expect(component.visibilityStateMachine.allowToSee).toHaveBeenCalled();

    expect(component.triggerModelChangeBatch).toHaveBeenCalledWith({
      showCommentTo: [],
      showGiverNameTo: [],
    });
  });
});
