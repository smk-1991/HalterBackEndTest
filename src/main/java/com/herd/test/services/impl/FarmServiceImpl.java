package com.herd.test.services.impl;

import com.herd.test.exception.ResourceNotFoundException;
import com.herd.test.exception.RestBusinessException;
import com.herd.test.helper.FarmHelper;
import com.herd.test.model.dto.FarmCreateUpdateDTO;
import com.herd.test.model.dto.FarmDTO;
import com.herd.test.model.entities.Farm;
import com.herd.test.model.entities.Herd;
import com.herd.test.repository.FarmRepository;
import com.herd.test.services.FarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.herd.test.helper.FarmHelper.farmBuilder;
import static com.herd.test.helper.FarmHelper.farmDTOBuilder;
import static com.herd.test.helper.FarmHelper.farmUpdateBuilder;

@Service
@RequiredArgsConstructor
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;

    @Override
    public List<FarmDTO> getAllFarms() {
        final List<Farm> farmList = farmRepository.findAll();
        return farmList.stream()
                .map(FarmHelper::farmDTOBuilder)
                .collect(Collectors.toList());
    }

    @Override
    public FarmDTO createNewFarm(FarmCreateUpdateDTO farmCreateUpdateDTO) {
        final Farm farm = farmBuilder(farmCreateUpdateDTO);
        farmDataEntryValidation(farmBuilder(farmCreateUpdateDTO));
        farmRepository.save(farm);
        return farmDTOBuilder(farm);
    }

    @Override
    public FarmDTO updateFarm(Integer id, FarmCreateUpdateDTO farmCreateUpdateDTO) {
        final Farm farm = farmRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Id not found!"));
        final Farm farmUpdated = farmUpdateBuilder(farm, farmCreateUpdateDTO);
        farmDataEntryValidation(farmBuilder(farmCreateUpdateDTO));
        farmRepository.save(farmUpdated);
        return farmDTOBuilder(farmUpdated);
    }

    @Override
    public FarmDTO findFarmById(int id) {
        final Farm farm = farmRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Id not found!"));
        return farmDTOBuilder(farm);
    }

    @Override
    public List<FarmDTO> findFarmByName(String farmName) {
        final List<Farm> farmList = farmRepository.findByfarmNameLike(farmName);

        if(farmList.isEmpty()){
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Name Not Found in Database!");
        }

        return farmList.stream()
                .map(FarmHelper::farmDTOBuilder)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFarm(Integer id) {
        farmRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Id not found!"));
        farmRepository.deleteById(id);
    }

    private void farmDataEntryValidation(final Farm farm) {
        final Farm farmName = farmRepository.findFarmByName(farm.getFarmName());

        if (Objects.nonNull(farmName)) {
            throw new RestBusinessException(HttpStatus.BAD_REQUEST, "Farm Name already exist in database!");
        }
    }

}
