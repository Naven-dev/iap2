package org.deloitte.onlineorderapp;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.*;

class OnlineOrderAppApplicationTest {

    @Test
    void main_invokesSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(Mockito.any(Class.class), Mockito.<String[]>any())).thenReturn(null);

            String[] args = new String[]{};
            OnlineOrderAppApplication.main(args);

            mocked.verify(() -> SpringApplication.run(OnlineOrderAppApplication.class, args));
        }
    }
}

