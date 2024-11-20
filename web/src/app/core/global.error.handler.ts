import {ErrorHandler, inject, Injectable} from "@angular/core";
import {MatSnackBar} from "@angular/material/snack-bar";

@Injectable()
export class GlobalErrorHandler extends ErrorHandler {
  //private snackBar = inject(MatSnackBar);

  override handleError(error: any) {
    console.error(error);
    //this.snackBar.open(error.statusText, 'Ok', {duration: 5000});

    if(error.status === 401){
      console.error("401");
    }
  }
}
