package com.midas.crm.service;

import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import com.midas.crm.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    @Autowired
    private UserRepository userRepository;

    public List<User> leerUsuariosDesdeExcelBackoffice(MultipartFile file) throws IOException {
        List<User> usuarios = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Usamos la primera hoja del Excel
            Iterator<Row> rows = sheet.iterator();
            boolean isFirstRow = true; // Para saltar el encabezado

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (isFirstRow) { // Saltamos la primera fila (encabezado)
                    isFirstRow = false;
                    continue;
                }

                Iterator<Cell> cells = currentRow.iterator();
                User user = new User();

                user.setFechaCreacion(LocalDateTime.now()); // Fecha de creación por defecto
                user.setRole(Role.BACKOFFICE); // Asignamos el rol BACKOFFICE
                user.setEstado("A"); // Estado activo

                int cellIndex = 0;
                while (cells.hasNext()) {
                    Cell currentCell = cells.next();
                    String cellValue = getCellValueAsString(currentCell);
                    switch (cellIndex) {
                        case 0 -> user.setUsername(cellValue);
                        case 1 -> user.setPassword(cellValue);
                        case 2 -> {
                            // Validar DNI: si es mayor a 8 caracteres, truncarlo a 8
                            if (cellValue.length() > 8) {
                                cellValue = cellValue.substring(0, 8);
                            }
                            user.setDni(cellValue);
                        }
                        case 3 -> user.setNombre(cellValue);
                        case 4 -> user.setApellido(cellValue);
                        case 5 -> user.setSede(cellValue);
                        default -> { }
                    }
                    cellIndex++;
                }
                usuarios.add(user);
            }
        }
        return usuarios;
    }


    public List<User> leerUsuariosDesdeExcel(MultipartFile file) throws IOException {
        List<User> usuarios = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Tomamos la primera hoja del Excel
            Iterator<Row> rows = sheet.iterator();
            boolean isFirstRow = true; // Para saltar el encabezado

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (isFirstRow) { // Saltamos la primera fila (encabezado)
                    isFirstRow = false;
                    continue;
                }

                Iterator<Cell> cells = currentRow.iterator();
                User user = new User();

                user.setFechaCreacion(LocalDateTime.now()); // Fecha de creación por defecto
                user.setRole(Role.ASESOR); // Rol predeterminado
                user.setEstado("A"); // Estado activo

                int cellIndex = 0;
                while (cells.hasNext()) {
                    Cell currentCell = cells.next();
                    String cellValue = getCellValueAsString(currentCell);
                    switch (cellIndex) {
                        case 0 -> user.setUsername(cellValue);
                        case 1 -> user.setPassword(cellValue);
                        case 2 -> {
                            // Validar DNI: si es mayor a 8 caracteres, truncarlo a 8
                            if (cellValue.length() > 8) {
                                cellValue = cellValue.substring(0, 8);
                            }
                            user.setDni(cellValue);
                        }
                        case 3 -> user.setNombre(cellValue);
                        case 4 -> user.setApellido(cellValue);
                        case 5 -> user.setSede(cellValue);
                        default -> {
                        }
                    }
                    cellIndex++;
                }
                usuarios.add(user);
            }
        }
        return usuarios;
    }

    // Método auxiliar para manejar valores de celda de diferentes tipos
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
