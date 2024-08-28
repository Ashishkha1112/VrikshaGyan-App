import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PlantService } from '../../../service/plant.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-updateplant',
  templateUrl: './updateplant.component.html',
  styleUrl: './updateplant.component.css'
})
export class UpdateplantComponent {
  updatePlantForm!: FormGroup;
  isEditable: boolean = false;
  plant: any;
  plantId!:  number;
  errorMessage: string = '';
  plantImageBase64: string[] = [];
  isModalOpen: boolean = false;
  selectedImageUrl: string = '';

  constructor(
    private router: Router,
    private plantService : PlantService,
    private fb: FormBuilder,
    private http: HttpClient,
    private route: ActivatedRoute,
  ){}
  ngOnInit(): void {
    this.initializeForm();
    this.route.params.subscribe(params =>{
      this.plantId = +params['id'];
      this.plantService.getPlantById(this.plantId).subscribe(response =>{
        this.plant =response.data;
        console.log(this.plant);
        this.plantImageBase64 = this.plant?.imagePaths || [];
        this.patchFormValues();
      })
    });
    
  }
 
  private initializeForm(): void{
    this.updatePlantForm = this.fb.group({
      englishName: [{value: null, disabled: !this.isEditable}, Validators.required],
      nepaliName: [{value: null, disabled: !this.isEditable}, Validators.required],
      tharuName: [{value: null, disabled: !this.isEditable}],
      localName: [{value: null, disabled: !this.isEditable}],
      scientificName: [{value: null, disabled: !this.isEditable}],
      plantCategory: [{value: null, disabled: !this.isEditable}, Validators.required],
      partUsed: [{value: null, disabled: !this.isEditable}, Validators.required],
      normalUses: [{value: null, disabled: !this.isEditable}, Validators.required],
      traditionalUse: [{value: null, disabled: !this.isEditable}],
      medicalUses: [{value: null, disabled: !this.isEditable}],
      preparationType: [{value: null, disabled: !this.isEditable}],
      plantHeight:[{value: null, disabled: !this.isEditable}],
      status: [{value: null, disabled: !this.isEditable}, Validators.required],
      description:[{value: null, disabled: !this.isEditable}, Validators.required]
    //  images: [{value: null, disabled: !this.isEditable}]
    });
  }
  private patchFormValues(): void{
    this.updatePlantForm.patchValue({
      englishName: this.plant?.englishName,
      nepaliName: this.plant?.nepaliName,
      tharuName: this.plant?.tharuName,
      localName: this.plant?.localName,
      scientificName: this.plant?.scientificName,
      plantCategory: this.plant?.plantCategory,
      partUsed: this.plant?.partUsed,
      normalUses: this.plant?.normalUses,
      traditionalUse: this.plant?.traditionalUse,
      medicalUses: this.plant?.medicalUses,
      preparationType: this.plant?.preparationType,
      plantHeight: this.plant?.plantHeight,
      status: this.plant?.status,
      description: this.plant?.description,
      images: this.plant?.images
    });
  }

  toggleEdit() {
    this.isEditable = !this.isEditable;
    Object.keys(this.updatePlantForm.controls).forEach(field => {
      const control = this.updatePlantForm.get(field);
      if (this.isEditable) {
        control?.enable();
      } else {
        control?.disable();
      }
    });
  }
  onSubmit() {
  //  console.log(this.updatePlantForm.value);
    if(this.updatePlantForm.valid){
      this.plantService.updatePlant(this.plantId, this.updatePlantForm.value).pipe(
        catchError((error) => {
          this.handleServerError(error);
          return of(null);
        })
      ).subscribe((response)=>{
        if(response){
          console.log('plant Added succesfully', response);
          this.router.navigate(['/vrikshagyan/viewplant']);
        }
      });
    }
    else{
      console.error('Form Validation failed');
      this.errorMessage = 'Please fill out all required fields correctly.';
      this.markFormGroupTouched(this.updatePlantForm);
    }
  }
   
  handleServerError(error: any): void {
    if (error.status === 400) {
      this.handleValidationError(error.error);
    } else {
      // Handle other types of errors (e.g., server errors)
      this.errorMessage = 'An error occurred. Please try again later.';
      this.clearErrorMessage(1000);
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
        const control = this.updatePlantForm.get(key);
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

  openModal(base64Image: string): void {
    this.selectedImageUrl = 'data:image/jpeg;base64,' + base64Image;
    this.isModalOpen = true;
  }
  

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedImageUrl = '';
  }
}
