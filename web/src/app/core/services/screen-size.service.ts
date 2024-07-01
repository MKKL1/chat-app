import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ScreenSizeService {
  private isMobileViewSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public isMobileView$: Observable<boolean> = this.isMobileViewSubject.asObservable();

  setMobileView(isMobileView: boolean): void {
    this.isMobileViewSubject.next(isMobileView);
  }
}
