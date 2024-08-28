import { Component, OnInit } from '@angular/core';
import { FormBuilder,ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';
import { JwtService } from '../../../service/jwt.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-updateuser',
  templateUrl: './updateuser.component.html',
  styleUrls: ['./updateuser.component.css']
})
export class UpdateuserComponent implements OnInit {
  updateUserForm!: FormGroup;
  getprovincesdata: any[] = [];
  getdistrictdata: any[] = [];
  getmunicipalitydata: any[] = [];
  userId!: number;
  user: any;
  profile: any;
  getroles: any[] = [];
  isEditable: boolean = false;
  profileImageUrl: any;
  errorMessage: string = '';
  url: any;
  selectedImageUrl: string = '';
  isModalOpen: boolean = false;

  constructor(
    private jwtService: JwtService,
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.route.params.subscribe(params => {
      this.userId = +params['id'];
      this.jwtService.getUserById(this.userId).subscribe(response => {
        this.user = response.data.user;
        this.profile = response.data.profileImage;
       this.profileImageUrl = 'http://localhost:8080/resources/static/profileImages/'+this.profile.profileImage;
        console.log("hello"+this.profileImageUrl);
        this.patchFormValues();
        this.fetchAddressData();
        this.cdr.detectChanges();
      });
    });

    this.jwtService.getRoles().subscribe(roles => {
      this.getroles = roles;
    });

    this.jwtService.getProvinceData().subscribe(data => {
      this.getprovincesdata = data;
    });

    this.updateUserForm.get('address.province')?.valueChanges.subscribe(provinceId => {
      if (provinceId) {
        this.jwtService.getDistrictData(provinceId).subscribe(data => {
          this.getdistrictdata = data;
        });
      } else {
        this.getdistrictdata = [];
      }
    });

    this.updateUserForm.get('address.district')?.valueChanges.subscribe(districtId => {
      if (districtId) {
        this.jwtService.getMunicipalityData(districtId).subscribe(data => {
          this.getmunicipalitydata = data;
        });
      } else {
        this.getmunicipalitydata = [];
      }
    });
  }

  private initializeForm(): void {
    this.updateUserForm = this.fb.group({
   //   uID: [{ value: '', disabled: true }],
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
     /// password: [{ value: null, disabled: !this.isEditable }],
      occupation: [{ value: null, disabled: !this.isEditable }, [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      role: [{ value: null, disabled: !this.isEditable }, [Validators.required]],
      status: [{ value: null, disabled: !this.isEditable }, [Validators.required]],
      profileImage: [{ value: null, disabled: !this.isEditable }]
    });
  }

  private patchFormValues(): void {
    this.updateUserForm.patchValue({
      firstName: this.user.firstName || '',
      middleName: this.user.middleName || '',
      lastName: this.user.lastName || '',
      phone: this.user.phone || '',
      email: this.user.email || '',
      occupation: this.user.occupation || '',
      role: this.user.role || '',
      status: this.user.status || '',
      profileImage: this.profile.profileImage || '',
      address: {
        province: this.user?.address?.province || '',
        district: this.user?.address?.district || '',
        municipality: this.user?.address?.municipality || '',
        wardNumber: this.user.address.wardNumber || '',
        tole: this.user.address.tole || ''
      }
    });
  }

  private fetchAddressData(): void {
    const provinceId = this.user.address.province;
    if (provinceId) {
      this.jwtService.getDistrictData(provinceId).subscribe(data => {
        this.getdistrictdata = data;
        const districtId = this.user.address.district;
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
    Object.keys(this.updateUserForm.controls).forEach(field => {
      const control = this.updateUserForm.get(field);
      if (this.isEditable) {
        control?.enable();
      } else {
        control?.disable();
      }
    });

    const addressControls = this.updateUserForm.get('address') as FormGroup;
    Object.keys(addressControls.controls).forEach(field => {
      const control = addressControls.get(field);
      if (this.isEditable) {
        control?.enable();
      } else {
        control?.disable();
      }
    });
  }

  onSubmit(): void {
    if (this.updateUserForm.valid) {
      console.log(this.updateUserForm);
      const formData = this.updateUserForm.value;
      if (!formData.password) {
        delete formData.password;
      }
      if (!formData.address) {
        delete formData.address;
      }
      this.jwtService.updateUser(this.userId, formData).pipe(
        catchError((error) => {
          this.handleServerError(error);
          return of(null);
        })
      ).subscribe(
        (response) => {
          if (response) {
            console.log('User updated successfully:', response);
            alert('User updated successfully');
            this.router.navigate(['/vrikshagyan/viewuser']);
          }
        }
      );
    } else {
      console.error('Form validation failed');
      this.errorMessage = 'Please fill out all required fields correctly.';
      this.markFormGroupTouched(this.updateUserForm);
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
        const control = this.updateUserForm.get(key);
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
