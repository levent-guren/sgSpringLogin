CREATE TABLE refresh_tokens (
  id number generated always as identity primary key,
  personel_id varchar2(36) not null,
  token_hash varchar2(255) not null,
  expires_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
  revoked number(1) not null,
  family_id varchar2(100) not null,
  replaced_by_token_id number,
  created_by_ip varchar2(255),
  user_agent varchar2(255)
  );
