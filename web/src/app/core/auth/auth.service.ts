import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {LoginForm} from "../../features/pages/login/LoginForm";
import {BehaviorSubject, map, Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authUrl = "";
  private token = "";
  private loggedIn = new BehaviorSubject<boolean>(this.hasToken());

  constructor(private http: HttpClient) { }

  login(request: LoginForm){
    return this.http.post<any>(this.authUrl, { request })
      .pipe(
        map(response => {
          if (response && response.token) {
            this.setToken(response.token);
            this.loggedIn.next(true);
          }
          return response;
        })
      );
  }

  register(){

  }

  logout(): void {
    this.removeToken();
    this.loggedIn.next(false);
  }

  isLoggedIn(): Observable<boolean> {
    return this.loggedIn.asObservable();
  }

  getToken(): string {
    return this.token;
  }

  private setToken(token: string): void {
    localStorage.setItem(this.token, token);
  }

  private removeToken(): void {
    localStorage.removeItem(this.token);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.token);
  }
}
