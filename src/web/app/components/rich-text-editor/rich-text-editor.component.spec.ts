import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RichTextEditorComponent } from './rich-text-editor.component';
import { RichTextEditorModule } from './rich-text-editor.module';

describe('RichTextEditorComponent', () => {
  let component: RichTextEditorComponent;
  let fixture: ComponentFixture<RichTextEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RichTextEditorModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RichTextEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
