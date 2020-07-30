import { DefaultUrlSerializer, UrlTree } from '@angular/router';
import { CustomUrlSerializer } from './custom-url-serializer';

describe('Custom Url Serializer', () => {

  let serializer: CustomUrlSerializer;
  let dus: DefaultUrlSerializer;

  it('should convert plus sign to white space using default url serializer', () => {
    dus = new DefaultUrlSerializer();
    const urlTree: UrlTree =
        dus.parse('/localhost:8080/web/instructor/sessions/edit?courseid=C01&fsname=test+session');
    const url: string = dus.serialize(urlTree);
    expect(url).toEqual('/localhost:8080/web/instructor/sessions/edit?courseid=C01&fsname=test%20session');
  });

  it('should preserve the plus sign in the url using custom url serializer', () => {
    serializer = new CustomUrlSerializer();
    const urlTree: UrlTree =
        serializer.parse('/localhost:8080/web/instructor/sessions/edit?courseid=C01&fsname=test+session');
    const url: string = serializer.serialize(urlTree);
    expect(url).toEqual('/localhost:8080/web/instructor/sessions/edit?courseid=C01&fsname=test+session');
  });

});
