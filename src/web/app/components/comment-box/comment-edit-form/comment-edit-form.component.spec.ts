import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe, CommentVisibilityTypeNamePipe,
} from '../comment-visibility-setting.pipe';
import { CommentEditFormComponent } from './comment-edit-form.component';
import {CommentVisibilityType} from "../../../../types/api-output";
import {CommentVisibilityControl} from "../../../../types/comment-visibility-control";

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

  // toggleVisibilityTable
  it('should initialize with the visibility table collapsed', () => {
    expect(component.isVisibilityTableExpanded).toBeFalsy();
  });

  it('should toggle visibility table from collapsed to expanded', () => {
    expect(component.isVisibilityTableExpanded).toBeFalsy();
    component.toggleVisibilityTable();
    // After toggling, it should be expanded
    expect(component.isVisibilityTableExpanded).toBeTruthy();
  });

  it('should toggle visibility table from expanded to collapsed', () => {
    component.isVisibilityTableExpanded = true;
    expect(component.isVisibilityTableExpanded).toBeTruthy();
    component.toggleVisibilityTable();
    // After toggling, it should be collapsed
    expect(component.isVisibilityTableExpanded).toBeFalsy();
  });

  //triggerModelChange
  it('should trigger model change with updated field', () => {
    const testField = 'commentText';
    const testData = 'Updated comment text';

    const emitSpy = jest.spyOn(component.modelChange, 'emit');

    component.triggerModelChange(testField, testData);

    // Verify that the model should be updated
    expect(emitSpy).toHaveBeenCalledWith({
      ...component.model,
      [testField]: testData,
    });
  });

  //triggerModelChangeBatch
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

    // Check if the model is updated
    expect(modelChangeSpy).toHaveBeenCalledWith(updatedModel);
  });

  // triggerCloseCommentBoxEvent
  it('should emit the closeCommentBoxEvent when triggerCloseCommentBoxEvent is called', () => {
    const closeCommentBoxEventSpy = jest.spyOn(component.closeCommentBoxEvent, 'emit');
    component.triggerCloseCommentBoxEvent();
    // Check if the closeCommentBoxEvent was emitted
    expect(closeCommentBoxEventSpy).toHaveBeenCalled();
  });

  //triggerSaveCommentEvent
  it('should emit the saveCommentEvent when triggerSaveCommentEvent is called', () => {
    const saveCommentEventSpy = jest.spyOn(component.saveCommentEvent, 'emit');
    component.triggerSaveCommentEvent();
    // Check if the saveCommentEvent was emitted
    expect(saveCommentEventSpy).toHaveBeenCalled();
  });

  //modifyVisibilityControl
  it('should allow and disallow visibility control and trigger model change', () => {
    const isAllowed = true;
    const visibilityType = CommentVisibilityType.GIVER;
    const visibilityControl = CommentVisibilityControl.SHOW_COMMENT;

    jest.spyOn(component.visibilityStateMachine, 'allowToSee');
    jest.spyOn(component.visibilityStateMachine, 'disallowToSee');
    jest.spyOn(component, 'triggerModelChangeBatch');

    component.modifyVisibilityControl(isAllowed, visibilityType, visibilityControl);

    // Check if the allowToSee method was called and disallowToSee was not called
    expect(component.visibilityStateMachine.allowToSee).toHaveBeenCalledWith(visibilityType, visibilityControl);
    expect(component.visibilityStateMachine.disallowToSee).not.toHaveBeenCalled();

    // Check if triggerModelChangeBatch was called with the expected visibility types
    expect(component.triggerModelChangeBatch).toHaveBeenCalledWith({
      showCommentTo: [visibilityType],
      showGiverNameTo: [],
    });

    component.modifyVisibilityControl(false, visibilityType, visibilityControl);

    // Check if the disallowToSee method was called and allowToSee was not called
    expect(component.visibilityStateMachine.disallowToSee).toHaveBeenCalledWith(visibilityType, visibilityControl);
    expect(component.visibilityStateMachine.allowToSee).toHaveBeenCalled();

    // Check if triggerModelChangeBatch was called again with the updated visibility
    expect(component.triggerModelChangeBatch).toHaveBeenCalledWith({
      showCommentTo: [],
      showGiverNameTo: [],
    });
  });
});
