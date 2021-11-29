import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule, NoopAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { CommentEditFormComponent } from '../comment-edit-form/comment-edit-form.component';
import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe,
  CommentVisibilityTypeNamePipe,
  CommentVisibilityTypesJointNamePipe,
} from '../comment-visibility-setting.pipe';
import { CommentRowComponent, CommentRowModel } from './comment-row.component';

describe('CommentRowComponent', () => {
  let component: CommentRowComponent;
  let fixture: ComponentFixture<CommentRowComponent>;

  beforeEach(async(() => {
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
        BrowserAnimationsModule,
        NoopAnimationsModule,
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

  it('should raises the selected event when closeEditingEvent is invoked', () => {
    const button: any = fixture.nativeElement.querySelector('button');

    spyOn(component.closeEditingEvent, 'emit');

    button.click();
    fixture.detectChanges();

    component.triggerCloseEditing();

    expect(component.closeEditingEvent.emit).toHaveBeenCalled();
  });

  it('should raises the selected event when saveCommentEvent is invoked', () => {
    const button: any = fixture.nativeElement.querySelector('button');

    spyOn(component.saveCommentEvent, 'emit');

    button.click();
    fixture.detectChanges();

    component.triggerSaveCommentEvent();

    expect(component.saveCommentEvent.emit).toHaveBeenCalled();
  });

  it('should raises the selected event when modelChange is invoked', () => {
    const button: any = fixture.nativeElement.querySelector('button');
    const field: string = 'test field';
    const data: any = {};
    const object: any = { [field]: data };
    const commentRowModel: CommentRowModel = {
      commentEditFormModel: {
        commentText: '',
        isUsingCustomVisibilities: false,
        showCommentTo: [],
        showGiverNameTo: [],
      },
      isEditing: false,
    };

    spyOn(component.modelChange, 'emit').and.returnValue(of({ ...commentRowModel, ...object }));

    button.click();
    fixture.detectChanges();

    component.triggerModelChange(field, data);

    expect(component.modelChange.emit).toHaveBeenCalledWith({ ...commentRowModel, ...object });
  });
});
