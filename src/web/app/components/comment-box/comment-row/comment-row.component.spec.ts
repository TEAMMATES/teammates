import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { vi } from 'vitest';
import { CommentRowComponent } from './comment-row.component';

describe('CommentRowComponent', () => {
  let component: CommentRowComponent;
  let fixture: ComponentFixture<CommentRowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(CommentRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('triggerCloseEditing', () => {
    it('should emit closeEditing event', () => {
      const emitSpy = vi.spyOn(component.closeEditingEvent, 'emit');
      component.triggerCloseEditing();
      expect(emitSpy).toHaveBeenCalled();
    });
  });

  describe('triggerSaveCommentEvent', () => {
    it('should emit saveComment event', () => {
      const spy = vi.spyOn(component.saveCommentEvent, 'emit');
      component.triggerSaveCommentEvent();
      expect(spy).toHaveBeenCalled();
    });
  });

  it('should disable edit and delete buttons for comments not owned by the current instructor', () => {
    component.mode = component.CommentRowMode.EDIT;
    component.model = {
      commentType: 'instructor',
      commentId: 'comment-id',
      commentGiverName: 'Instructor',
      createdAt: 1,
      timezone: 'UTC',
      isOwnedByCurrentInstructor: false,
      originalCommentFormModel: {
        commentText: 'comment text',
      },
      commentEditFormModel: {
        commentText: 'comment text',
      },
      isEditing: false,
    };

    fixture.detectChanges();

    const buttons = fixture.debugElement.queryAll(By.css('button'));
    expect((buttons[0].nativeElement as HTMLButtonElement).disabled).toBe(true);
    expect((buttons[1].nativeElement as HTMLButtonElement).disabled).toBe(true);
  });

  it('should enable edit and delete buttons for comments owned by the current instructor', () => {
    component.mode = component.CommentRowMode.EDIT;
    component.model = {
      commentType: 'instructor',
      commentId: 'comment-id',
      commentGiverName: 'Instructor',
      createdAt: 1,
      timezone: 'UTC',
      isOwnedByCurrentInstructor: true,
      originalCommentFormModel: {
        commentText: 'comment text',
      },
      commentEditFormModel: {
        commentText: 'comment text',
      },
      isEditing: false,
    };

    fixture.detectChanges();

    const buttons = fixture.debugElement.queryAll(By.css('button'));
    expect((buttons[0].nativeElement as HTMLButtonElement).disabled).toBe(false);
    expect((buttons[1].nativeElement as HTMLButtonElement).disabled).toBe(false);
  });
});
