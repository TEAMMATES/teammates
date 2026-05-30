import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { CommentTableModalComponent } from './comment-table-modal.component';
import { CommentTableModel } from '../comment-table/comment-table.model';

describe('CommentTableModalComponent', () => {
  let component: CommentTableModalComponent;
  let fixture: ComponentFixture<CommentTableModalComponent>;
  const testModel: CommentTableModel = {
    commentRows: [],
    newCommentRow: {
      commentType: 'new',
      commentEditFormModel: {
        commentText: '',
        showCommentTo: [],
        showGiverNameTo: [],
      },
      isEditing: true,
    },
    isAddingNewComment: false,
    isReadOnly: false,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [NgbActiveModal, provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(CommentTableModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set isAddingNewComment to true in the model', () => {
    const ngOnChangesSpy = vi.spyOn(component.modelChange, 'emit');
    component.model = testModel;
    component.ngOnChanges();
    expect(ngOnChangesSpy).toHaveBeenCalledWith({
      ...testModel,
      isAddingNewComment: true,
    });
  });

  it('should emit a DeleteCommentEvent with the correct index when triggerDeleteCommentEvent is called', () => {
    const deleteCommentEventSpy = vi.spyOn(component.deleteCommentEvent, 'emit');
    const testIndex = 1;
    component.triggerDeleteCommentEvent(testIndex);
    expect(deleteCommentEventSpy).toHaveBeenCalledWith(testIndex);
  });

  it('should emit an UpdateCommentEvent with the correct index when triggerUpdateCommentEvent is called', () => {
    const updateCommentEventSpy = vi.spyOn(component.updateCommentEvent, 'emit');
    const testIndex = 0;
    component.triggerUpdateCommentEvent(testIndex);
    expect(updateCommentEventSpy).toHaveBeenCalledWith(testIndex);
  });

  it('should emit a SaveNewCommentEvent when triggerSaveNewCommentEvent is called', () => {
    const saveNewCommentEventSpy = vi.spyOn(component.saveNewCommentEvent, 'emit');
    component.triggerSaveNewCommentEvent();
    expect(saveNewCommentEventSpy).toHaveBeenCalled();
  });

  it('should emit a ChangeFormModelEvent when triggerChangeFormModelEvent is called', () => {
    const changeFormModelEventSpy = vi.spyOn(component.modelChange, 'emit');
    component.triggerModelChange(testModel);
    expect(changeFormModelEventSpy).toHaveBeenCalledWith(testModel);
  });
});
