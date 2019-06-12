import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { CommentEditFormComponent } from '../comment-edit-form.component';

import { CommentRowComponent } from './comment-row.component';

describe('CommentRowComponent', () => {
  let component: CommentRowComponent;
  let fixture: ComponentFixture<CommentRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CommentRowComponent,
        CommentEditFormComponent,
      ],
      imports: [
        FormsModule,
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
