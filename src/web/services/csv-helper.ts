
const LINE_SEPARATOR: string = '\r\n';

/**
 * CSV related utility functions.
 */
export class CsvHelper {

  /**
   * Converts CSV contents to a standard CSV string.
   *
   * @param rows list of rows, each row contains elements for each column
   * @see <a href="http://tools.ietf.org/html/rfc4180">http://tools.ietf.org/html/rfc4180</a>
   */
  static convertCsvContentsToCsvString(rows: string[][]): string {
    return rows.map((columns: string[]) => columns.map((entry: string) => {
      if (entry.indexOf('\r') >= 0 || entry.indexOf('\n') >= 0 || entry.indexOf(',') >= 0 || entry.indexOf('"') >= 0) {
        return `"${entry.replace(/"/g, '""')}"`;
      }
      return entry;
    }).join(',')).join(LINE_SEPARATOR);
  }
}
