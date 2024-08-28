//RBAC

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { JwtService } from '../service/jwt.service';

export const authGuard: CanActivateFn = (route, state) => {
  const jwtService = inject(JwtService);
  const router = inject(Router);

  const user = jwtService.getUser();
  console.log('User retrieved from token:', user); // Debugging

  // Check if the user has the required role
  const hasRequiredRole = user?.role.some((role: { authority: string }) =>
    role.authority === 'ROLE_ADMIN' || role.authority === 'ROLE_VERIFIER'
  );

  if (hasRequiredRole) {
    return true;
  } else {
    router.navigate(['login'], { queryParams: { error: 'invalid-credentials' } });
    return false;
  }
};
