import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OptionRichTextEditorComponent } from './option-rich-text-editor.component';
import { OptionRichTextEditorModule } from './option-rich-text-editor.module';

describe('RichTextEditorComponent', () => {
  let component: OptionRichTextEditorComponent;
  let fixture: ComponentFixture<OptionRichTextEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [OptionRichTextEditorModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OptionRichTextEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
