import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/admin/login/login.component';
import { ForgotpasswordComponent } from './components/admin/forgotpassword/forgotpassword.component';
import { LayoutComponent } from './components/admin/layout/layout.component';
import { DashboardComponent } from './components/admin/dashboard/dashboard.component';
import { AdduserComponent } from './components/admin/adduser/adduser.component';
import { UpdateuserComponent } from './components/admin/updateuser/updateuser.component';
import { ViewuserComponent } from './components/admin/viewuser/viewuser.component';
import { MyprofileComponent } from './components/admin/myprofile/myprofile.component';
import { DeleteduserComponent } from './components/admin/deleteduser/deleteduser.component';
import { AddplantComponent } from './components/admin/addplant/addplant.component';
import { ViewplantComponent } from './components/admin/viewplant/viewplant.component';
import { AddplantbulkComponent } from './components/admin/addplantbulk/addplantbulk.component';
import { UpdateplantComponent } from './components/admin/updateplant/updateplant.component';
import { NonverifiedplantComponent } from './components/admin/nonverifiedplant/nonverifiedplant.component';
import { VerifiedplantComponent } from './components/admin/verifiedplant/verifiedplant.component';
import { DeletedplantComponent } from './components/admin/deletedplant/deletedplant.component';
import { ChangeprofilepasswordComponent } from './components/admin/changeprofilepassword/changeprofilepassword.component';
import { ChangeprofilepictureComponent } from './components/admin/changeprofilepicture/changeprofilepicture.component';
import { NotfoundComponent } from './components/admin/notfound/notfound.component';
import { authGuard } from './service/auth.guard';

const routes: Routes = [
  {
    path: '', redirectTo: 'login', pathMatch: 'full'
  },
  {
    path: 'login', component: LoginComponent
  },
  {
    path: 'forgotpassword', component: ForgotpasswordComponent
  },
  {
    path: 'vrikshagyan', component: LayoutComponent, canActivate: [authGuard],
    children: [
      {
        path: 'dashboard', component: DashboardComponent, canActivate: [authGuard],
      },
    {
      path: 'adduser', component: AdduserComponent,canActivate: [authGuard]
    },
    {
      path: 'update/:id', component: UpdateuserComponent, canActivate :[authGuard]
    },
    {
      path: 'viewuser', component: ViewuserComponent,canActivate: [authGuard]
    },
    {
      path: 'deleteduser', component: DeleteduserComponent,canActivate: [authGuard]
    },
    {
      path: 'myprofile', component: MyprofileComponent,canActivate: [authGuard]
    },
    {
      path: 'addplant', component: AddplantComponent,canActivate: [authGuard]
    },
    {
      path: 'viewplant', component: ViewplantComponent,canActivate: [authGuard]
    },
    {
      path: 'addplantbulk', component: AddplantbulkComponent,canActivate: [authGuard]
    },
    {
      path: 'updatePlant/:id', component: UpdateplantComponent,canActivate: [authGuard]
    },
    {
      path:'nonverifiedplant', component: NonverifiedplantComponent,canActivate: [authGuard]
    },
    {
      path:'verifiedplant', component: VerifiedplantComponent,canActivate: [authGuard]
    },
    {
      path: 'deletedplant', component: DeletedplantComponent,canActivate: [authGuard]
    },
    {
      path:'changepassword', component: ChangeprofilepasswordComponent, canActivate: [authGuard]
    },
    {
      path: 'changeprofilepicture', component: ChangeprofilepictureComponent, canActivate: [authGuard]
    }
  ]},
  {
    path:'**', component: NotfoundComponent, canActivate: [authGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
