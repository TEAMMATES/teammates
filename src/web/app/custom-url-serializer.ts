import { DefaultUrlSerializer, UrlSerializer, UrlTree } from '@angular/router';

/**
 * Custom Url Serializer to handle plus sign related issues
 */
export class CustomUrlSerializer implements UrlSerializer {
  parse(url: any): UrlTree {
    const dus: DefaultUrlSerializer = new DefaultUrlSerializer();
    return dus.parse(url.replace(/\+/g, '%2B'));
  }

  serialize(tree: UrlTree): any {
    const dus: DefaultUrlSerializer = new DefaultUrlSerializer();
    return dus.serialize(tree).replace(/%2B/g, '+');
  }
}
