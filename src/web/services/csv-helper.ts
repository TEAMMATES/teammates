const LINE_SEPARATOR = '\r\n';

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
    return rows
      .map((columns: string[]) =>
        columns
          .map((entry: string) => {
            if (entry.includes('\r') || entry.includes('\n') || entry.includes(',') || entry.includes('"')) {
              return `"${entry.replaceAll('"', '""')}"`;
            }
            return entry;
          })
          .join(','),
      )
      .join(LINE_SEPARATOR);
  }
}
