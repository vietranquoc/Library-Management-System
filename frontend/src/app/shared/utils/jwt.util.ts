export class JwtUtil {
  /**
   * Decode JWT token và trả về payload
   */
  static decodeToken(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      return null;
    }
  }

  /**
   * Lấy danh sách roles từ token
   */
  static getRoles(token: string): string[] {
    const decoded = this.decodeToken(token);
    return decoded?.roles || [];
  }

  /**
   * Kiểm tra user có role ADMIN không
   */
  static isAdmin(token: string): boolean {
    const roles = this.getRoles(token);
    return roles.includes('ROLE_ADMIN');
  }

  /**
   * Kiểm tra user có role STAFF không
   */
  static isStaff(token: string): boolean {
    const roles = this.getRoles(token);
    return roles.includes('ROLE_STAFF');
  }

  /**
   * Kiểm tra user có role STAFF hoặc ADMIN không
   */
  static isAdminOrStaff(token: string): boolean {
    const roles = this.getRoles(token);
    return roles.includes('ROLE_ADMIN') || roles.includes('ROLE_STAFF');
  }
}

