package com.midas.crm.service.serviceImpl;

import com.midas.crm.entity.ClienteConUsuarioDTO;
import com.midas.crm.entity.ClienteResidencial;
import com.midas.crm.entity.User;
import com.midas.crm.repository.ClienteResidencialRepository;
import com.midas.crm.repository.UserRepository;
import com.midas.crm.service.ClienteResidencialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClienteResidencialServiceImpl implements ClienteResidencialService {

    @Autowired
    private ClienteResidencialRepository clienteRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public List<ClienteResidencial> listarTodos() {
        return clienteRepo.findAll();
    }

    @Override
    public Page<ClienteConUsuarioDTO> obtenerClientesConUsuario(Pageable pageable) {
        return clienteRepo.obtenerClientesConUsuario(pageable);
    }

    // Cambiar para que retorne una lista en lugar de un Optional
    @Override
    public List<ClienteResidencial> buscarPorMovil(String movil) {
        return clienteRepo.findByMovilContacto(movil);
    }

    @Override
    public ClienteResidencial obtenerPorId(Long id) {
        return clienteRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado con id: " + id));
    }
    @Transactional
    @Override
    public ClienteResidencial guardar(ClienteResidencial cliente, Long usuarioId) {
        if (cliente == null || usuarioId == null) {
            throw new IllegalArgumentException("Cliente y usuarioId no pueden ser nulos");
        }

        // Obtener el usuario de la base de datos
        User usuario = userRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + usuarioId));

        cliente.setUsuario(usuario);
        cliente.setFechaCreacion(LocalDateTime.now());

        // Dado que 'movilesAPortar' es una lista de Strings, no es necesario asignar relaciones
        // La colección se gestionará automáticamente como ElementCollection

        return clienteRepo.save(cliente);
    }


    @Override
    public ClienteResidencial actualizar(Long id, ClienteResidencial cliente) {
        if (!clienteRepo.existsById(id)) {
            throw new NoSuchElementException("Cliente no encontrado con id: " + id);
        }
        cliente.setId(id);  // Aseguramos que actualice el existente
        return clienteRepo.save(cliente);
    }

    @Override
    public void eliminar(Long id) {
        if (!clienteRepo.existsById(id)) {
            throw new NoSuchElementException("Cliente no encontrado con id: " + id);
        }
        clienteRepo.deleteById(id);
    }

    @Override
    public Page<ClienteConUsuarioDTO> obtenerClientesConUsuarioFiltrados(String dniAsesor, String nombreAsesor, String numeroMovil, LocalDate fecha, Pageable pageable) {
        return clienteRepo.obtenerClientesConUsuarioFiltrados(dniAsesor, nombreAsesor, numeroMovil, fecha, pageable);
    }
}