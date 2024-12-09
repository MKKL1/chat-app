import {ErrorHandler, Injectable} from "@angular/core";

@Injectable()
export class GlobalErrorHandler extends ErrorHandler {
  override handleError(error: any) {
    console.error(error);
  }
}
