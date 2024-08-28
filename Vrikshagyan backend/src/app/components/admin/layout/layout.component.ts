import { Router } from '@angular/router';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { JwtService } from '../../../service/jwt.service';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent implements OnInit{

  constructor(
    private jwtService: JwtService,
    private fb: FormBuilder,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}


  user: any;
  profile: any;
  profileImageUrl: string | ArrayBuffer | null = null;
  errorMessage: string = '';
  url: any;
  isAdmin: boolean = false;

 

  ngOnInit(): void {
    this.loadUserProfile();
    this.checkAdminRole();
  }

  checkAdminRole(): void {
    this.isAdmin = this.jwtService.isAdmin();
  }

  private loadUserProfile(): void {
    this.jwtService.getUserProfile().subscribe(
      response => {
    //    console.log('Full response:', response); // Log the full response
        this.user = response.data.user; // Assign the response directly to this.user
        const imageBytes = response.data.profileImage;
        if (imageBytes) {
          this.profileImageUrl = 'data:image/jpeg;base64,' + imageBytes;
        }
        this.cdr.detectChanges();
      }, 
      error => {
        console.error('Error fetching user profile:', error);
      }
    );
  }


  logout(): void {
    localStorage.removeItem('jwtToken'); 
    // To reload the page after logging out
    if (typeof window !== 'undefined') {
      window.location.href = '/login';  // Redirect to login and reload page
    }
  }
  
}
