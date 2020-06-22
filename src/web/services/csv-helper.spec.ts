import { CsvHelper } from './csv-helper';

describe('CsvHelper', () => {
  it('should escape " in csv properly', () => {
    expect(CsvHelper.convertCsvContentsToCsvString([['aaa ', ' bb"b', ' c""cc']])).toEqual('aaa ," bb""b"," c""""cc"');
    expect(CsvHelper.convertCsvContentsToCsvString([['aaa'], ['bb"b', 'c""cc'], []]))
        .toEqual('aaa\r\n"bb""b","c""""cc"\r\n');
  });

  it('should escape , in csv properly', () => {
    expect(CsvHelper.convertCsvContentsToCsvString([['aaa,cde']])).toEqual('"aaa,cde"');
    expect(CsvHelper.convertCsvContentsToCsvString([['a,,a,,a'], ['bb",,,b']])).toEqual('"a,,a,,a"\r\n"bb"",,,b"');
    expect(CsvHelper.convertCsvContentsToCsvString([[',', ',,']])).toEqual('",",",,"');
  });

  it('should escape CR/LF in csv properly', () => {
    expect(CsvHelper.convertCsvContentsToCsvString([['\r\nabc']])).toEqual('"\r\nabc"');
    expect(CsvHelper.convertCsvContentsToCsvString([['a,,\ra\na,,a'], ['\r\n']])).toEqual('"a,,\ra\na,,a"\r\n"\r\n"');
    expect(CsvHelper.convertCsvContentsToCsvString([['\r\n\r\n"']])).toEqual('"\r\n\r\n"""');
    expect(CsvHelper.convertCsvContentsToCsvString([['\r'], ['\n']])).toEqual('"\r"\r\n"\n"');
  });

  it('should generate proper CSV for empty inputs', () => {
    expect(CsvHelper.convertCsvContentsToCsvString([])).toEqual('');
    expect(CsvHelper.convertCsvContentsToCsvString([['']])).toEqual('');
    expect(CsvHelper.convertCsvContentsToCsvString([['', '']])).toEqual(',');
    expect(CsvHelper.convertCsvContentsToCsvString([[]])).toEqual('');
    expect(CsvHelper.convertCsvContentsToCsvString([[], []])).toEqual('\r\n');
  });
});
