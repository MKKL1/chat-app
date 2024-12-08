import {Observable} from "rxjs";

export interface EventConnection {
  connect(): void;
  requestStream<T>(path: string): Observable<T>;
}
