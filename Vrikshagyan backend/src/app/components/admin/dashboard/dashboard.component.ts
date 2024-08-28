import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../../service/dashboard.service';
import { JwtService } from '../../../service/jwt.service';



@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  totalUsers: number = 0;
  verifiedUsers: number = 0;
  unverifiedUsers: number = 0;
  totalPlants: number = 0;
  verifiedPlants: number = 0;
  unverifiedPlants: number = 0;
  activeUsers: number = 0;
  isAdmin: boolean = false;

  constructor(private dashboardService: DashboardService, private jwtService: JwtService){}

    ngOnInit(): void {
      this.loadUserCounts();
      this.loadPlantConts();
      this.checkAdminRole();
    }

 
    loadUserCounts(): void {
      this.dashboardService.getUserCount().subscribe(count => {
        this.totalUsers = count;
      });
  
      this.dashboardService.getVerifiedUserCount().subscribe(count => {
        this.verifiedUsers = count;
      });
  
      this.dashboardService.getUnUserCount().subscribe(count => {
        this.unverifiedUsers = count;
      });
  
      this.dashboardService.getActiveUserCount().subscribe(count => {
        this.activeUsers = count;
      });
    }
    loadPlantConts(): void{
      this.dashboardService.getTotalPlant().subscribe(count =>{
        this.totalPlants = count;
      });
      this.dashboardService.getVerifiedPlant().subscribe(count =>{
        this.verifiedPlants = count;
      });
      this.dashboardService.getUnVerifedPlant().subscribe(count =>{
        this.unverifiedPlants = count;
      });
    }
    checkAdminRole(): void {
      this.isAdmin = this.jwtService.isAdmin();
    }
}
