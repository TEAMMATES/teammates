import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommentBoxModule } from '../../../components/comment-box/comment-box.module';
import { SingleResponseModule } from '../../../components/question-responses/single-response/single-response.module';
import { CommentResultTableComponent } from './comment-result-table.component';

describe('CommentResultTableComponent', () => {
  let component: CommentResultTableComponent;
  let fixture: ComponentFixture<CommentResultTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CommentResultTableComponent],
      imports: [CommentBoxModule, SingleResponseModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentResultTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
