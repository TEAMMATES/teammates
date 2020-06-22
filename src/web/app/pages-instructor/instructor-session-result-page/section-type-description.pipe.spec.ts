import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';
import { SectionTypeDescriptionPipe } from './section-type-description.pipe';

describe('SectionTypeDescriptionPipe', () => {
  it('create an instance', () => {
    const pipe: SectionTypeDescriptionPipe = new SectionTypeDescriptionPipe();
    expect(pipe).toBeTruthy();
  });

  it('should generate description for SectionType.BOTH', () => {
    const pipe: SectionTypeDescriptionPipe = new SectionTypeDescriptionPipe();
    expect(pipe.transform(InstructorSessionResultSectionType.BOTH))
        .toEqual('Show response only if both are in the selected section');
  });

  it('should generate description for SectionType.EITHER', () => {
    const pipe: SectionTypeDescriptionPipe = new SectionTypeDescriptionPipe();
    expect(pipe.transform(InstructorSessionResultSectionType.EITHER))
        .toEqual('Show response if either the giver or evaluee is in the selected section');
  });

  it('should generate description for SectionType.EVALUEE', () => {
    const pipe: SectionTypeDescriptionPipe = new SectionTypeDescriptionPipe();
    expect(pipe.transform(InstructorSessionResultSectionType.EVALUEE))
        .toEqual('Show response if the evaluee is in the selected section');
  });

  it('should generate description for SectionType.GIVER', () => {
    const pipe: SectionTypeDescriptionPipe = new SectionTypeDescriptionPipe();
    expect(pipe.transform(InstructorSessionResultSectionType.GIVER))
        .toEqual('Show response if the giver is in the selected section');
  });

  it('should generate unknown for unknown SectionType', () => {
    const pipe: SectionTypeDescriptionPipe = new SectionTypeDescriptionPipe();
    expect(pipe.transform('Unknown' as InstructorSessionResultSectionType))
        .toEqual('Unknown');
  });

});
