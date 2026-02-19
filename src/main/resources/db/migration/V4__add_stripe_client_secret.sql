-- Add stripe_client_secret column to payments table
ALTER TABLE payments ADD COLUMN IF NOT EXISTS stripe_client_secret VARCHAR(200);

