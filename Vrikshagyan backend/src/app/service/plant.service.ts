import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const BASE_URL = "http://localhost:8080/";

@Injectable({
  providedIn: 'root'
})
export class PlantService {

  constructor(private http: HttpClient) { }

  //add plants
  addPlant(plantData: any, files: FileList): Observable<any> {
    const formData: FormData = new FormData();
    // Append plant data
    for (const key in plantData){
      if(plantData.hasOwnProperty(key)){
        formData.append(key, plantData[key]);
      }
    }
    // Append files
    for(let i = 0; i < files.length; i++){
      formData.append('images', files[i]);
    }
    // Send request
    return this.http.post(BASE_URL + 'api/plants/single', formData);
  }

  updatePlant(plantId: number, updatePlantData: any) : Observable<any> {
    return this.http.put(BASE_URL +'api/plants/update/'+plantId, updatePlantData);
  }


  //update is deleted status
  deletePlant(plantId: number): Observable<any> {
    return this.http.put( BASE_URL + 'api/plants/delete/'+ plantId,null);
  }
  //update verified status
  verifyPlant(plantId: number): Observable<any> {
    return this.http.put( BASE_URL + 'api/plant/verify/'+ plantId,null);
  }

  //fetch plants
  getPlants(): Observable<any[]> {
    return this.http.get<any[]>(BASE_URL + 'api/plants/list');
  }
  //fetch deleted plants
  getDeletedPlants(): Observable<any[]> {
    return this.http.get<any[]>(BASE_URL + 'api/plants/deleted');
  }
  //fetch Verified plants
  getVerifiedPlants(): Observable<any[]>{
    return this.http.get<any[]>(BASE_URL +'api/plants/verified')
  }
  //fetch Unverified Plants
  getUnVerifiedPlants(): Observable<any[]>{
    return this.http.get<any[]>(BASE_URL +'api/plants/unverifed')
  }
  //fetch Unverified plants


  // Fetch plant by ID
  getPlantById(plantId:  string | number): Observable<any> {
    return this.http.get<any>(BASE_URL + 'api/plants/editPlant/' + plantId );
  }

   // Bulk upload plants and images
  //  uploadBulkData(formData: FormData): Observable<any> {
  //   return this.http.post(BASE_URL + 'api/plants/bulk', formData);
  // }
  uploadBulkData(FormData: FormData): Observable <any>{
    return this.http.post(BASE_URL + 'api/plants/bulk', FormData, { responseType: 'text' });

  }
}
