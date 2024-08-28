import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { JwtService } from '../../../service/jwt.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-viewuser',
  templateUrl: './viewuser.component.html',
  styleUrls: ['./viewuser.component.css']
})
export class ViewuserComponent implements OnInit, OnDestroy {
  users: any[] = [];
  message: string = '';
  dtOptions: DataTables.Settings = {};
  dtTrigger: Subject<any> = new Subject<any>();

  constructor(private jwtService: JwtService, private router: Router) {}

  ngOnInit(): void {
    this.dtOptions = {
      pagingType: 'full_numbers',
      pageLength: 10,
      responsive: true
    };
    this.fetchUsers();
  }

  fetchUsers(): void {
    this.jwtService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.dtTrigger.next(null);
      },
      error: (err) => {
        console.error('Error fetching users', err);
      }
    });
  }

  updateUser(userId: number): void {
    this.router.navigate(['/vrikshagyan/update', userId]);
  }

  deleteUser(userId: number): void {
    this.jwtService.deleteUser(userId).subscribe(
      () => {
        console.log('User deleted successfully.');
        alert('User deleted successfully');
        this.router.navigate(['/vrikshagyan/deleteduser']);
      },
      error => {
        console.error('Failed to delete user:', error);
      }
    );
  }

  verifyUser(userId: number): void {
    this.jwtService.verifyUser(userId).subscribe({
      next: () => {
        alert("User successfully verified.");
        this.message = 'User successfully verified.';
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
      this.router.onSameUrlNavigation = 'reload';
      this.router.navigate([this.router.url]);
      },
      error: (error) => {
        alert(error.message);
      }
    });
  }

  ngOnDestroy(): void {
    // Do not forget to unsubscribe the event
    this.dtTrigger.unsubscribe();
  }
}
