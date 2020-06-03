import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommentResultTableComponent } from './comment-result-table.component';

describe('CommentResultTableComponent', () => {
  let component: CommentResultTableComponent;
  let fixture: ComponentFixture<CommentResultTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CommentResultTableComponent ]
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
