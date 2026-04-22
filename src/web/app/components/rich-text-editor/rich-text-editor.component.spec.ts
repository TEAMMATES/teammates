import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RichTextEditorComponent } from './rich-text-editor.component';

describe('RichTextEditorComponent', () => {
  let component: RichTextEditorComponent;
  let fixture: ComponentFixture<RichTextEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RichTextEditorComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RichTextEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize editor settings on init', () => {
    component.ngOnInit();

    expect(component.init).toBeTruthy();
    expect(component.init.toolbar1).toBe(component.defaultToolbar);
  });

  it('should use input values in editor settings', () => {
    component.minHeightInPx = 300;
    component.placeholderText = 'Enter text here';

    component.ngOnInit();

    expect(component.init.height).toBe(300);
    expect(component.init.placeholder).toBe('Enter text here');
  });

  it('should return current character count from editor', () => {
    const mockEditor = {
      plugins: {
        wordcount: {
          body: {
            getCharacterCount: () => 123,
          },
        },
      },
    };

    expect(component.getCurrentCharacterCount(mockEditor)).toBe(123);
  });

  it('should render editor when visible', () => {
    component.render = false;
    component.renderEditor({ visible: true });
    expect(component.render).toBe(true);
  });

  it('should not render editor when not visible', () => {
    component.render = false;
    component.renderEditor({ visible: false });
    expect(component.render).toBe(false);
  });

  it('should define setup function when character limit is enabled', () => {
    component.hasCharacterLimit = true;
    component.ngOnInit();
    expect(component.init.setup).toBeDefined();
  });
});
