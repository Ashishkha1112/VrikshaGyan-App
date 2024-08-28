import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { LayoutComponent } from './components/admin/layout/layout.component';
import { AddplantComponent } from './components/admin/addplant/addplant.component';
import { AddplantbulkComponent } from './components/admin/addplantbulk/addplantbulk.component';
import { AdduserComponent } from './components/admin/adduser/adduser.component';
import { ChangeprofilepasswordComponent } from './components/admin/changeprofilepassword/changeprofilepassword.component';
import { ChangeprofilepictureComponent } from './components/admin/changeprofilepicture/changeprofilepicture.component';
import { DeletedplantComponent } from './components/admin/deletedplant/deletedplant.component';
import { DeleteduserComponent } from './components/admin/deleteduser/deleteduser.component';
import { ForgotpasswordComponent } from './components/admin/forgotpassword/forgotpassword.component';
import { LoginComponent } from './components/admin/login/login.component';
import { MyprofileComponent } from './components/admin/myprofile/myprofile.component';
import { NonverifiedplantComponent } from './components/admin/nonverifiedplant/nonverifiedplant.component';
import { NotfoundComponent } from './components/admin/notfound/notfound.component';
import { UpdateplantComponent } from './components/admin/updateplant/updateplant.component';
import { UpdateuserComponent } from './components/admin/updateuser/updateuser.component';
import { VerifiedplantComponent } from './components/admin/verifiedplant/verifiedplant.component';
import { ViewplantComponent } from './components/admin/viewplant/viewplant.component';
import { ViewuserComponent } from './components/admin/viewuser/viewuser.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DashboardComponent } from './components/admin/dashboard/dashboard.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { interceptorInterceptor } from './service/interceptor.interceptor';
import { JwtService } from './service/jwt.service';
import { VerifyuserComponent } from './components/admin/verifyuser/verifyuser.component';
import { DataTablesModule } from 'angular-datatables';
import { AppRoutingModule } from './app-routing.module';

@NgModule({
  declarations: [
    AppComponent,
    LayoutComponent,
    AddplantComponent,
    AddplantbulkComponent,
    AdduserComponent,
    ChangeprofilepasswordComponent,
    ChangeprofilepictureComponent,
    DashboardComponent,
    DeletedplantComponent,
    DeleteduserComponent,
    ForgotpasswordComponent,
    LoginComponent,
    MyprofileComponent,
    NonverifiedplantComponent,
    NotfoundComponent,
    UpdateplantComponent,
    UpdateuserComponent,
    VerifiedplantComponent,
    ViewplantComponent,
    ViewuserComponent,
    VerifyuserComponent
  ],
  imports: [
    DataTablesModule,
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    NgbModule
  ],
  providers: [
    {provide:HTTP_INTERCEPTORS, useClass:interceptorInterceptor,multi:true},
    JwtService,
    provideClientHydration()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
