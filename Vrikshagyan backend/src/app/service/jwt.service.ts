// old onne
import { Inject, Injectable } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { jwtDecode } from "jwt-decode";


const BASE_URL = "http://localhost:8080/";

 
@Injectable({
  providedIn: 'root'
})
export class JwtService {
  private baseUrl: string = 'http://localhost:8080/api';
  constructor(private http: HttpClient, @Inject (DOCUMENT) private document: Document) { }
  
    //method to login
      login(credentials: { username: string; password: string }): Observable<any> {
        return this.http.post<any>(BASE_URL+'authenticate', credentials);
      }
    // creating user
    registerUser(userData: any): Observable<any> {
      console.log(userData);
      return this.http.post(BASE_URL + 'api/signup', userData);
    }
    //updating user
    updateUser(id: number, updateUserData: any) : Observable<any> {
      return this.http.put(BASE_URL +'api/admin/update/'+id,updateUserData);
    }

    //Deleting user
    deleteUser(id: number) : Observable<any> {
        return this.http.put( BASE_URL + 'api/admin/delete/'+ id, null);
    }
    verifyUser(id: number) : Observable<any> {
      return this.http.put( BASE_URL + 'api/admin/verify/'+ id, {}).pipe(
        catchError(this.handleError)
      );
    }
    private handleError(error: HttpErrorResponse) {
      let errorMessage = 'An unknown error occurred!';
      if (error.error instanceof ErrorEvent) {
        // Client-side or network error
        errorMessage = `Error: ${error.error.message}`;
      } else {
        // Backend error
        switch (error.status) {
          case 404:
            errorMessage = 'User not found.';
            break;
          case 409:
            errorMessage = 'User is already verified.';
            break;
          default:
            errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
            break;
        }
      }
      return throwError(() => new Error(errorMessage));
    }
    //get single user
    getUser() {
      const token = localStorage.getItem('jwtToken');
      if (!token) return null;
  
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload;
      } catch (e) {
        console.error('Error parsing token', e);
        return null;
      }
    }

    //list of users
    getUsers(): Observable<any> {
      return this.http.get(BASE_URL + 'api/admin/list');
    }
     
    //get profile image
    getProfileImage(imageName: string) {
      return this.http.get(BASE_URL + 'VrikshaGyanServer/resources/static/profileImages/' + imageName);
    }

     //get all  deleted users
    getDeletedUsers(queryParams?: any): Observable<any> {
      let params = new HttpParams();

      if (queryParams) {
        Object.keys(queryParams).forEach(key => {
          params = params.set(key, queryParams[key]);
        });
      }

      return this.http.get(BASE_URL + 'api/admin/deleteduser', { params });
     }


     // Method to get user by ID
      getUserById(userId: number): Observable<any> {
        return this.http.get<any>(BASE_URL + 'api/admin/editUser/' + userId);
        }
  
  
  //checking if logged in
  isLoggedIn(): boolean {
    const defaultView = this.document.defaultView;
    if (defaultView && defaultView.localStorage) {
      const jwtToken = defaultView.localStorage.getItem('jwtToken');
      return jwtToken !== null;
    }
    return false; // Or any default behavior for server-side
  }
  
  //Getting Roles(Enum)
  getRoles(): Observable<any[]> {
    return this.http.get<any[]>(BASE_URL+'api/roles');
  }
  //extracting address from database
  getProvinceData(): Observable<any[]> {
    return this.http.get<any[]>(BASE_URL+'api/provinces');
  }

  getDistrictData(id: string): Observable<any[]> {
    return this.http.get<any[]>(BASE_URL+'api/provinces/'+id);
  }
  getMunicipalityData(id: string): Observable<any[]>{
    return this.http.get<any[]>(BASE_URL+'api/district/'+id);
  }

  //method to retrive token from local storages
  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }
  //get my profile
  getUserProfile(): Observable<any> {
    return this.http.get<any>(BASE_URL+'api/profile');
  }
//forgotpass  section
getemail(): string | null {
  return localStorage.getItem('email');
}

forgotpass(email: string): Observable <any>{
  const encodedEmail = encodeURIComponent(email);
  return this.http.post<any>(BASE_URL+'api/send/'+ encodedEmail, {});
}

//verifyotp
verifyOTP(otp: string): Observable<any> {
  const email  = localStorage.getItem('email') || '';
  const params = new HttpParams()
    .set('email', email)
    .set('otp', otp);
   return this.http.post(BASE_URL + 'api/verify', {}, { params: params, responseType: 'text' });
}

resetPassword(newPassword: string): Observable <any> {
  const email  = localStorage.getItem('email') || '';
  const body = { email, newPassword };
 return this.http.put(BASE_URL + 'api/change',{email,newPassword},{ responseType: 'text' });
}


//change password from inside
changePassword(password: any): Observable< any > {
  return this.http.post(BASE_URL+ 'api/changePassword', password);
}

//update profile picture
updateProfilePicture(formData: FormData): Observable<any> {
  console.log(formData);
  return this.http.put(BASE_URL + 'api/uploadProfilePicture', formData);
}

//update profile 
updateProfile(userId: number, userDetails: any): Observable<any> {
  return this.http.put(BASE_URL + 'api/admin/update/'+userId, userDetails);
}

// getLoggedInUserId(): number {
//   const token = localStorage.getItem('jwtToken');
//   if (token) {
//     const decodedToken: any = jwt_decode(token);
//     return decodedToken.userId; // Adjust this to match the actual key in your JWT payload
//   }
//   return 0;
// }

// method to decode token and extract roles
decodeToken(token: string): any {
  try {
    return jwtDecode(token);;
  } catch (e) {
    console.error('Error decoding token', e);
    return null;
  }
}
 // method to get user roles
 getUserRoles(): string[] {
  const token = this.getToken();
  if (token) {
    const decodedToken = this.decodeToken(token);
    return decodedToken.role.map((r: any) => r.authority);
  }
  return [];
}

// method to check if user is an admin
isAdmin(): boolean {
  const roles = this.getUserRoles();
  return roles.includes('ROLE_ADMIN');
}

// method to check if token is expired
isTokenExpired(token: string): boolean {
  const decodedToken: any = this.decodeToken(token);
  const expirationDate = decodedToken.exp * 1000;
  return Date.now() > expirationDate;
}
}