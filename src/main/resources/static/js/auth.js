(function () {
  if (window.__flashlearnAuthPatched) {
    return;
  }
  window.__flashlearnAuthPatched = true;

  const ACCESS_KEY = 'token';
  const REFRESH_KEY = 'refreshToken';
  const USER_KEY = 'user';
  const originalFetch = window.fetch.bind(window);

  let refreshingPromise = null;

  function getAccessToken() {
    return localStorage.getItem(ACCESS_KEY);
  }

  function getRefreshToken() {
    return localStorage.getItem(REFRESH_KEY);
  }

  function setTokens(accessToken, refreshToken) {
    if (accessToken) localStorage.setItem(ACCESS_KEY, accessToken);
    if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken);
  }

  function clearAuth() {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
  }

  function isApiRequest(url) {
    return typeof url === 'string' && url.startsWith('/api');
  }

  function isAuthBypassPath(url) {
    return url.startsWith('/api/auth/login')
      || url.startsWith('/api/auth/register')
      || url.startsWith('/api/auth/refresh');
  }

  async function refreshAccessToken() {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      return null;
    }
    if (!refreshingPromise) {
      refreshingPromise = originalFetch('/api/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken })
      })
        .then(async (res) => {
          if (!res.ok) {
            throw new Error('refresh-failed');
          }
          const data = await res.json();
          setTokens(data.token, data.refreshToken);
          if (data.user) {
            localStorage.setItem(USER_KEY, JSON.stringify(data.user));
          }
          return data.token;
        })
        .catch(() => {
          clearAuth();
          return null;
        })
        .finally(() => {
          refreshingPromise = null;
        });
    }
    return refreshingPromise;
  }

  async function authFetch(input, init = {}) {
    const url = typeof input === 'string' ? input : (input && input.url) || '';
    if (!isApiRequest(url)) {
      return originalFetch(input, init);
    }

    const headers = new Headers(init.headers || (input instanceof Request ? input.headers : undefined));
    if (!isAuthBypassPath(url) && !headers.has('Authorization')) {
      const accessToken = getAccessToken();
      if (accessToken) {
        headers.set('Authorization', 'Bearer ' + accessToken);
      }
    }

    const firstResponse = await originalFetch(input, { ...init, headers });
    if (firstResponse.status !== 401 || isAuthBypassPath(url) || init.__retryOnce) {
      return firstResponse;
    }

    const newAccessToken = await refreshAccessToken();
    if (!newAccessToken) {
      return firstResponse;
    }

    const retryHeaders = new Headers(headers);
    retryHeaders.set('Authorization', 'Bearer ' + newAccessToken);
    return originalFetch(input, { ...init, headers: retryHeaders, __retryOnce: true });
  }

  window.AuthStore = {
    getAccessToken,
    getRefreshToken,
    setTokens,
    clear: clearAuth
  };
  window.authFetch = authFetch;
  window.fetch = authFetch;
})();
