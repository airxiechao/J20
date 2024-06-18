import { request } from '../request';

/**
 * Register
 *
 * @param username username
 * @param password password
 */
export function fetchRegister(username: string, password: string) {
  return request({
    url: '/public/user/register',
    method: 'post',
    data: {
      username,
      password
    }
  });
}

/**
 * Login
 *
 * @param username username
 * @param password password
 */
export function fetchLogin(username: string, password: string) {
  return request<Api.Auth.LoginToken>({
    url: '/public/user/login',
    method: 'post',
    data: {
      username,
      password
    }
  });
}

/** Get user info */
export function fetchUserInfo() {
  return request<Api.Auth.UserInfo>({
    url: '/user/info',
    method: 'post'
  });
}

/**
 * Refresh token
 *
 * @param refreshToken Refresh token
 */
export function fetchRefreshToken(refreshToken: string) {
  return request<Api.Auth.LoginToken>({
    url: '/public/user/token/refresh',
    method: 'post',
    data: {
      refreshToken
    }
  });
}

/**
 * return custom backend error
 *
 * @param code error code
 * @param msg error message
 */
export function fetchCustomBackendError(code: string, msg: string) {
  return request({ url: '/auth/error', params: { code, msg } });
}
