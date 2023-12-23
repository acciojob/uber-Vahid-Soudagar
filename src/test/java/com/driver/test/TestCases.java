package com.driver.test;

import com.driver.model.*;
import com.driver.repository.*;
import com.driver.services.AdminService;
import com.driver.services.impl.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestCases {

    @Mock
    AdminRepository adminRepository1;
    @InjectMocks
    AdminServiceImpl adminService;

    @Test
    public void createAdmin() {
        adminService = new AdminServiceImpl();
        Admin admin = new Admin(1, "vahid", "90041");
        adminService.adminRegister(admin);
    }
}

