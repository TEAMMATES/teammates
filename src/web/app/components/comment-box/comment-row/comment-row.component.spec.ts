import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
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
});
