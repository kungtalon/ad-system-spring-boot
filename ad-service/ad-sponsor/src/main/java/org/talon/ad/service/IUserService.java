package org.talon.ad.service;

import org.talon.ad.exception.AdException;
import org.talon.ad.vo.CreateUserRequest;
import org.talon.ad.vo.CreateUserResponse;

public interface IUserService {

    CreateUserResponse createUser(CreateUserRequest request) throws AdException;
}
