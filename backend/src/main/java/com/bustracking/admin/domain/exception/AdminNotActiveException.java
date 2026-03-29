package com.bustracking.admin.domain.exception;

import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
    
public class AdminNotActiveException extends BusinessRuleException {

    public AdminNotActiveException(Long adminId) {
        super(
            ErrorCode.ADMIN_NOT_ACTIVE,
            "The admin is not active: " + adminId,
            "Admin with ID " + adminId + " is not active and cannot perform this operation."
        );
    }
}
