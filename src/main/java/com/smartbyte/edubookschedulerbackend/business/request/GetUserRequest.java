package com.smartbyte.edubookschedulerbackend.business.request;

import java.util.Optional;
import java.util.OptionalInt;

public class GetUserRequest {
    OptionalInt id;
    Optional<String> email;
}
