import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { JwtService } from '../../../service/jwt.service';
import { catchError, of } from 'rxjs';
import { Router } from '@angular/router';
import { HttpHeaders } from '@angular/common/http';


@Component({
  selector: 'app-myprofile',
  templateUrl: './myprofile.component.html',
  styleUrls: ['./myprofile.component.css']
})
export class MyprofileComponent implements OnInit {
  profileForm!: FormGroup;
  getprovincesdata: any[] = [];
  getdistrictdata: any[] = [];
  getmunicipalitydata: any[] = [];
  user: any;
  profile: any;
  getroles: any[] = [];
  isEditable: boolean = false;
  profileImageUrl: string | ArrayBuffer | null = null;
  errorMessage: string = '';
  url: any;
  selectedImageUrl: string = '';
  isModalOpen: boolean = false;


  constructor(
    private jwtService: JwtService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.initializeForm();
    this.loadUserProfile();
    this.loadRoles();
    this.loadProvinces();
    this.setupAddressChangeHandlers();
  }

  private initializeForm(): void {
    this.profileForm = this.fb.group({
      userId: [{ value: '', disabled: true }],
      firstName: [{ value: null, disabled: !this.isEditable }, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      middleName: [{ value: null, disabled: !this.isEditable }],
      lastName: [{ value: null, disabled: !this.isEditable }, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      address: this.fb.group({
        province: [{ value: null, disabled: !this.isEditable }, [Validators.required]],
        district: [{ value: null, disabled: !this.isEditable }, [Validators.required]],
        municipality: [{ value: null, disabled: !this.isEditable }, [Validators.required]],
        wardNumber: [{ value: null, disabled: !this.isEditable }, [Validators.required, Validators.pattern('^[0-9]*$'), Validators.minLength(1), Validators.maxLength(100)]],
        tole: [{ value: null, disabled: !this.isEditable }, [Validators.required]]
      }),
      phone: [{ value: null, disabled: true }, [Validators.required, Validators.minLength(10), Validators.maxLength(10), Validators.pattern('^[0-9]{10}$')]],
      email: [{ value: null, disabled: true }, [Validators.required, Validators.email, Validators.maxLength(100)]],
      occupation: [{ value: null, disabled: !this.isEditable }, [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      role: [{ value: null, disabled:true  }],
      status: [{ value: null, disabled:true }],
      profileImage: [{ value: null, disabled: !this.isEditable }]
    });
  }

  private loadUserProfile(): void {
    this.jwtService.getUserProfile().subscribe(
      response => {
        this.user = response.data.user; // Assign the response directly to this.user
        const imageBytes = response.data.profileImage;
        if (imageBytes) {
          this.profileImageUrl = 'data:image/jpeg;base64,' + imageBytes;
          console.log(this.profileImageUrl);
        }
  
        this.patchFormValues();
        this.fetchAddressData();
        this.cdr.detectChanges();
      }, 
      error => {
        console.error('Error fetching user profile:', error);
      }
    );
  }
  private loadRoles(): void {
    this.jwtService.getRoles().subscribe(roles => {
      this.getroles = roles;
    });
  }

  private loadProvinces(): void {
    this.jwtService.getProvinceData().subscribe(data => {
      this.getprovincesdata = data;
    });
  }

  private setupAddressChangeHandlers(): void {
    this.profileForm.get('address.province')?.valueChanges.subscribe(provinceId => {
      if (provinceId) {
        this.jwtService.getDistrictData(provinceId).subscribe(data => {
          this.getdistrictdata = data;
        });
      } else {
        this.getdistrictdata = [];
      }
    });

    this.profileForm.get('address.district')?.valueChanges.subscribe(districtId => {
      if (districtId) {
        this.jwtService.getMunicipalityData(districtId).subscribe(data => {
          this.getmunicipalitydata = data;
        });
      } else {
        this.getmunicipalitydata = [];
      }
    });
  }

  private patchFormValues(): void {
    if (this.user) {
      this.profileForm.patchValue({
        userId: this.user.userId || '',
        firstName: this.user.firstName || '',
        middleName: this.user.middleName || '',
        lastName: this.user.lastName || '',
        phone: this.user.phone || '',
        email: this.user.email || '',
        occupation: this.user.occupation || '',
        role: this.user.role || '',
        status: this.user.status || '',
        profileImage: this.user.profileImage || '',
        address: {
          province: this.user.address?.province || '',
          district: this.user.address?.district || '',
          municipality: this.user.address?.municipality || '',
          wardNumber: this.user.address?.wardNumber || '',
          tole: this.user.address?.tole || ''
        }
      });
    }
  }

  private fetchAddressData(): void {
    const provinceId = this.user?.address?.province;
    if (provinceId) {
      this.jwtService.getDistrictData(provinceId).subscribe(data => {
        this.getdistrictdata = data;
        const districtId = this.user?.address?.district;
        if (districtId) {
          this.jwtService.getMunicipalityData(districtId).subscribe(data => {
            this.getmunicipalitydata = data;
          });
        }
      });
    }
  }

  toggleEdit(): void {
    this.isEditable = !this.isEditable;
    Object.keys(this.profileForm.controls).forEach(field => {
      const control = this.profileForm.get(field);
      if (this.isEditable) {
        control?.enable();
      } else {
        control?.disable();
      }
    });

    const addressControls = this.profileForm.get('address') as FormGroup;
    Object.keys(addressControls.controls).forEach(field => {
      const control = addressControls.get(field);
      if (this.isEditable) {
        control?.enable();
      } else {
        control?.disable();
      }
    });
  }
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.profileImageUrl = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.profileForm.valid) {
      const formData = this.profileForm.value;
     
      if (!formData.address) {
        delete formData.address;
      }
      this.jwtService.updateProfile(this.user.userId, formData).pipe(
        catchError((error) => {
          this.handleServerError(error);
          return of(null);
        })
      ).subscribe(
        (response) => {
          if (response) {
            console.log('Profile updated successfully:', response);
            alert('User updated successfully');
            this.router.navigate(['/vrikshagyan']);
          }
        }
      );
    } else {
      console.error('Form validation failed');
      this.errorMessage = 'Please fill out all required fields correctly.';
      this.markFormGroupTouched(this.profileForm);
    }
  }

  handleValidationError(errorResponse: any): void {
    if (errorResponse.errors) {
      this.setServerErrors(errorResponse.errors);
    }
    if (errorResponse.message) {
      this.errorMessage = errorResponse.message;
      this.clearErrorMessage(1000);
    }
  }

  setServerErrors(errors: any): void {
    for (const key in errors) {
      if (errors.hasOwnProperty(key)) {
        const control = this.profileForm.get(key);
        if (control) {
          control.setErrors({ serverError: errors[key] });
        }
      }
    }
  }

  handleServerError(error: any): void {
    if (error.status === 400) {
      this.handleValidationError(error.error);
    } else {
      this.errorMessage = 'An error occurred. Please try again later.';
      this.clearErrorMessage(1000);
    }
  }

  clearErrorMessage(timeout: number): void {
    setTimeout(() => {
      this.errorMessage = '';
    }, timeout);
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
  openModal(imageUrl: string) {
    this.selectedImageUrl = imageUrl;
    this.isModalOpen = true;
  }
  closeModal(): void {
    this.isModalOpen = false;
    this.selectedImageUrl = '';
  }
}
