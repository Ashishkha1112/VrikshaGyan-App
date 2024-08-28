import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { JwtService } from '../../../service/jwt.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-changeprofilepassword',
  templateUrl: './changeprofilepassword.component.html',
  styleUrl: './changeprofilepassword.component.css'
})
export class ChangeprofilepasswordComponent implements OnInit {

  

  changePasswordForm !: FormGroup;
  submitted = false;
  successMessage = '';
  errorMessage = '';

  constructor(private jwtService: JwtService, private router: Router, private fb: FormBuilder){
  
    this.changePasswordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(5)]],
      confirmPassword: ['', Validators.required]
    }, {
      validator: this.mustMatch('newPassword', 'confirmPassword')
    });

  }

  ngOnInit(): void {
    
  }
    // Custom validator to check if newPassword and confirmPassword fields match
    mustMatch(controlName: string, matchingControlName: string) {
      return (formGroup: FormGroup) => {
        const control = formGroup.controls[controlName];
        const matchingControl = formGroup.controls[matchingControlName];
        
        if (matchingControl.errors && !matchingControl.errors?.['mustMatch']) {
          return;
        }
  
        if (control.value !== matchingControl.value) {
          matchingControl.setErrors({ mustMatch: true });
        } else {
          matchingControl.setErrors(null);
        }
      };
    }
    onSubmit(): void{
      
      this.submitted = true;

    if (this.changePasswordForm.invalid) {
      return;
    }

    this.jwtService.changePassword(this.changePasswordForm.value).subscribe(
      response => {
        this.successMessage = 'Password changed successfully';
        alert('Password changed successfully');
        this.changePasswordForm.reset();
        this.router.navigate(['/vrikshagyan']);
        this.errorMessage = '';
      },
      error => {
        this.errorMessage = 'Failed to change password';
        this.changePasswordForm.reset();
        this.successMessage = '';
      }
    );
  }
}
