package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.*;
import se.lexicon.subscriptionapi.domain.entity.User;
import se.lexicon.subscriptionapi.domain.entity.user.UserAdmin;
import se.lexicon.subscriptionapi.domain.entity.user.UserCustomer;
import se.lexicon.subscriptionapi.domain.entity.user.UserOperator;
import se.lexicon.subscriptionapi.dto.request.UserRequest;
import se.lexicon.subscriptionapi.dto.response.UserResponse;


@Mapper(componentModel = "spring")
public interface UserMapper {
    default User toEntity(UserRequest request, User user) {
        if (user instanceof UserAdmin admin)
            return updateAdmin(request, admin);
        if (user instanceof UserCustomer customer)
            return updateCustomer(request, customer);
        if (user instanceof UserOperator operator)
            return updateOperator(request, operator);
        return user;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "writeInstant", ignore = true)
    @Mapping(target = "deleteInstant", ignore = true)
    @Mapping(target = "lastLoginInstant", ignore = true)
    @Mapping(target = "lastLoginIp", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    UserCustomer updateCustomer(UserRequest request, @MappingTarget UserCustomer user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "writeInstant", ignore = true)
    @Mapping(target = "deleteInstant", ignore = true)
    @Mapping(target = "lastLoginInstant", ignore = true)
    @Mapping(target = "lastLoginIp", ignore = true)
    UserAdmin updateAdmin(UserRequest request, @MappingTarget UserAdmin user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "writeInstant", ignore = true)
    @Mapping(target = "deleteInstant", ignore = true)
    @Mapping(target = "lastLoginInstant", ignore = true)
    @Mapping(target = "lastLoginIp", ignore = true)
    @Mapping(target = "operator", ignore = true) // Specific to Operator
    UserOperator updateOperator(UserRequest request, @MappingTarget UserOperator user);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @SubclassMapping(source = UserAdmin.class, target = UserResponse.class)
    @SubclassMapping(source = UserCustomer.class, target = UserResponse.class)
    @SubclassMapping(source = UserOperator.class, target = UserResponse.class)
    UserResponse toResponse(User user);

    UserResponse toResponse(UserCustomer user);

    @Mapping(target = "address", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "preferences", ignore = true)
    UserResponse toResponse(UserAdmin user);

    @Mapping(target = "address", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "preferences", ignore = true)
    UserResponse toResponse(UserOperator user);
}
