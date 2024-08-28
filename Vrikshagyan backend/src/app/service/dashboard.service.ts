import { DOCUMENT } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';


const BASE_URL = "http://localhost:8080/";

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(private http: HttpClient, @Inject (DOCUMENT) private document: Document) { }

  getUserCount() :Observable<any>{
    return this.http.get(BASE_URL+'api/admin/count');
  }
  getVerifiedUserCount() :Observable<any>{
    return this.http.get(BASE_URL+'api/admin/verified');
  }
  getUnUserCount() :Observable<any>{
    return this.http.get(BASE_URL+'api/admin/unverified');
  }
  getActiveUserCount() :Observable<any>{
    return this.http.get(BASE_URL+'api/admin/activeUser');
  }
  getTotalPlant(): Observable<any> {
    return this.http.get(BASE_URL + 'api/plants/count');
  }
  getVerifiedPlant(): Observable<any> {
    return this.http.get(BASE_URL + 'api/plants/verifiedcount');
  }
  getUnVerifedPlant(): Observable<any> {
    return this.http.get(BASE_URL + 'api/plants/unverifiedcount');
  }
}
