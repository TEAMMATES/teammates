import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';

import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { CommentEditFormComponent } from './comment-edit-form.component';

describe('CommentEditFormComponent', () => {
  let component: CommentEditFormComponent;
  let fixture: ComponentFixture<CommentEditFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CommentEditFormComponent],
      imports: [
        FormsModule,
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
