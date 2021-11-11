import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';

import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {RichTextEditorModule} from '../../rich-text-editor/rich-text-editor.module';
import {TeammatesCommonModule} from '../../teammates-common/teammates-common.module';
import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe,
  CommentVisibilityTypeNamePipe,
} from '../comment-visibility-setting.pipe';
import {CommentEditFormComponent, CommentEditFormModel} from './comment-edit-form.component';
import {BrowserAnimationsModule, NoopAnimationsModule} from "@angular/platform-browser/animations";
import {of} from "rxjs";
import {CommentVisibilityType, FeedbackVisibilityType} from "../../../../types/api-output";
import {CommentVisibilityControl} from "../../../../types/comment-visibility-control";
import {FeedbackResponseCommentService} from "../../../../services/feedback-response-comment.service";
import {CommentVisibilityStateMachine} from "../../../../services/comment-visibility-state-machine";

const object = {['key']: 1};
const model: CommentEditFormModel = {
  commentText: '',

  isUsingCustomVisibilities: false,
  showCommentTo: [],
  showGiverNameTo: [],
};

describe('CommentEditFormComponent', () => {
  let component: CommentEditFormComponent;
  let fixture: ComponentFixture<CommentEditFormComponent>;
  let commentService: FeedbackResponseCommentService;

  beforeEach(async(() => {
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
        BrowserAnimationsModule,
        NoopAnimationsModule
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentEditFormComponent);
    component = fixture.componentInstance;
    commentService = TestBed.inject(FeedbackResponseCommentService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should raises the selected event when closeCommentBoxEvent is invoked', () => {
    const button = fixture.nativeElement.querySelector('button');

    spyOn(component.closeCommentBoxEvent, 'emit');

    button.click();
    fixture.detectChanges();

    component.triggerCloseCommentBoxEvent();

    expect(component.closeCommentBoxEvent.emit).toHaveBeenCalled();
  });

  it('should raises the selected event when saveCommentEvent is invoked', () => {
    const button = fixture.nativeElement.querySelector('button');

    spyOn(component.saveCommentEvent, 'emit');

    button.click();
    fixture.detectChanges();

    component.triggerSaveCommentEvent();

    expect(component.saveCommentEvent.emit).toHaveBeenCalled();
  });

  it('should raises the selected event when modelChange is invoked', () => {
    const button = fixture.nativeElement.querySelector('button');
    const field = 'test field';
    const data = {};

    spyOn(component.modelChange, 'emit').and.returnValue(of(Object.assign({}, model, { [field]: data })));

    button.click();
    fixture.detectChanges();

    component.triggerModelChange(field, data);

    expect(component.modelChange.emit).toHaveBeenCalledWith(Object.assign({}, model, { [field]: data }));
  });

  it('should raises the selected event when modelChange with batch is invoked', () => {
    const button = fixture.nativeElement.querySelector('button');

    spyOn(component.modelChange, 'emit').and.returnValue(of({...model, ...object}));

    button.click();
    fixture.detectChanges();

    component.triggerModelChangeBatch(object);

    expect(component.modelChange.emit).toHaveBeenCalledWith({...model, ...object});
  });

  it('should allowToSee visibility in modifyVisibilityControl method', () => {
    const visibilityType = CommentVisibilityType.GIVER;
    const visibilityControl = CommentVisibilityControl.SHOW_COMMENT;

    const spy1 = spyOn(component.visibilityStateMachine, 'allowToSee');
    const spy2 = spyOn(component, 'triggerModelChangeBatch');

    component.modifyVisibilityControl(true, visibilityType, visibilityControl);

    expect(spy1).toHaveBeenCalled();
    expect(spy2).toHaveBeenCalled();
  });

  it('should disallowToSee visibility in modifyVisibilityControl method', () => {
    const visibilityType = CommentVisibilityType.GIVER;
    const visibilityControl = CommentVisibilityControl.SHOW_COMMENT;

    const spy1 = spyOn(component.visibilityStateMachine, 'disallowToSee');
    const spy2 = spyOn(component, 'triggerModelChangeBatch');

    component.modifyVisibilityControl(false, visibilityType, visibilityControl);

    expect(spy1).toHaveBeenCalled();
    expect(spy2).toHaveBeenCalled();
  });

  it('should disallowToSee visibility in modifyVisibilityControl method', () => {
    const visibilityType = CommentVisibilityType.GIVER;
    const visibilityControl = CommentVisibilityControl.SHOW_COMMENT;

    const spy1 = spyOn(component.visibilityStateMachine, 'disallowToSee');
    const spy2 = spyOn(component, 'triggerModelChangeBatch');

    component.modifyVisibilityControl(false, visibilityType, visibilityControl);

    expect(spy1).toHaveBeenCalled();
    expect(spy2).toHaveBeenCalled();
  });

  it('should check toggleVisibilityTable method to true', () => {
    component.isVisibilityTableExpanded = false;
    component.toggleVisibilityTable();
    expect(component.isVisibilityTableExpanded).toBe(true);
  });

  it('should check toggleVisibilityTable method to false', () => {
    component.isVisibilityTableExpanded = true;
    component.toggleVisibilityTable();
    expect(component.isVisibilityTableExpanded).toBe(false);
  });

  it('should check ngOnChanges method true condition', () => {
    const questionShowResponsesTo: FeedbackVisibilityType[] = [FeedbackVisibilityType.RECIPIENT];
    const stateMachine = new CommentVisibilityStateMachine(questionShowResponsesTo);

    spyOn(commentService, 'getNewVisibilityStateMachine').and.returnValue(stateMachine);
    const spy2 = spyOn(stateMachine, 'applyVisibilitySettings');

    component.model.isUsingCustomVisibilities = true;
    component.ngOnChanges();

    expect(component.visibilityStateMachine).toEqual(stateMachine);
    expect(spy2).toHaveBeenCalled();
  });

  it('should check ngOnChanges method false condition', () => {
    const questionShowResponsesTo: FeedbackVisibilityType[] = [FeedbackVisibilityType.RECIPIENT];
    const stateMachine = new CommentVisibilityStateMachine(questionShowResponsesTo);

    spyOn(commentService, 'getNewVisibilityStateMachine').and.returnValue(stateMachine);
    const spy2 = spyOn(stateMachine, 'allowAllApplicableTypesToSee');
    const spy3 = spyOn(component, 'triggerModelChangeBatch');

    component.model.isUsingCustomVisibilities = false;
    component.ngOnChanges();

    expect(component.visibilityStateMachine).toEqual(stateMachine);
    expect(spy2).toHaveBeenCalled();
    expect(spy3).toHaveBeenCalled();
  });
});
