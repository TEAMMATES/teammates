import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommentRowComponent } from './comment-row.component';
import { CommentVisibilityStateMachine } from '../../../../services/comment-visibility-state-machine';
import { FeedbackResponseCommentService } from '../../../../services/feedback-response-comment.service';
import createSpyFromClass from '../../../../test-helpers/create-spy-from-class';
import { CommentVisibilityType, FeedbackVisibilityType } from '../../../../types/api-output';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { CommentEditFormComponent } from '../comment-edit-form/comment-edit-form.component';
import {
  CommentVisibilityControlNamePipe, CommentVisibilityTypeDescriptionPipe, CommentVisibilityTypeNamePipe,
  CommentVisibilityTypesJointNamePipe,
} from '../comment-visibility-setting.pipe';

describe('CommentRowComponent', () => {
  let component: CommentRowComponent;
  let fixture: ComponentFixture<CommentRowComponent>;

  const spyVisibilityStateMachine = createSpyFromClass(CommentVisibilityStateMachine);

  const spyCommentService = createSpyFromClass(FeedbackResponseCommentService);
  spyCommentService.getNewVisibilityStateMachine.mockReturnValue(spyVisibilityStateMachine);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        CommentRowComponent,
        CommentEditFormComponent,
        CommentVisibilityControlNamePipe,
        CommentVisibilityTypeDescriptionPipe,
        CommentVisibilityTypeNamePipe,
        CommentVisibilityTypesJointNamePipe,
      ],
      imports: [
        FormsModule,
        TeammatesCommonModule,
        HttpClientTestingModule,
        NgbModule,
        RichTextEditorModule,
      ],
      providers: [
        { provide: FeedbackResponseCommentService, useValue: spyCommentService },
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnChanges', () => {
    it('should properly handle visibility settings if originalComment is defined', () => {
      component.model = {
        originalComment: {
          isVisibilityFollowingFeedbackQuestion: true,
          commentGiver: 'mockCommentGiver',
          lastEditorEmail: 'mockEditor@example.com',
          feedbackResponseCommentId: 12345,
          commentText: 'Mock comment text',
          showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
          createdAt: new Date().getTime(),
          lastEditedAt: new Date().getTime(),
          showGiverNameTo: [],
        },
        commentEditFormModel: {
          commentText: 'Mock comment text for form',
          isUsingCustomVisibilities: false,
          showCommentTo: [CommentVisibilityType.GIVER],
          showGiverNameTo: [CommentVisibilityType.INSTRUCTORS],
        },
        isEditing: true,
      };
      component.questionShowResponsesTo = [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT];
      component.ngOnChanges();

      expect(component.visibilityStateMachine).toBeDefined();
      expect(component.visibilityStateMachine.allowAllApplicableTypesToSee).toBeDefined();

      expect(spyVisibilityStateMachine.allowAllApplicableTypesToSee).toHaveBeenCalled();

      expect(spyCommentService.getNewVisibilityStateMachine).toHaveBeenCalledWith(
      component.questionShowResponsesTo,
    );
    });

    it('should allow all applicable types to see when isVisibilityFollowingFeedbackQuestion is true', () => {
      component.model = {
        originalComment: {
          isVisibilityFollowingFeedbackQuestion: true,
          commentGiver: 'mockCommentGiver',
          lastEditorEmail: 'mockEditor@example.com',
          feedbackResponseCommentId: 12345,
          commentText: 'Mock comment text',
          showCommentTo: [],
          createdAt: new Date().getTime(),
          lastEditedAt: new Date().getTime(),
          showGiverNameTo: [],
        },
        commentEditFormModel: {
          commentText: 'Mock comment text for form',
          isUsingCustomVisibilities: false,
          showCommentTo: [CommentVisibilityType.GIVER],
          showGiverNameTo: [CommentVisibilityType.INSTRUCTORS],
        },
        isEditing: true,
      };
      component.ngOnChanges();
      expect(spyVisibilityStateMachine.allowAllApplicableTypesToSee).toHaveBeenCalled();
    });
  });

  describe('triggerCloseEditing', () => {
    it('should emit closeEditing event', () => {
        const emitSpy = jest.spyOn(component.closeEditingEvent, 'emit');
        component.triggerCloseEditing();
        expect(emitSpy).toHaveBeenCalled();
    });
  });

  describe('triggerSaveCommentEvent', () => {
    it('should emit saveComment event', () => {
        const spy = jest.spyOn(component.saveCommentEvent, 'emit');
        component.triggerSaveCommentEvent();
        expect(spy).toHaveBeenCalled();
    });
  });

  describe('triggerDeleteCommentEvent', () => {
    it('should emit deleteComment event after modal confirmation', async () => {
      const mockModalRef = { result: Promise.resolve(true) };
      jest.spyOn((component as any).simpleModalService, 'openConfirmationModal').mockReturnValue(mockModalRef);
      const emitSpy = jest.spyOn(component.deleteCommentEvent, 'emit');
      component.triggerDeleteCommentEvent();
      await mockModalRef.result;
      expect(emitSpy).toHaveBeenCalled();
    });

  it('should set visibility settings when originalComment is defined and not following feedback question', () => {
    component.model = {
      originalComment: {
        isVisibilityFollowingFeedbackQuestion: false,
        commentGiver: 'mockCommentGiver',
        lastEditorEmail: 'mockEditor@example.com',
        feedbackResponseCommentId: 12345,
        commentText: 'Mock comment text',
        showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
        createdAt: new Date().getTime(),
        lastEditedAt: new Date().getTime(),
        showGiverNameTo: [],
      },
      commentEditFormModel: {
        commentText: 'Mock comment text for form',
        isUsingCustomVisibilities: false,
        showCommentTo: [CommentVisibilityType.GIVER],
        showGiverNameTo: [CommentVisibilityType.INSTRUCTORS],
      },
      isEditing: true,
    };
    component.ngOnChanges();
    expect(spyVisibilityStateMachine.applyVisibilitySettings).toHaveBeenCalledWith({
      SHOW_COMMENT: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
      SHOW_GIVER_NAME: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
    });
  });
  });
});
