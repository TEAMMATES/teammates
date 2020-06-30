/**
 * String related utility functions.
 */
export class StringHelper {

  /**
   * Get the human-readable text from a HTML string.
   */
  static getTextFromHtml(html: string): string {
    const domParser: DOMParser = new DOMParser();
    const document: Document = domParser.parseFromString(html, 'text/html');
    const text: string = document.documentElement.innerText || document.documentElement.textContent || html;
    // replace all line breaks also
    return text.replace(/\r?\n|\r/g, ' ');
  }

  /**
   * Converts img tags in html to absolute links
   */
  static convertImageToLinkInHtml(html: string): string {
    const domParser: DOMParser = new DOMParser();
    const document: Document = domParser.parseFromString(html, 'text/html');
    const imgElements: HTMLCollectionOf<HTMLImageElement> = document.getElementsByTagName('img');
    if (!imgElements.length) {
      return '';
    }

    let imgLinks: string = ' Images Link: ';
    for (let i: number = 0; i < imgElements.length; i += 1) {
      const img: HTMLImageElement | null = imgElements.item(i);
      if (!img) {
        continue;
      }
      imgLinks += `${img.src} `;
    }
    return imgLinks;
  }

  /**
   * Trims the string and reduces consecutive white spaces to only one space.
   * Example: " a   a  " --> "a a".
   * @return processed string, returns null if parameter is null
   */
  static removeExtraSpace(str: string): string {
    return str.trim().replace(/\s+/g, ' ');
  }

  /**
   * Converts an integer to alphabetical form (base26).
   * <br>
   * 1 - a
   * 2 - b
   * ...
   * 26 - z
   * 27 - aa
   * 28 - ab
   * ...
   * http://stackoverflow.com/questions/11969840/how-to-convert-a-base-10-number-to-alphabetic-like-ordered-list-in-html
   *
   * @param n - number to convert
   */
  static integerToLowerCaseAlphabeticalIndex(n: number): string {
    let result: string = '';
    let n0: number = n;
    while (n0 > 0) {
      n0 -= 1; // 1 => a, not 0 => a
      const remainder: number = n0 % 26;
      const digit: string = String.fromCharCode(remainder + 97);
      result += digit;
      n0 = (n0 - remainder) / 26;
    }
    return result;
  }
}
