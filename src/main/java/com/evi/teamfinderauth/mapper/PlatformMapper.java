package com.evi.teamfinderauth.mapper;

import com.evi.teamfinderauth.domain.Platform;
import com.evi.teamfinderauth.model.PlatformDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(builder = @Builder)
public abstract class PlatformMapper {

    public abstract PlatformDTO mapPlatformToPlatformDTO(Platform platform);
}
