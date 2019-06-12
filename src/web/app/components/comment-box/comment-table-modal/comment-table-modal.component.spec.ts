import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SingleResponseModule } from '../../question-responses/single-response/single-response.module';
import { CommentEditFormComponent } from '../comment-edit-form.component';
import { CommentRowComponent } from '../comment-table/comment-row.component';
import { CommentTableComponent } from '../comment-table/comment-table.component';

import { CommentTableModalComponent } from './comment-table-modal.component';

describe('CommentTableModalComponent', () => {
  let component: CommentTableModalComponent;
  let fixture: ComponentFixture<CommentTableModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CommentTableModalComponent,
        CommentTableComponent,
        CommentRowComponent,
        CommentEditFormComponent,
      ],
      imports: [
        FormsModule,
        SingleResponseModule,
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
});
