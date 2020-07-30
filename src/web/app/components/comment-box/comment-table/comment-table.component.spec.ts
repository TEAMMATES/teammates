import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { CommentEditFormComponent } from '../comment-edit-form/comment-edit-form.component';
import { CommentRowComponent } from '../comment-row/comment-row.component';
import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe, CommentVisibilityTypeNamePipe, CommentVisibilityTypesJointNamePipe,
} from '../comment-visibility-setting.pipe';
import { CommentTableComponent } from './comment-table.component';

describe('CommentTableComponent', () => {
  let component: CommentTableComponent;
  let fixture: ComponentFixture<CommentTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CommentTableComponent,
        CommentRowComponent,
        CommentEditFormComponent,
        CommentVisibilityControlNamePipe,
        CommentVisibilityTypeDescriptionPipe,
        CommentVisibilityTypeNamePipe,
        CommentVisibilityTypesJointNamePipe,
      ],
      imports: [
        NgbModule,
        RichTextEditorModule,
        HttpClientTestingModule,
        TeammatesCommonModule,
        FormsModule,
        BrowserAnimationsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
