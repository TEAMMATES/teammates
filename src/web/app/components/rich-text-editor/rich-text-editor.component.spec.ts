import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RichTextEditorComponent } from './rich-text-editor.component';

describe('RichTextEditorComponent', () => {
  let component: RichTextEditorComponent;
  let fixture: ComponentFixture<RichTextEditorComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RichTextEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
