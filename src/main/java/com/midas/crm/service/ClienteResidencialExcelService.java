package com.midas.crm.service;

import com.midas.crm.entity.ClienteResidencial;
import com.midas.crm.repository.ClienteResidencialRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteResidencialExcelService {

    @Autowired
    private ClienteResidencialRepository clienteResidencialRepository;

    /**
     * =========================================================================
     *  1) MÉTODO PARA EXPORTAR "MASIVO"
     *     Cada cliente en una fila y cada columna para un campo específico.
     *     Ejemplo de columnas: NÚMERO (movilContacto), CAMPAÑA, NOMBRE...
     * =========================================================================
     */
    public byte[] generarExcelClientesMasivo() {
        List<ClienteResidencial> clientes = clienteResidencialRepository.findAll();
        if (clientes == null || clientes.isEmpty()) {
            return new byte[0];
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clientes Residenciales");

            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            // Encabezado (fila 0)
            Row headerRow = sheet.createRow(0);

            // Datos Cliente Residencial
            crearCeldaConEstilo(headerRow, 0, "CAMPAÑA", headerStyle);
            crearCeldaConEstilo(headerRow, 1, "NOMBRES Y APELLIDOS", headerStyle);
            crearCeldaConEstilo(headerRow, 2, "NIF/NIE", headerStyle);
            crearCeldaConEstilo(headerRow, 3, "NACIONALIDAD", headerStyle);
            crearCeldaConEstilo(headerRow, 4, "FECHA DE NACIMIENTO", headerStyle);
            crearCeldaConEstilo(headerRow, 5, "GÉNERO", headerStyle);
            crearCeldaConEstilo(headerRow, 6, "CORREO ELECTRÓNICO", headerStyle);
            crearCeldaConEstilo(headerRow, 7, "CUENTA BANCARIA", headerStyle);
            crearCeldaConEstilo(headerRow, 8, "DIRECCIÓN", headerStyle);
            crearCeldaConEstilo(headerRow, 9, "TIPO DE FIBRA", headerStyle);
            crearCeldaConEstilo(headerRow, 10, "HORA DE INSTALACIÓN", headerStyle);

            // Datos de la Promoción
            crearCeldaConEstilo(headerRow, 11, "PROMOCIÓN", headerStyle);
            crearCeldaConEstilo(headerRow, 12, "TV/DECO", headerStyle);
            crearCeldaConEstilo(headerRow, 13, "GRABACIÓN OCM", headerStyle);
            crearCeldaConEstilo(headerRow, 14, "MOVIL CONTACTO", headerStyle);
            crearCeldaConEstilo(headerRow, 15, "FIJO/COMPAÑÍA", headerStyle);
            crearCeldaConEstilo(headerRow, 16, "MOVILES A PORTAR", headerStyle);
            crearCeldaConEstilo(headerRow, 17, "PRECIO PROMOCIÓN/TIEMPO", headerStyle);
            crearCeldaConEstilo(headerRow, 18, "PRECIO REAL O DESPUÉS DE PROMOCIÓN", headerStyle);
            crearCeldaConEstilo(headerRow, 19, "SEGMENTO", headerStyle);
            crearCeldaConEstilo(headerRow, 20, "COMENTARIOS RELEVANTES", headerStyle);
            crearCeldaConEstilo(headerRow, 21, "COMERCIAL", headerStyle);
            crearCeldaConEstilo(headerRow, 22, "ASIGNADO A", headerStyle);
            crearCeldaConEstilo(headerRow, 23, "OBSERVACIONES", headerStyle);
            crearCeldaConEstilo(headerRow, 24, "TIPO DE USUARIO", headerStyle);

            // Rellenar datos fila a fila
            int rowIndex = 1;
            for (ClienteResidencial cliente : clientes) {
                Row dataRow = sheet.createRow(rowIndex++);

                dataRow.createCell(0).setCellValue(cliente.getCampania() != null ? cliente.getCampania() : "");
                dataRow.createCell(1).setCellValue(cliente.getNombresApellidos() != null ? cliente.getNombresApellidos() : "");
                dataRow.createCell(2).setCellValue(cliente.getNifNie() != null ? cliente.getNifNie() : "");
                dataRow.createCell(3).setCellValue(cliente.getNacionalidad() != null ? cliente.getNacionalidad() : "");
                dataRow.createCell(4).setCellValue(cliente.getFechaNacimiento() != null ? cliente.getFechaNacimiento().toString() : "");
                dataRow.createCell(5).setCellValue(cliente.getGenero() != null ? cliente.getGenero() : "");
                dataRow.createCell(6).setCellValue(cliente.getCorreoElectronico() != null ? cliente.getCorreoElectronico() : "");
                dataRow.createCell(7).setCellValue(cliente.getCuentaBancaria() != null ? cliente.getCuentaBancaria() : "");
                dataRow.createCell(8).setCellValue(cliente.getDireccion() != null ? cliente.getDireccion() : "");
                dataRow.createCell(9).setCellValue(cliente.getTipoFibra() != null ? cliente.getTipoFibra() : "");
                dataRow.createCell(10).setCellValue(""); // Hora de instalación

                dataRow.createCell(11).setCellValue(cliente.getPlanActual() != null ? cliente.getPlanActual() : "");
                dataRow.createCell(12).setCellValue(""); // TV/DECO
                dataRow.createCell(13).setCellValue(""); // Grabación OCM
                dataRow.createCell(14).setCellValue(cliente.getMovilContacto() != null ? cliente.getMovilContacto() : "");
                dataRow.createCell(15).setCellValue(cliente.getFijoCompania() != null ? cliente.getFijoCompania() : "");

                // Móviles a portar concatenados
                if (cliente.getMovilesAPortar() != null && !cliente.getMovilesAPortar().isEmpty()) {
                    String movilesConcatenados = String.join(", ", cliente.getMovilesAPortar());
                    dataRow.createCell(16).setCellValue(movilesConcatenados);
                } else {
                    dataRow.createCell(16).setCellValue("");
                }

                dataRow.createCell(17).setCellValue(""); // Precio promoción/tiempo
                dataRow.createCell(18).setCellValue(""); // Precio real
                dataRow.createCell(19).setCellValue(""); // Segmento
                dataRow.createCell(20).setCellValue(""); // Comentarios
                dataRow.createCell(21).setCellValue(cliente.getUsuario() != null ? cliente.getUsuario().getNombre() : "");
                dataRow.createCell(22).setCellValue(""); // Asignado a
                dataRow.createCell(23).setCellValue(""); // Observaciones
                dataRow.createCell(24).setCellValue(""); // Tipo de usuario
            }

            // Ajustar el ancho de las columnas
            for (int i = 0; i < 25; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * =========================================================================
     *  2) MÉTODO PARA EXPORTAR "INDIVIDUAL"
     *     Un solo cliente en una hoja, con estructura vertical y secciones.
     * =========================================================================
     */
    public byte[] generarExcelClienteIndividual(String movilContacto) {
        List<ClienteResidencial> clientes = clienteResidencialRepository.findByMovilContacto(movilContacto);
        if (clientes.isEmpty()) {
            return new byte[0]; // Si no se encontró ningún registro, retornar un array vacío.
        }

        // Ejemplo: usar el primer registro de la lista.
        ClienteResidencial cliente = clientes.get(0);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Cliente Residencial");

            // Estilo para los encabezados
            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            int rowNum = 0;

            // Sección: DATOS CLIENTE RESIDENCIAL
            Row headerRow1 = sheet.createRow(rowNum++);
            Cell headerCell1 = headerRow1.createCell(0);
            headerCell1.setCellValue("DATOS CLIENTE RESIDENCIAL");
            headerCell1.setCellStyle(headerStyle);

            agregarFila(sheet, rowNum++, "CAMPAÑA:", cliente.getCampania());
            agregarFila(sheet, rowNum++, "NOMBRES Y APELLIDOS:", cliente.getNombresApellidos());
            agregarFila(sheet, rowNum++, "NIF / NIE:", cliente.getNifNie());
            agregarFila(sheet, rowNum++, "NACIONALIDAD:", cliente.getNacionalidad());
            agregarFila(sheet, rowNum++, "FECHA DE NACIMIENTO:",
                    cliente.getFechaNacimiento() != null ? cliente.getFechaNacimiento().toString() : "");
            agregarFila(sheet, rowNum++, "GÉNERO:", cliente.getGenero());
            agregarFila(sheet, rowNum++, "CORREO ELECTRÓNICO:", cliente.getCorreoElectronico());
            agregarFila(sheet, rowNum++, "CUENTA BANCARIA:", cliente.getCuentaBancaria());
            agregarFila(sheet, rowNum++, "DIRECCIÓN:", cliente.getDireccion());
            agregarFila(sheet, rowNum++, "TIPO DE FIBRA:", cliente.getTipoFibra());

            // Campo de ejemplo para hora de instalación
            agregarFila(sheet, rowNum++, "HORA DE INSTALACIÓN:", "");

            rowNum++; // Espacio antes de la siguiente sección

            // Sección: DATOS DE LA PROMOCIÓN
            Row headerRow2 = sheet.createRow(rowNum++);
            Cell headerCell2 = headerRow2.createCell(0);
            headerCell2.setCellValue("DATOS DE LA PROMOCIÓN");
            headerCell2.setCellStyle(headerStyle);

            agregarFila(sheet, rowNum++, "PROMOCIÓN:", cliente.getPlanActual());
            agregarFila(sheet, rowNum++, "TV / DECO:", "");
            agregarFila(sheet, rowNum++, "GRABACIÓN OCM:", "");
            agregarFila(sheet, rowNum++, "MOVIL CONTACTO:", cliente.getMovilContacto());
            agregarFila(sheet, rowNum++, "FIJO / COMPAÑÍA:", cliente.getFijoCompania());

            // Iterar sobre los móviles a portar
            if (cliente.getMovilesAPortar() != null && !cliente.getMovilesAPortar().isEmpty()) {
                for (int i = 0; i < cliente.getMovilesAPortar().size(); i++) {
                    agregarFila(sheet, rowNum++,
                            "MOVIL A PORTAR " + (i + 1) + " / COMPAÑÍA:",
                            cliente.getMovilesAPortar().get(i));
                }
            }

            // Otros campos de ejemplo
            agregarFila(sheet, rowNum++, "PRECIO PROMOCIÓN / TIEMPO:", "");
            agregarFila(sheet, rowNum++, "PRECIO REAL O DESPUÉS DE PROMOCIÓN:", "");
            agregarFila(sheet, rowNum++, "SEGMENTO:", "");
            agregarFila(sheet, rowNum++, "COMENTARIOS RELEVANTES CON EL CLIENTE:", "");
            agregarFila(sheet, rowNum++, "COMERCIAL:", cliente.getUsuario().getNombre());
            agregarFila(sheet, rowNum++, "ASIGNADO A:","");
            agregarFila(sheet, rowNum++, "OBSERVACIONES:", "");
            agregarFila(sheet, rowNum++, "TIPO DE USUARIO:", "");

            // Ajustar el ancho de las columnas
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }



    /**
     * =========================================================================
     *  MÉTODOS AUXILIARES
     * =========================================================================
     */

    // Crea un estilo de encabezado (fondo verde y texto en blanco, negrita)
    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }

    // Crea una celda con el valor 'texto' y aplica un estilo
    private void crearCeldaConEstilo(Row row, int columnIndex, String texto, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(texto);
        cell.setCellStyle(style);
    }

    // Método auxiliar para agregar filas en el Excel "vertical"
    private void agregarFila(Sheet sheet, int rowNum, String titulo, String valor) {
        if (valor == null) valor = "";
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(titulo);
        row.createCell(1).setCellValue(valor);
    }
}
