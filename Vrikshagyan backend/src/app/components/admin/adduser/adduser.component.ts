
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { JwtService } from '../../../service/jwt.service';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-adduser',
  templateUrl: './adduser.component.html',
  styleUrls: ['./adduser.component.css']
})
export class AdduserComponent implements OnInit {
  addUserForm!: FormGroup;
  getprovincesdata: any[] = [];
  getdistrictdata: any[] = [];
  getmunicipalitydata: any[] = [];
  getroles: any[] = [];
  selectedImage: string | ArrayBuffer | null = null;
  errorMessage: string = '';

  constructor(private jwtService: JwtService, private fb: FormBuilder, private router: Router) {}

  ngOnInit() {
    this.addUserForm = this.fb.group({
      firstName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      middleName: [null, [Validators.minLength(3), Validators.maxLength(30)]],
      lastName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      address: this.fb.group({
        province: [null, [Validators.required]],
        district: [null, [Validators.required]],
        municipality: [null, [Validators.required]],
        wardNumber: [null, [Validators.required, Validators.pattern('^[0-9]*$'), Validators.minLength(1), Validators.maxLength(100)]],
        tole: [null, [Validators.required]]
      }),
      phone: [null, [Validators.required, Validators.minLength(10), Validators.maxLength(10), Validators.pattern('^[0-9]{10}$')]],
      email: [null, [Validators.required, Validators.email, Validators.maxLength(100)]],
      password: [null, [Validators.required]],
      occupation: [null, [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      role: [null, [Validators.required]],
      status: [1]
    });

    this.jwtService.getRoles().subscribe(roles => {
      this.getroles = roles;
    });

    this.jwtService.getProvinceData().subscribe(data => {
      this.getprovincesdata = data;
    });

    this.addUserForm.get('address.province')?.valueChanges.subscribe(provinceId => {
      if (provinceId) {
        this.jwtService.getDistrictData(provinceId).subscribe(data => {
          this.getdistrictdata = data;
        });
      } else {
        this.getdistrictdata = [];
      }
    });

    this.addUserForm.get('address.district')?.valueChanges.subscribe(districtId => {
      if (districtId) {
        this.jwtService.getMunicipalityData(districtId).subscribe(data => {
          this.getmunicipalitydata = data;
        });
      } else {
        this.getmunicipalitydata = [];
      }
    });
  }

  onSubmit(): void {
    if (this.addUserForm.valid) {
      this.jwtService.registerUser(this.addUserForm.value).pipe(
        catchError((error) => {
          this.handleServerError(error);
          return of(null); // Return observable to prevent propagation of error
        })
      ).subscribe(
        (response) => {
          if (response) {
            console.log('User registered successfully:', response);
            alert('User created successfully');
            this.addUserForm.reset();
            this.errorMessage = ''; // Clear any previous errors
          }
        }
      );
    } else {
      console.error('Form validation failed');
      this.errorMessage = 'Please fill out all required fields correctly.';
      this.markFormGroupTouched(this.addUserForm);
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
    if (errorResponse.email) {
      this.addUserForm.get('email')?.setErrors({ serverError: errorResponse.email });
    }
    if (errorResponse.phone) {
      this.addUserForm.get('phone')?.setErrors({ serverError: errorResponse.phone });
    }
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
        const control = this.addUserForm.get(key);
        if (control) {
          control.setErrors({ serverError: errors[key] });
        }
      }
    }
  }

  onFileChange(event: any): void {
    const file = event.target.files[0]; // Get the first file

    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const result = e.target?.result;
        if (result) {
          this.selectedImage = result;
        }
      };
      reader.readAsDataURL(file);
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
