package com.bustracking.shared.unit;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import com.bustracking.shared.application.RefreshTokenUseCase;
import com.bustracking.shared.infrastructure.service.JwtService;
import com.bustracking.shared.infrastructure.service.RefreshTokenService;

public class RefreshUseCaseTest {

    // Mocks
    @Mock
    private RefreshTokenService refreshTokenServiceMock;
    
    @Mock
    private JwtService jwtServiceMock;

    private RefreshTokenUseCase refreshTokenUseCase;



    @BeforeEach
    void setUp(){
        refreshTokenUseCase = new RefreshTokenUseCase(refreshTokenServiceMock, jwtServiceMock);
    }


    
}
