import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommentEditFormComponent } from './comment-edit-form.component';
import {
  CommentOutput,
  CommentVisibilityType, FeedbackQuestionType,
  FeedbackResponseDetails,
} from '../../../../types/api-output';
import { CommentVisibilityControl } from '../../../../types/comment-visibility-control';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe,
  CommentVisibilityTypeNamePipe,
} from '../comment-visibility-setting.pipe';

describe('CommentEditFormComponent', () => {
  let component: CommentEditFormComponent;
  let fixture: ComponentFixture<CommentEditFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        CommentEditFormComponent,
        CommentVisibilityControlNamePipe,
        CommentVisibilityTypeDescriptionPipe,
        CommentVisibilityTypeNamePipe,
      ],
      imports: [
        FormsModule,
        NgbModule,
        HttpClientTestingModule,
        TeammatesCommonModule,
        RichTextEditorModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
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
      const feedbackResponseDetails : FeedbackResponseDetails = {
        questionType: FeedbackQuestionType.CONSTSUM,
      };
      const commentOutputs: CommentOutput[] = [];
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

    const emitSpy = jest.spyOn(component.modelChange, 'emit');

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

    const modelChangeSpy = jest.spyOn(component.modelChange, 'emit');

    component.triggerModelChangeBatch(updatedModel);

    expect(modelChangeSpy).toHaveBeenCalledWith(updatedModel);
  });

  it('should emit the closeCommentBoxEvent when triggerCloseCommentBoxEvent is called', () => {
    const closeCommentBoxEventSpy = jest.spyOn(component.closeCommentBoxEvent, 'emit');
    component.triggerCloseCommentBoxEvent();
    expect(closeCommentBoxEventSpy).toHaveBeenCalled();
  });

  it('should emit the saveCommentEvent when triggerSaveCommentEvent is called', () => {
    const saveCommentEventSpy = jest.spyOn(component.saveCommentEvent, 'emit');
    component.triggerSaveCommentEvent();
    expect(saveCommentEventSpy).toHaveBeenCalled();
  });

  it('should allow and disallow visibility control and trigger model change', () => {
    const isAllowed = true;
    const visibilityType = CommentVisibilityType.GIVER;
    const visibilityControl = CommentVisibilityControl.SHOW_COMMENT;

    const allowToSee = jest.spyOn(component.visibilityStateMachine, 'allowToSee');
    const disallowToSee = jest.spyOn(component.visibilityStateMachine, 'disallowToSee');
    const triggerModelChangeBatch = jest.spyOn(component, 'triggerModelChangeBatch');

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
