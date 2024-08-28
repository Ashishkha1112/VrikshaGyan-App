import { Component, EventEmitter, Output } from '@angular/core';
import { JwtService } from '../../../service/jwt.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-changeprofilepicture',
  templateUrl: './changeprofilepicture.component.html',
  styleUrls: ['./changeprofilepicture.component.css']
})
export class ChangeprofilepictureComponent {

  pictureForm!: FormGroup;
  profileImageUrl: string | ArrayBuffer | null = null;
  errorMessage: string = '';

  constructor(private jwtService: JwtService, private fb: FormBuilder, private router : Router) {
    this.pictureForm = this.fb.group({
      file: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.pictureForm.valid) {
      const formData = new FormData();
      formData.append('file', this.pictureForm.get('file')!.value);

      this.jwtService.updateProfilePicture(formData).subscribe(
        response => {
          alert('Profile picture updated successfully');
           // Navigate and reload the page
          this.router.navigate(['/vrikshagyan/dashboard']).then(() => {
            window.location.reload();
          });
        },
        error => {
          alert('Failed to update profile picture. Please try again.');
          this.errorMessage = 'Failed to update profile picture. Please try again.';
          console.error(error);
        }
      );
    }
  }

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.pictureForm.patchValue({
        file: file
      });

      const reader = new FileReader();
      reader.onload = () => {
        this.profileImageUrl = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }
}
