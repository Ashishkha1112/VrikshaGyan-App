import { Component, OnInit } from '@angular/core';
import { JwtService } from '../../../service/jwt.service';
import { Subject } from 'rxjs';


@Component({
  selector: 'app-deleteduser',
  templateUrl: './deleteduser.component.html',
  styleUrl: './deleteduser.component.css'
})

export class DeleteduserComponent implements OnInit {
  users: any[] = [];
  dtOptions: DataTables.Settings = {};
  dtTrigger: Subject<any> = new Subject<any>();

  constructor(private jwtService: JwtService) {}
  ngOnInit(): void {
    this.dtOptions = {
      pagingType: 'full_numbers',
      pageLength: 10,
      responsive: true
    };
    this.fetchDeletedUsers();
  }
  fetchDeletedUsers(): void {
    this.jwtService.getDeletedUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.dtTrigger.next(null);
      },
      error: (err) => {
        console.error('Error fetching users', err);
      }
    });
}
}
