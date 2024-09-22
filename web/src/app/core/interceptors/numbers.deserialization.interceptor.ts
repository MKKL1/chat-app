import {HttpInterceptorFn, HttpResponse} from '@angular/common/http';
import {map} from "rxjs";

/**
 * ID's provided by backend are too big to be properly mapped to javascript numbers
 * Because of this 46173257522479104 becomes 46173257522479100
 * To prevent this ridiculous behaviour after getting response from api this interceptor
 * is mapping every number into BigInt type which can handle such big numbers
 * HOWEVER BigInt cannot be serialized to json, so it has to be cast to string
**/

// thank you javascript
export const numbersDeserializationInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    map(event => {
      if (event instanceof HttpResponse && event.body) {
        const transformBigInt = (data: any): any => {
          if (typeof data === 'number' && Number.isInteger(data)) {
            return BigInt(data).toString();
          } else if (Array.isArray(data)) {
            return data.map(item => transformBigInt(item));
          } else if (typeof data === 'object' && data !== null) {
            const newObj: any = {};
            for (const key of Object.keys(data)) {
              newObj[key] = transformBigInt(data[key]);
            }
            return newObj;
          }
          return data;
        };

        const transformedBody = transformBigInt(event.body);
        return event.clone({ body: transformedBody });
      }
      return event;
    })
  );
};
