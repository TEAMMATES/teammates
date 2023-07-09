import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TINYMCE_BASE_URL } from './tinymce';

const RICH_TEXT_EDITOR_MAX_WORD_LENGTH = 500;

const SPACE_KEYCODE = 32;

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
  RICH_TEXT_EDITOR_MAX_WORD_LENGTH: number = RICH_TEXT_EDITOR_MAX_WORD_LENGTH;

  @Input()
  isDisabled: boolean = false;

  @Input()
  minHeightInPx: number = 150;

  @Input()
  placeholderText: string = '';

  @Input()
  richText: string = '';

  @Output()
  richTextChange: EventEmitter<string> = new EventEmitter();

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
        editor.on('keypress', (event:any) => {
          const wordCountApi = editor.plugins.wordcount;
          const wordCount = wordCountApi.body.getWordCount();
          if (wordCount >= RICH_TEXT_EDITOR_MAX_WORD_LENGTH && event.keyCode === SPACE_KEYCODE) {
            event.preventDefault();
          }
        });
      },
    };
  }

  renderEditor(event: any): void {
    // If the editor has not been rendered before, render it once it gets into the viewport
    // However, do not destroy it when it gets out of the viewport
    if (event.visible) {
      this.render = true;
    }
  }

}
