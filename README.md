### CHANGES / ADDITIONS

#### Tests
Testing is organized by API domain with cross-referencing between [request.http](scripts/request.http) and Java integration tests. See the full [TEST_INDEX.md](scripts/TEST_INDEX.md) for details.

Highlights:
- **Auth**: [AuthHttpMatrixTest](subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/AuthHttpMatrixTest.java) covers registration, login/logout, and JWT validation.
- **Workflow**: [ChangeRequestFlowHttpMatrixTest](subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/ChangeRequestFlowHttpMatrixTest.java) validates the multi-step `Operator submit -> Admin approve -> Execution` cycle.
- **Resources**: Dedicated tests for [Users](subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/UserHttpMatrixTest.java), [Operators](subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/OperatorHttpMatrixTest.java), [Plans](subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/PlanHttpMatrixTest.java), and [Subscriptions](subscription-api/src/test/java/se/lexicon/subscriptionapi/httpmatrix/SubscriptionHttpMatrixTest.java).

#### Validation/Exception Messages
Used a consistent format for validation and exception messages without needing a specific message code system. 

Centralized [ApiMessages.properties](subscription-api/src/main/resources/ApiMessages.properties) for API documentation and human-readable errors.
- **Swagger Documentation**: [SwaggerConfig.java](subscription-api/src/main/java/se/lexicon/subscriptionapi/config/SwaggerConfig.java) uses a `GlobalOperationCustomizer` to inject summaries/descriptions from these properties into the OpenAPI spec.
- **Error Resolution**: Exception messages are resolved via Spring's `MessageSource` in the [GlobalExceptionHandler](subscription-api/src/main/java/se/lexicon/subscriptionapi/exception/GlobalExceptionHandler.java), allowing code-internal dummy strings like `"auto"` to be replaced with localized/formatted messages.

Tried a similar approach on exceptions, also tried an auto system that builds messages based on the exception type and context.

#### Country Code Validation
Added a custom `@ValidCountryCode` annotation for validating country codes in user profiles. This annotation uses a `CountryCodeValidator` that checks against a predefined list of valid codes. This approach keeps validation logic clean and reusable across different DTOs.

#### Abstract Classes
Experiemented with abstract classes and related database logic. Different user types contain specific attributes, but share common ones. 

Operator plans follow a similar pattern. 

#### Functional Enums
Uses the [Command Pattern](https://en.wikipedia.org/wiki/Command_pattern) through enums with `Supplier` factories:
- **[ActionType.java](subscription-api/src/main/java/se/lexicon/subscriptionapi/domain/constant/ActionType.java)** embeds an `ActionType(Supplier<ChangeRequest> factory)` constructor.
- Instead of large `switch` statements or conditional logic in services, the [RequestMapper](subscription-api/src/main/java/se/lexicon/subscriptionapi/mapper/RequestMapper.java) calls `action.create()` to instantiate the correct JPA entity (e.g. `CreatePlanRequest`, `UpdateOperatorRequest`).
- This binds the discriminator used in JPA `SINGLE_TABLE` inheritance directly to its class implementation, ensuring type safety and reducing repetition.

#### DB Access Requests
Added a queued change-request workflow for operator-initiated admin actions:
1. **Submit**: Operator submits a [ChangeRequest](subscription-api/src/main/java/se/lexicon/subscriptionapi/domain/entity/ChangeRequest.java). The entity (using `SINGLE_TABLE` inheritance) stores the staged data.
2. **Review**: Admin reviews pending requests. The [AdministrationServiceImpl](subscription-api/src/main/java/se/lexicon/subscriptionapi/service/AdministrationServiceImpl.java) manages approval/rejection.
3. **Execution**: Approvals trigger standard service calls (e.g. `planService.create()`) via the `executeApprovalAction` method.
4. **Log**: Rejections are logged with a reason, accessible to the original requester.


##### TODO

- `--dev` command-line arg to trigger dev-only setup logic with related features.
- admin local-only login policy (larger security overhaul)
- add login event table with timestamp, ip, user-agent, outcome, reason
- Additional tests that are missing.
user identity strategy:
- keep numeric db primary key as internal id
- add immutable public id (string) for display/search, e.g. A-000123, O-000456, U-000789
- generate public id in service layer after persistence (or via db sequence + formatter), never replace PK