import { Injectable } from '@angular/core';
import {User} from "../../features/models/user";
import {BehaviorSubject, Observable, tap} from "rxjs";
import {KeycloakService} from "keycloak-angular";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../../environment";
import {filePathMapping} from "../../shared/utils/utils";
import {Permission} from "../../features/models/permission";
import {overwriteBasePermission} from "../../shared/utils/binaryOperations";

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

  private permissionSubject: BehaviorSubject<Permission> = new BehaviorSubject<Permission>(
    new Permission("0"));

  public permissions$: Observable<Permission> = this.permissionSubject.asObservable();

  constructor(private http: HttpClient, private keycloakService: KeycloakService) {
  }

  public getUser(): User {
    return this.userSubject.value;
  }

  public setUser(user: User): void {
    user.imageUrl = filePathMapping(user.imageUrl!);
    this.userSubject.next(user);
  }

  public getPermission(): Permission {
    return this.permissionSubject.value;
  }

  public setPermission(permission: Permission): void {
    // todo change user permissions
    this.permissionSubject.next(permission);
  }

  fetchUserData(){
      this.http.get<User>(this.api + "/me").subscribe({
        next: (user) => {
          this.setUser(user);
        },
        error: (err: HttpErrorResponse) => {
          // if backend returns error with status 419, it means user is not fully registered yet
          // to complete registration another request to backend must be sent
          if(err.status === this.errorStatus){
            this.registerUser().subscribe(res => {
              // after registering user, we are trying to
              // get user data from api once again
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
    return this.http.patch<User>(this.api, {description: description});
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

  updateUserPermissions(basePermission: string, permissions: string[]){
    let accumulatedPermissions = 0n;
    for(let i = 0; i < permissions.length; i++){
      accumulatedPermissions |= BigInt(permissions[i]);
    }

    // Krystian should review this
    // example
    // 00111110 overwritten bits
    // 11100000 base permission bits
    // 00111110 result

    const currentPermissions = overwriteBasePermission(accumulatedPermissions, BigInt(basePermission));

    console.log(currentPermissions.toString(2));
    console.log(currentPermissions);

    this.setPermission(new Permission(currentPermissions));
  }

}
