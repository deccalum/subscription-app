package se.lexicon.subscriptionapi.service;

import java.util.List;

import se.lexicon.subscriptionapi.domain.constant.UserCredentials;
import se.lexicon.subscriptionapi.dto.request.UserRequest;
import se.lexicon.subscriptionapi.dto.response.UserResponse;

public interface UserService {
    UserResponse create(UserRequest request);
    UserResponse read(Long id);
    UserResponse update(Long id, UserRequest request);
    void delete(Long id);

    List<UserResponse> getAll();
    UserResponse getCredentials(UserCredentials credential);
    UserResponse getEmail(String email);
    UserResponse getName(String name);
}
