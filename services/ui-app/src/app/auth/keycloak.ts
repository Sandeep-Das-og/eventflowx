import Keycloak from 'keycloak-js';

export const keycloak = new Keycloak({
  url: 'http://localhost:9090',
  realm: 'eventflowx',
  clientId: 'eventflowx-ui'
});

export async function initKeycloak(): Promise<void> {
  await keycloak.init({
    onLoad: 'login-required',
    checkLoginIframe: false,
    pkceMethod: 'S256'
  });
}

export async function getValidToken(): Promise<string | undefined> {
  if (!keycloak.authenticated) {
    return undefined;
  }

  try {
    await keycloak.updateToken(30);
  } catch {
    await keycloak.login();
  }

  return keycloak.token;
}
