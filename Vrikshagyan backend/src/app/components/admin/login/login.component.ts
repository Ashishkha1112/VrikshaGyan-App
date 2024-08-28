import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, } from '@angular/forms';
import { JwtService } from '../../../service/jwt.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})


export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  errorMessage: string | null = null;

  constructor(
    private service :JwtService,
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute
  ){

  }

    ngOnInit(): void {

      // Handle query parameters for error messages
      this.route.queryParams.subscribe(params => {
        if (params['error']) {
          // Redirect to login page without query parameters
          this.router.navigate(['/login']).then(() => {
            this.errorMessage = 'Invalid credentials.';
          });
        }
      });

      this.loginForm = this.fb.group({
        username:['',Validators.required],
        password:['',Validators.required]
      });
      
    }

  submitForm(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.service.login(this.loginForm.value).subscribe(
      (response) => {
        if (response.jwtToken) {
          const JWTTOKEN = response.jwtToken;
          window.localStorage.setItem('jwtToken', JWTTOKEN);
          this.router.navigateByUrl('vrikshagyan/dashboard');
        } else {
          this.errorMessage = 'Invalid credentials.';
          this.loginForm.setErrors({ invalidCredentials: true });
        }
      },
      (error) => {
        if (error.status === 401) {
          this.errorMessage = 'Invalid credentials.';
          this.loginForm.setErrors({ invalidCredentials: true });
        } else {
          console.error('Login Failed', error);
          this.errorMessage = 'An error occurred. Please try again.';
        }
      }
    );
  }
    
}