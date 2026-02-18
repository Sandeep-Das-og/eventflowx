CREATE USER booking_user WITH PASSWORD 'booking_password';
CREATE USER wallet_user WITH PASSWORD 'wallet_password';
CREATE USER keycloak_user WITH PASSWORD 'keycloak_password';

CREATE DATABASE booking_db OWNER booking_user;
CREATE DATABASE wallet_db OWNER wallet_user;
CREATE DATABASE keycloak_db OWNER keycloak_user;
