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
});
