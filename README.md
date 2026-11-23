# cafe_ops

Small web app I'm building for a single coffee shop: owner runs the place, one waiter per shift takes drink orders by table. Drinks only — no food, no fiscal receipts, no card payments. Totals are just for daily overview.

**Stack:** Spring Boot, PostgreSQL, Angular. Postgres for local dev runs in Docker.

## What it should do when finished

**Owner:** staff accounts, open/close shifts, edit the drink menu, place tables on a simple floor map, see a basic daily summary.

**Waiter (during an open shift):** see all tables, start or update an order for a table, add drinks with quantity, close the order when the table is done.

## How it's wired

Browser (Angular) talks to a Spring REST API. Login returns a JWT; every other call sends `Authorization: Bearer …`. Postgres schema is managed with Flyway migrations.

Folders (as I add them):

- `backend/` — API  
- `frontend/` — UI  
- `docker-compose.yml` — Postgres for local dev  
- `docs/` — notes and roadmap  

## Progress

- [x] README and docs sketch  
- [x] Postgres in Docker  
- [ ] Spring skeleton  
- [ ] DB schema + demo data  
- [ ] Auth (JWT) + roles  
- [ ] Shifts, menu, tables, orders, reports  
- [ ] Angular (waiter + owner screens)  

## Run it locally

I'll document steps once Docker and the backend are in the repo.
