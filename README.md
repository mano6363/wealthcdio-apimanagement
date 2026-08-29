# Banking Transaction Processor

A simple Java 21 / Spring Boot implementation of the Banking Transaction Processor coding kata.

## Requirements

- Unique account IDs and balances
- Deposit, withdrawal and transfer
- Invalid amount and overdraft validation
- Per-account transaction ledger with timestamps
- Balance and transaction-history REST APIs

## Architecture

```text
REST Controller
      |
      v
Banking Service
   /         v         v
Account   Transaction
Repository Ledger
```

The service layer owns banking business rules. The repository and ledger are in-memory because persistence was not specified by the exercise.

## APIs

- `POST /api/accounts`
- `GET /api/accounts/{id}`
- `POST /api/accounts/{id}/deposit`
- `POST /api/accounts/{id}/withdraw`
- `POST /api/transfers`
- `GET /api/accounts/{id}/transactions`

## Example

```bash
curl -X POST http://localhost:8080/api/accounts   -H "Content-Type: application/json"   -d '{"accountId":"ACC-1"}'
```

```bash
curl -X POST http://localhost:8080/api/accounts/ACC-1/deposit   -H "Content-Type: application/json"   -d '{"amount":500.00}'
```

## Business rules

1. Account IDs are unique and non-blank.
2. Amounts must be greater than zero.
3. A withdrawal cannot exceed the available balance.
4. Withdrawal equal to the balance is allowed.
5. Source and destination accounts must differ.
6. A transfer creates TRANSFER_OUT and TRANSFER_IN ledger entries.
7. Each transaction has a timestamp.
8. `BigDecimal` is used for money.

## Edge cases tested

- Duplicate account
- Unknown account
- Blank account ID
- Zero/negative amount
- Insufficient funds
- Exact-balance withdrawal
- Self-transfer
- Transfer ledger entries
- Failed withdrawal leaves state unchanged
- Transaction timestamp and post-transaction balance

## Run

```bash
mvn clean test
mvn spring-boot:run
```

## Design trade-offs

The solution intentionally avoids unnecessary microservices, messaging and persistence. The kata asks for simple, readable code and focuses on business behavior.

The synchronized transaction operations provide atomicity for this small in-memory implementation. A production system should use database transactions and account-level locking.

## Recommended Git history

1. `chore: initialize Spring Boot banking application`
2. `test: add account creation specifications`
3. `feat: implement account creation`
4. `test: add deposit and withdrawal specifications`
5. `feat: implement deposit and withdrawal`
6. `test: add validation and overdraft scenarios`
7. `feat: implement transaction validation`
8. `test: add transfer specifications`
9. `feat: implement account transfers`
10. `test: add transaction ledger specifications`
11. `feat: maintain transaction ledger`
12. `feat: expose banking REST APIs`
13. `test: add REST controller tests`
14. `docs: document architecture and design decisions`
15. `refactor: simplify validation and transaction handling`

## Further improvements

- PostgreSQL persistence
- Database transactions
- Account-level locking
- Idempotency keys
- Pagination
- Authentication/authorization
- OpenAPI
- Testcontainers
- Metrics and tracing
