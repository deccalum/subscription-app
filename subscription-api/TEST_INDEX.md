# API Test Index

This file links the HTTP test matrix in [request.http](request.http) with Java parity test classes under [src/test/java/se/lexicon/subscriptionapi/httpmatrix](src/test/java/se/lexicon/subscriptionapi/httpmatrix).

## Auth
- HTTP section: `AUTH-*` in [request.http](request.http)
- Java parity: [src/test/java/se/lexicon/subscriptionapi/httpmatrix/AuthHttpMatrixTest.java](src/test/java/se/lexicon/subscriptionapi/httpmatrix/AuthHttpMatrixTest.java)

## Users
- HTTP section: `USER-*` in [request.http](request.http)
- Java parity: [src/test/java/se/lexicon/subscriptionapi/httpmatrix/UserHttpMatrixTest.java](src/test/java/se/lexicon/subscriptionapi/httpmatrix/UserHttpMatrixTest.java)

## Operators
- HTTP section: `OPERATOR-*` in [request.http](request.http)
- Java parity: [src/test/java/se/lexicon/subscriptionapi/httpmatrix/OperatorHttpMatrixTest.java](src/test/java/se/lexicon/subscriptionapi/httpmatrix/OperatorHttpMatrixTest.java)

## Plans
- HTTP section: `PLAN-*` in [request.http](request.http)
- Java parity: [src/test/java/se/lexicon/subscriptionapi/httpmatrix/PlanHttpMatrixTest.java](src/test/java/se/lexicon/subscriptionapi/httpmatrix/PlanHttpMatrixTest.java)

## Subscriptions
- HTTP section: `SUB-*` in [request.http](request.http)
- Java parity: [src/test/java/se/lexicon/subscriptionapi/httpmatrix/SubscriptionHttpMatrixTest.java](src/test/java/se/lexicon/subscriptionapi/httpmatrix/SubscriptionHttpMatrixTest.java)

## Notes
- Administration and ChangeRequest flows are intentionally excluded for now.
- Several service methods currently return `null` instead of throwing not-found exceptions; those are documented in [request.http](request.http) as `FAIL(current behavior)` cases.
