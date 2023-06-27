import { Injectable } from "@angular/core";
import {HttpClient} from "@angular/common/http";

@Injectable({
    providedIn: 'root'
  })
  export class ImageService {
    base_url:string="http://localhost:8080/";

    constructor(private http:HttpClient) {
      
    }

    saveImage(imageData:File){
      const formData = new FormData();
      formData.append(
        'file', 
        imageData
      );
      return this.http.post(this.base_url + "images", formData);
    }

    getFirstImage(cuts:number) {
      return this.http.get(this.base_url + 'images/'+ cuts);
    }

    getSolve(){
      return this.http.get(this.base_url + 'solve');
    }
    
  }
  