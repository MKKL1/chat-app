import { Injectable } from '@angular/core';
import {User} from "../../features/models/user";
import {BehaviorSubject, Observable, tap} from "rxjs";
import {KeycloakService} from "keycloak-angular";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../../environment";
import {filePathMapping} from "../../shared/utils/utils";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly api: string = environment.api + "users";
  private readonly errorStatus: number = 419;

  private userSubject: BehaviorSubject<User> = new BehaviorSubject<User>({
    id: '',
    username: "",
    description: "",
    imageUrl: "",
  });

  public user$: Observable<User> = this.userSubject.asObservable();

  constructor(private http: HttpClient, private keycloakService: KeycloakService) {
  }

  public getUser(): User {
    return this.userSubject.value;
  }

  public setUser(user: User): void {
    user.imageUrl = filePathMapping(user.imageUrl!);
    this.userSubject.next(user);
  }

  fetchUserData(){
      this.http.get<User>(this.api + "/me").subscribe({
        next: (user) => {
          this.setUser(user);
        },
        error: (err: HttpErrorResponse) => {
          // If status 419, complete registration and retry fetching user data
          if(err.status === this.errorStatus){
            this.registerUser().subscribe(res => {
              this.fetchUserData();
            });
          }
        }
      });
  }

  registerUser(): Observable<any>{
    return this.http.post<User>(this.api,
      {username: this.keycloakService.getUsername()});
  }

  editDescription(description: string){
    return this.http.patch<User>(this.api + "/description", {description: description});
  }

  editAvatar(file: File) {
    const formData = new FormData();
    formData.append('file', file, file.name);

    return this.http.patch(this.api + "/avatar", formData).pipe(
      tap((response) => {
        this.setUser(<User>response);
      })
    );
  }

  deleteAccount(){
    this.http.delete(this.api).subscribe(res => {
      this.keycloakService.logout();
    });
  }

}
