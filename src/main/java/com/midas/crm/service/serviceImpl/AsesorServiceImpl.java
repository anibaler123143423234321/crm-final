package com.midas.crm.service.serviceImpl;

import com.midas.crm.entity.DTO.AsesorDTO;
import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import com.midas.crm.repository.UserRepository;
import com.midas.crm.service.AsesorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsesorServiceImpl implements AsesorService {

    private final UserRepository userRepository;

    @Override
    public List<AsesorDTO> getAllAsesores() {
        List<User> asesores = userRepository.findByRole(Role.ASESOR);
        return asesores.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AsesorDTO> getAsesorById(Long id) {
        return userRepository.findByIdAndRole(id, Role.ASESOR)
                .map(this::convertToDTO);
    }

    @Override
    public List<AsesorDTO> getAsesoresByCoordinadorId(Long coordinadorId) {
        List<User> asesores = userRepository.findByCoordinadorIdAndRole(coordinadorId, Role.ASESOR);
        return asesores.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AsesorDTO convertToDTO(User user) {
        return new AsesorDTO(
                user.getId(),
                user.getNombre(),
                user.getApellido(),
                user.getUsername(),
                user.getDni(),
                user.getEmail(),
                user.getTelefono(),
                user.getSede()
        );
    }
}