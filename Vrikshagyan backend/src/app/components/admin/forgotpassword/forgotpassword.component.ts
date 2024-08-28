import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { JwtService } from '../../../service/jwt.service';

@Component({
  selector: 'app-forgotpassword',
  templateUrl: './forgotpassword.component.html',
  styleUrl: './forgotpassword.component.css'
})
export class ForgotpasswordComponent implements OnInit {
  forgotpassform!: FormGroup;
  otpform!: FormGroup;
  newpassform!: FormGroup;
  step = 1;
  email!: string;
  
constructor(
  private service: JwtService,
  private fb: FormBuilder,
  private router: Router
){}

ngOnInit(): void {
  this.forgotpassform = this.fb.group({
    email:['', Validators.required]
  })

  this.otpform = this.fb.group({
    otp:['', Validators.required]
  })

  this.newpassform = this.fb.group({
    newPassword:['', Validators.required]
  })
}
submitForm(): void{
  const email = this.forgotpassform.get('email')?.value;
  localStorage.setItem("email", email);
  this.service.forgotpass(email).subscribe({
    next: () => {
      alert("otp Sent Sucessfully")
      this.step = 2;
    },
    error: err => {
      alert("Enter Valid Email")
      console.error(err);
    }
  });
}

Otpverify(): void{
  const otp = this.otpform.get('otp')?.value;
  this.service.verifyOTP(otp).subscribe({
    next:(response :string) =>{
      alert(response)
      this.step = 3;
    },
    error: err => {
      alert("Enter Valid OTP")
      console.error(err);
      // Handle error appropriately
    }
  });
}
resetPassword() {
  const password = this.newpassform.get('newPassword')?.value;
  this.service.resetPassword(password).subscribe({
    next: () => {
      alert('Password reset successfully');
      localStorage.removeItem(this.email);
      this.router.navigateByUrl('/login');
    },
    error: err => {
      console.error(err);
      alert("Password Reset Failed")
      this.router.navigateByUrl('/login');
    }
  });
}


}
