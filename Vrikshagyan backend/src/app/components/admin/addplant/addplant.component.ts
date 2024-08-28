import { Component, OnInit } from '@angular/core';
import { FormBuilder,FormGroup, FormControl, Validators } from '@angular/forms';
import { PlantService } from '../../../service/plant.service';
import { Router } from '@angular/router';
import { catchError, of } from 'rxjs';


@Component({
  selector: 'app-addplant',
  templateUrl: './addplant.component.html',
  styleUrl: './addplant.component.css'
})
export class AddplantComponent implements OnInit {
  //Decalaration Section
   addPlantForm!: FormGroup;
   selectedFiles!: FileList;
   errorMessage: string = '';

   //Constructor Section
  constructor(private plantService: PlantService,private fb: FormBuilder, private router: Router){}
  ngOnInit(): void {
    this.addPlantForm = this.fb.group({
      englishName: [null, [Validators.required]],
      nepaliName: [null, [Validators.required]],
      tharuName: [null],
      localName: [null],
      scientificName: [null],
      plantCategory: [null, [Validators.required]],
      partUsed: [null, [Validators.required]],
      normalUses: [null, [Validators.required]],
      traditionalUse: [null],
      medicalUses: [null],
      preparationType: [null],
      plantHeight:[null],
      status: [1, [Validators.required]],
      description:[null,[Validators.required]],
      images: [null, [Validators.required]]
    });
  }
  onFileChange(event: any): void {
    this.selectedFiles = event.target.files;
  }

  onSubmit() {
    if(this.addPlantForm.valid){
      this.plantService.addPlant(this.addPlantForm.value,this.selectedFiles).pipe(
        catchError((error) =>{
          this.handleServerError(error);
          return of(null);
        })
      ).subscribe((response) => {
        if(response){
          console.log('plant Added succesfully', response);
          alert('Plant Added Successfully');
          this.addPlantForm.reset();
          this.errorMessage = ''; // Clear any previous errors
          this.router.navigate(['/vrikshagyan/viewplant']);
        }
      });
    }else{
      console.error('Form Validation failed');
      this.errorMessage = 'Please fill out all required fields correctly.';
      this.markFormGroupTouched(this.addPlantForm);
    }
  }


  
  handleServerError(error: any): void {
    if (error.status === 400) {
      this.handleValidationError(error.error);
    } else {
      // Handle other types of errors (e.g., server errors)
      this.errorMessage = 'An error occurred. Please try again later.';
      this.clearErrorMessage(100);
    }
  }

  handleValidationError(errorResponse: any): void {
    if (errorResponse.errors) {
      this.setServerErrors(errorResponse.errors); // Implement setServerErrors as per your requirement
    }
    if (errorResponse.message) {
      this.errorMessage = errorResponse.message;
      this.clearErrorMessage(1000);
    }
    if (!errorResponse.email && !errorResponse.phone && !errorResponse.errors && !errorResponse.message) {
      this.errorMessage = 'Validation error occurred.';
      this.clearErrorMessage(1000);
    }
  }

  clearErrorMessage(timeout: number): void {
    setTimeout(() => {
      this.errorMessage = '';
    }, timeout);
  }

  setServerErrors(errors: any): void {
    for (const key in errors) {
      if (errors.hasOwnProperty(key)) {
        const control = this.addPlantForm.get(key);
        if (control) {
          control.setErrors({ serverError: errors[key] });
        }
      }
    }
  }

  // Helper function to mark all form controls as touched
  markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}

