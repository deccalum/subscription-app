# API Test Index

This file links the HTTP test matrix in [request.http](request.http) with Java parity test classes under [tests/../httpmatrix](../subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix).

## Auth
- HTTP section: `AUTH-*` in [request](request.http)
- Java parity: [AuthHttpMatrixTest](../subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/AuthHttpMatrixTest.java)

## Users
- HTTP section: `USER-*` in [request](request.http)
- Java parity: [UserHttpMatrixTest](../subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/UserHttpMatrixTest.java)

## Operators
- HTTP section: `OPERATOR-*` in [request](request.http)
- Java parity: [OperatorHttpMatrixTest](../subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/OperatorHttpMatrixTest.java)

## Plans
- HTTP section: `PLAN-*` in [request](request.http)
- Java parity: [PlanHttpMatrixTest](../subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/PlanHttpMatrixTest.java)

## Subscriptions
- HTTP section: `SUB-*` in [request](request.http)
- Java parity: [SubscriptionHttpMatrixTest](../subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/SubscriptionHttpMatrixTest.java)

## Administration + Change Requests
- HTTP section: `OPERATOR-CR-*` and `ADMIN-CR-*` in [request](request.http)
- Java parity (existing): [ChangeRequestFlowHttpMatrixTest](../subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/ChangeRequestFlowHttpMatrixTest.java)
