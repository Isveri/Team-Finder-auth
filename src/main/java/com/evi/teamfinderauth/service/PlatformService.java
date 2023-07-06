package com.evi.teamfinderauth.service;

import com.evi.teamfinderauth.model.PlatformDTO;

public interface PlatformService {
    PlatformDTO connectDC(String accessToken, String tokenType);
}
