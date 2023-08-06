import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TINYMCE_BASE_URL } from './tinymce';

const RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH = 2000;

/**
 * A rich text editor.
 */
@Component({
  selector: 'tm-rich-text-editor',
  templateUrl: './rich-text-editor.component.html',
  styleUrls: ['./rich-text-editor.component.scss'],
})
export class RichTextEditorComponent implements OnInit {

  // const
  RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH: number = RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH;

  @Input()
  isDisabled: boolean = false;

  @Input()
  hasCharacterLimit: boolean = false;

  @Input()
  minHeightInPx: number = 150;

  @Input()
  placeholderText: string = '';

  @Input()
  richText: string = '';

  @Output()
  richTextChange: EventEmitter<string> = new EventEmitter();

  characterCount: number = 0;

  // the argument passed to tinymce.init() in native JavaScript
  init: any = {};

  render: boolean = false;

  defaultToolbar: string = 'styles | forecolor backcolor '
      + '| bold italic underline strikethrough subscript superscript '
      + '| alignleft aligncenter alignright alignjustify '
      + '| bullist numlist | link image charmap emoticons';

  ngOnInit(): void {
    this.init = this.getEditorSettings();
  }

  private getEditorSettings(): any {
    return {
      base_url: TINYMCE_BASE_URL,
      skin_url: `${TINYMCE_BASE_URL}/skins/ui/oxide`,
      content_css: '/assets/tinymce/tinymce.css',
      suffix: '.min',
      height: this.minHeightInPx,
      resize: true,
      inline: false,
      relative_urls: false,
      convert_urls: false,
      remove_linebreaks: false,
      placeholder: this.placeholderText,
      plugins: [
        'advlist', 'autolink', 'autoresize', 'lists', 'link', 'image', 'charmap', 'anchor',
        'searchreplace', 'wordcount', 'visualblocks', 'visualchars', 'code',
        'insertdatetime', 'nonbreaking', 'save', 'table', 'directionality',
        'emoticons',
      ],
      menubar: false,
      autoresize_bottom_margin: 50,

      toolbar1: this.defaultToolbar,
      setup: (editor:any) => {
        if (this.hasCharacterLimit) {
          editor.on('GetContent', () => {
            setTimeout(() => {
              this.characterCount = this.getCurrentCharacterCount(editor);
            }, 0);
          });
          editor.on('keypress', (event:any) => {
            const currentCharacterCount = this.getCurrentCharacterCount(editor);
            if (currentCharacterCount >= RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH) {
              event.preventDefault();
            }
          });
          editor.on('paste', (event: any) => {
            const contentBeforePasteEvent = editor.getContent({ format: 'text' });
            setTimeout(() => {
              const currentCharacterCount = this.getCurrentCharacterCount(editor);
              if (currentCharacterCount >= RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH) {
                event.preventDefault();
                const contentAfterPasteEvent = editor.getContent({ format: 'text' });
                let firstDifferentIndex = 0;
                while (contentBeforePasteEvent[firstDifferentIndex] === contentAfterPasteEvent[firstDifferentIndex]) {
                  firstDifferentIndex += 1;
                }
                const contentBeforeFirstDifferentIndex = contentBeforePasteEvent.substring(0, firstDifferentIndex);
                const contentAfterFirstDifferentIndex = contentBeforePasteEvent.substring(firstDifferentIndex);
                const lengthExceed = currentCharacterCount - RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH;
                const pasteContentLength = contentAfterPasteEvent.length - contentBeforePasteEvent.length;
                const pasteContent = contentAfterPasteEvent.substring(
                  firstDifferentIndex,
                  firstDifferentIndex + pasteContentLength,
                );
                const truncatedPastedText = pasteContent.substring(0, pasteContentLength - lengthExceed);
                const finalContent = contentBeforeFirstDifferentIndex + truncatedPastedText
                  + contentAfterFirstDifferentIndex;
                editor.setContent(finalContent);

                // This sets the cursor to the end of the text.
                const selection = editor.selection.getRng();
                const newCursorPosition = firstDifferentIndex + truncatedPastedText.length;
                const newRange = editor.dom.createRng();
                newRange.setStart(selection.startContainer, newCursorPosition);
                newRange.collapse(true);
                editor.selection.setRng(newRange);
              }
            }, 0);
          });
        }
      },
    };
  }

  getCurrentCharacterCount(editor: any): number {
    const wordCountApi = editor.plugins.wordcount;
    const currentCharacterCount = wordCountApi.body.getCharacterCount();
    return currentCharacterCount;
  }

  renderEditor(event: any): void {
    // If the editor has not been rendered before, render it once it gets into the viewport
    // However, do not destroy it when it gets out of the viewport
    if (event.visible) {
      this.render = true;
    }
  }

}
