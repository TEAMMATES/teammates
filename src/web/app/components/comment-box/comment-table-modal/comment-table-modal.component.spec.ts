import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import SpyInstance = jest.SpyInstance;
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { CommentEditFormComponent } from '../comment-edit-form/comment-edit-form.component';
import { CommentRowComponent } from '../comment-row/comment-row.component';
import { CommentTableComponent, CommentTableModel } from '../comment-table/comment-table.component';

import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe, CommentVisibilityTypeNamePipe, CommentVisibilityTypesJointNamePipe,
} from '../comment-visibility-setting.pipe';
import { CommentTableModalComponent } from './comment-table-modal.component';

describe('CommentTableModalComponent', () => {
  let component: CommentTableModalComponent;
  let fixture: ComponentFixture<CommentTableModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        CommentTableModalComponent,
        CommentTableComponent,
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
        RichTextEditorModule,
        NgbModule,
        BrowserAnimationsModule,
      ],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentTableModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set isAddingNewComment to true in the model', () => {
    const ngOnChangesSpy: SpyInstance = jest.spyOn(component.modelChange, 'emit');
    const testModel: CommentTableModel = {
      commentRows: [],
      newCommentRow: {
        commentEditFormModel: {
          commentText: '',
          isUsingCustomVisibilities: false,
          showCommentTo: [],
          showGiverNameTo: [],
        },
        isEditing: true,
      },
      isAddingNewComment: false,
      isReadOnly: false,
    };
    component.model = testModel;
    component.ngOnChanges();
    expect(ngOnChangesSpy).toHaveBeenCalledWith({
      ...testModel,
      isAddingNewComment: true,
    });
  });

  it('should trigger an event to delete comment from comments table', () => {
    const deleteCommentEventSpy: SpyInstance = jest.spyOn(component.deleteCommentEvent, 'emit');
    const testIndex = 1;
    component.triggerDeleteCommentEvent(testIndex);
    expect(deleteCommentEventSpy).toHaveBeenCalledWith(testIndex);
  });

  it('should trigger an event to update comment from comments table', () => {
    const updateCommentEventSpy: SpyInstance = jest.spyOn(component.updateCommentEvent, 'emit');
    const testIndex = 0;
    component.triggerUpdateCommentEvent(testIndex);
    expect(updateCommentEventSpy).toHaveBeenCalledWith(testIndex);
  });

  it('should trigger an event to add a new comment to the comments table', () => {
    const saveNewCommentEventSpy: SpyInstance = jest.spyOn(component.saveNewCommentEvent, 'emit');
    component.triggerSaveNewCommentEvent();
    expect(saveNewCommentEventSpy).toHaveBeenCalled();
  });

  it('should trigger an event to change the model of the form', () => {
    const changeFormModelEventSpy = jest.spyOn(component.modelChange, 'emit');
    const testModel: CommentTableModel = {
      commentRows: [],
      newCommentRow: {
        commentEditFormModel: {
          commentText: '',
          isUsingCustomVisibilities: false,
          showCommentTo: [],
          showGiverNameTo: [],
        },
        isEditing: true,
      },
      isAddingNewComment: false,
      isReadOnly: false,
    };
    component.triggerModelChange(testModel);
    expect(changeFormModelEventSpy).toHaveBeenCalledWith(testModel);
  });
});
