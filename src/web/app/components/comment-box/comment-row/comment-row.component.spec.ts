import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommentRowComponent } from './comment-row.component';
import { CommentVisibilityStateMachine } from '../../../../services/comment-visibility-state-machine';
import { ResponseInstructorCommentService } from '../../../../services/feedback-response-comment.service';
import createSpyFromClass from '../../../../test-helpers/create-spy-from-class';
import { CommentVisibilityType, FeedbackVisibilityType } from '../../../../types/api-output';

describe('CommentRowComponent', () => {
  let component: CommentRowComponent;
  let fixture: ComponentFixture<CommentRowComponent>;

  const spyVisibilityStateMachine = createSpyFromClass(CommentVisibilityStateMachine);

  const spyCommentService = createSpyFromClass(ResponseInstructorCommentService);
  spyCommentService.getNewVisibilityStateMachine.mockReturnValue(spyVisibilityStateMachine);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: ResponseInstructorCommentService, useValue: spyCommentService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CommentRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnChanges', () => {
    it('should properly handle visibility settings if comment text is defined', () => {
      component.model = {
        commentType: 'instructor',
        commentGiverName: 'Mock Giver Name',
        lastEditorName: 'Mock Editor Name',
        commentId: '00000000-0000-4000-8000-000000000001',
        createdAt: Date.now(),
        lastEditedAt: Date.now(),
        timezone: 'UTC',
        originalCommentFormModel: {
          commentText: 'Mock comment text',
          showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
          showGiverNameTo: [],
        },
        commentEditFormModel: {
          commentText: 'Mock comment text for form',
          showCommentTo: [CommentVisibilityType.GIVER],
          showGiverNameTo: [CommentVisibilityType.INSTRUCTORS],
        },
        isEditing: true,
      };
      component.questionShowResponsesTo = [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT];
      component.ngOnChanges();

      expect(component.visibilityStateMachine).toBeDefined();
      expect(component.visibilityStateMachine.allowAllApplicableTypesToSee).toBeDefined();

      expect(spyVisibilityStateMachine.applyVisibilitySettings).toHaveBeenCalledWith({
        SHOW_COMMENT: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
        SHOW_GIVER_NAME: [],
      });

      expect(spyCommentService.getNewVisibilityStateMachine).toHaveBeenCalledWith(component.questionShowResponsesTo);
    });

    it('should apply empty visibility settings when no visibility type is selected', () => {
      component.model = {
        commentType: 'instructor',
        commentGiverName: 'Mock Giver Name',
        lastEditorName: 'Mock Editor Name',
        commentId: '00000000-0000-4000-8000-000000000001',
        createdAt: Date.now(),
        lastEditedAt: Date.now(),
        timezone: 'UTC',
        originalCommentFormModel: {
          commentText: 'Mock comment text',
          showCommentTo: [],
          showGiverNameTo: [],
        },
        commentEditFormModel: {
          commentText: 'Mock comment text for form',
          showCommentTo: [CommentVisibilityType.GIVER],
          showGiverNameTo: [CommentVisibilityType.INSTRUCTORS],
        },
        isEditing: true,
      };
      component.ngOnChanges();
      expect(spyVisibilityStateMachine.applyVisibilitySettings).toHaveBeenCalledWith({
        SHOW_COMMENT: [],
        SHOW_GIVER_NAME: [],
      });
    });
  });

  describe('triggerCloseEditing', () => {
    it('should emit closeEditing event', () => {
      const emitSpy = vi.spyOn(component.closeEditingEvent, 'emit');
      component.triggerCloseEditing();
      expect(emitSpy).toHaveBeenCalled();
    });
  });

  describe('triggerSaveCommentEvent', () => {
    it('should emit saveComment event', () => {
      const spy = vi.spyOn(component.saveCommentEvent, 'emit');
      component.triggerSaveCommentEvent();
      expect(spy).toHaveBeenCalled();
    });
  });

  describe('triggerDeleteCommentEvent', () => {
    it('should emit deleteComment event after modal confirmation', async () => {
      const mockModalRef = { result: Promise.resolve(true) };
      vi.spyOn((component as any).simpleModalService, 'openConfirmationModal').mockReturnValue(mockModalRef);
      const emitSpy = vi.spyOn(component.deleteCommentEvent, 'emit');
      component.triggerDeleteCommentEvent();
      await mockModalRef.result;
      expect(emitSpy).toHaveBeenCalled();
    });

    it('should set visibility settings when comment text is defined and not following feedback question', () => {
      component.model = {
        commentType: 'instructor',
        commentGiverName: 'Mock Giver Name',
        lastEditorName: 'Mock Editor Name',
        commentId: '00000000-0000-4000-8000-000000000001',
        createdAt: Date.now(),
        lastEditedAt: Date.now(),
        timezone: 'UTC',
        originalCommentFormModel: {
          commentText: 'Mock comment text',
          showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
          showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
        },
        commentEditFormModel: {
          commentText: 'Mock comment text for form',
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
