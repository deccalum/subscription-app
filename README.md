## CHANGES AND ADDITIONS

### ValidationMessages
Kept in one layer (dto) and simplified with `ValidationMessages.properties`.

#### In DTO
```java
public record CustomerDetailRequest(
        @NotBlank(message = "{blank}") 
        String address,

        @NotBlank(message = "{blank}") 
        @Pattern(regexp = "^\\+?[0-9\\s-]{7,20}$", message = "{invalidNumber}")
        String phoneNumber,
        
        String preferences
) {}
```

#### In console
```
2026-03-17T10:21:03.248+01:00  WARN 45552 --- [subscription-api] [nio-8080-exec-6] s.l.s.exception.GlobalExceptionHandler   : Validation failed:
 - password: Invalid length
 - email: Invalid email format
 - firstName: Field cannot be blank
```

### Logging
customized application.properties

### Database
Switched to postgres. Added cloudbeaver to docker-compose for db review.

### Abstract Classes
Plan -> Plans(internet, cellular, satellite etc.)
User -> Users(admin, operator, customer)

### Renamed
Customer -> User

### Notes

Mapstruct issues...

### ADDITIONS

#### Change Request
Added a queued change-request workflow for operator-initiated admin actions:
- Operator can submit admin-level create/update/delete style actions as requests.
- Changes are not persisted until admin approval.
- Admin can review queued requests and approve/deny.
- Request model now tracks status (pending/approved/rejected), timestamps, operator reference, and request details.

### TODO

#### admin 
instead of seeder. use dev arg to auto-create+login admin user on startup for local development.
- add command-line arg (e.g., `--dev`) to trigger dev-only setup logic

#### ip related
admin local-only login policy (larger security overhaul):
- restrict admin authentication to allowlisted local/private networks (dev) or dedicated bastion/vpn (prod)
- add explicit deny + audit log for admin login attempts outside policy
- add login event table with timestamp, ip, user-agent, outcome, reason

user identity strategy:
- keep numeric db primary key as internal id
- add immutable public id (string) for display/search, e.g. A-000123, O-000456, U-000789
- generate public id in service layer after persistence (or via db sequence + formatter), never replace PK

future user model refactor:
- evaluate single-table inheritance for user types: BaseUser + AdminUser/OperatorUser/EndUser
- move subscriptions relation only to EndUser subtype if role-specific fields diverge further
- keep current single User + roles approach if differences remain mostly authorization-related

#### Admin Login IP Restriction
To restrict admin logins to "local" networks (e.g., localhost or internal VPN):
You should capture the IP address during authentication in your Spring Security configuration.
*   **Implementation Idea**: Extract the IP from `HttpServletRequest.getRemoteAddr()` (or `X-Forwarded-For` header if behind a proxy like CloudBeaver/Docker) inside your JWT Auth Filter or Login Controller. 
*   **Validation**: If the fetched user has `ROLE_ADMIN` and the IP is not `127.0.0.1`, `0:0:0:0:0:0:0:1`, or your local subnet, throw an `AccessDeniedException`.
*   **IP Logging**: Add a `last_login_ip` and `last_login_instant` column to the `User` class to track this for security audits.

