import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { CommentEditFormComponent } from '../comment-edit-form/comment-edit-form.component';
import {
  CommentVisibilityControlNamePipe, CommentVisibilityTypeDescriptionPipe, CommentVisibilityTypeNamePipe,
  CommentVisibilityTypesJointNamePipe,
} from '../comment-visibility-setting.pipe';
import { CommentRowComponent } from './comment-row.component';

describe('CommentRowComponent', () => {
  let component: CommentRowComponent;
  let fixture: ComponentFixture<CommentRowComponent>;

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
});
