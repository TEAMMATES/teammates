import { CustomUrlSerializer } from './custom-url-serializer';
import {DefaultUrlSerializer, UrlTree} from "@angular/router";

describe('Custom Url Serializer', () => {

  let serializer: CustomUrlSerializer;
  let dus: DefaultUrlSerializer;

  beforeEach(() => {
    serializer = new CustomUrlSerializer();
    dus = new DefaultUrlSerializer();
  });

  it('should convert plus sign to white space using default url serializer', () => {
    const urlTree: UrlTree =
        dus.parse('/localhost:8080/web/instructor/sessions/edit?courseid=C01&fsname=test+session');
    const url: string = dus.serialize(urlTree);
    expect(url).toEqual('/localhost:8080/web/instructor/sessions/edit?courseid=C01&fsname=test%20session');
  });

  it('should reserve the plus sign in the url using custom url serializer', () => {
    const urlTree: UrlTree =
        serializer.parse('/localhost:8080/web/instructor/sessions/edit?courseid=C01&fsname=test+session');
    const url: string = serializer.serialize(urlTree);
    expect(url).toEqual('/localhost:8080/web/instructor/sessions/edit?courseid=C01&fsname=test+session');
  });

});