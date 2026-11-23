UPDATE app_user
SET password_hash = '$2a$10$jJ0n/O2NY8SKRq7XkgE8xeq5zXoblPNMmgTRiSGeqU/JfZiT93MDC'
WHERE email IN ('owner@lumen.cafe', 'waiter1@lumen.cafe', 'waiter2@lumen.cafe');