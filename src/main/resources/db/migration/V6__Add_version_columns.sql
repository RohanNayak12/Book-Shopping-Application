-- VXX__add_version_columns.sql
ALTER TABLE public.tenants ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE public.users   ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

-- Optional: drop default after backfilling to let Hibernate manage increments
ALTER TABLE public.tenants ALTER COLUMN version DROP DEFAULT;
ALTER TABLE public.users   ALTER COLUMN version DROP DEFAULT;
