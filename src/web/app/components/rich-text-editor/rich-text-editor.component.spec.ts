import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RichTextEditorComponent } from './rich-text-editor.component';
import { mockTinyMceUuid } from '../../../test-helpers/mock-tinymce-uuid';

describe('RichTextEditorComponent', () => {
  let component: RichTextEditorComponent;
  let fixture: ComponentFixture<RichTextEditorComponent>;

  mockTinyMceUuid();

  beforeEach(() => {
    fixture = TestBed.createComponent(RichTextEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have correct default input values', () => {
    expect(component.isDisabled).toBe(false);
    expect(component.hasCharacterLimit).toBe(false);
    expect(component.minHeightInPx).toBe(150);
    expect(component.placeholderText).toBe('');
    expect(component.richText).toBe('');
    expect(component.characterCount).toBe(0);
    expect(component.render).toBe(false);
  });

  it('should expose the max character length constant', () => {
    expect(component.RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH).toBe(2000);
  });

  describe('init config', () => {
    it('should set entity_encoding to raw', () => {
      expect(component.init.entity_encoding).toBe('raw');
    });

    it('should set height from minHeightInPx', () => {
      expect(component.init.height).toBe(150);
    });

    it('should use a custom minHeightInPx when provided', () => {
      component.minHeightInPx = 300;
      component.ngOnInit();
      expect(component.init.height).toBe(300);
    });

    it('should set placeholder from placeholderText', () => {
      component.placeholderText = 'Type your response here';
      component.ngOnInit();
      expect(component.init.placeholder).toBe('Type your response here');
    });

    it('should disable inline mode', () => {
      expect(component.init.inline).toBe(false);
    });

    it('should disable menubar', () => {
      expect(component.init.menubar).toBe(false);
    });

    it('should disable relative_urls', () => {
      expect(component.init.relative_urls).toBe(false);
    });

    it('should disable convert_urls', () => {
      expect(component.init.convert_urls).toBe(false);
    });

    it('should enable resize', () => {
      expect(component.init.resize).toBe(true);
    });

    it('should include required plugins', () => {
      const plugins: string[] = component.init.plugins;
      expect(plugins).toContain('lists');
      expect(plugins).toContain('link');
      expect(plugins).toContain('image');
      expect(plugins).toContain('autoresize');
      expect(plugins).toContain('table');
    });
  });

  describe('renderEditor', () => {
    it('should set render to true when viewport event is visible', () => {
      expect(component.render).toBe(false);
      component.renderEditor({ visible: true });
      expect(component.render).toBe(true);
    });

    it('should not set render to true when viewport event is not visible', () => {
      component.renderEditor({ visible: false });
      expect(component.render).toBe(false);
    });

    it('should not reset render once it is set to true', () => {
      component.renderEditor({ visible: true });
      component.renderEditor({ visible: false });
      expect(component.render).toBe(true);
    });
  });

  describe('richTextChange output', () => {
    it('should emit richTextChange when triggered', () => {
      const emitted: string[] = [];
      component.richTextChange.subscribe((v: string) => emitted.push(v));
      component.richTextChange.emit('Hello world');
      expect(emitted).toEqual(['Hello world']);
    });

    it('should emit empty string', () => {
      const emitted: string[] = [];
      component.richTextChange.subscribe((v: string) => emitted.push(v));
      component.richTextChange.emit('');
      expect(emitted).toEqual(['']);
    });
  });

  describe('character count display', () => {
    it('should not show character count when hasCharacterLimit is false', () => {
      component.hasCharacterLimit = false;
      fixture.detectChanges();
      const el: HTMLElement = fixture.nativeElement;
      expect(el.textContent).not.toContain('characters left');
    });

    it('should show full characters remaining when characterCount is 0', () => {
      component.hasCharacterLimit = true;
      component.characterCount = 0;
      fixture.detectChanges();
      const el: HTMLElement = fixture.nativeElement;
      expect(el.textContent).toContain('2000 characters left');
    });

    it('should show reduced characters remaining for non-zero characterCount', () => {
      component.hasCharacterLimit = true;
      component.characterCount = 500;
      fixture.detectChanges();
      const el: HTMLElement = fixture.nativeElement;
      expect(el.textContent).toContain('1500 characters left');
    });

    it('should show 0 characters left when count equals the limit', () => {
      component.hasCharacterLimit = true;
      component.characterCount = 2000;
      fixture.detectChanges();
      const el: HTMLElement = fixture.nativeElement;
      expect(el.textContent).toContain('0 characters left');
    });

    it('should show 0 characters left when count exceeds the limit', () => {
      component.hasCharacterLimit = true;
      component.characterCount = 2500;
      fixture.detectChanges();
      const el: HTMLElement = fixture.nativeElement;
      expect(el.textContent).toContain('0 characters left');
    });
  });
});
