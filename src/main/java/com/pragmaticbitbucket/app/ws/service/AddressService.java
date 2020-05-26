package com.pragmaticbitbucket.app.ws.service;

import com.pragmaticbitbucket.app.ws.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String userId);
    AddressDto getAddress(String userId);
}
