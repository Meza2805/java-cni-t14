// Paquete de los REPORTES (Excel y PDF)
package com.example.demo.reporte;

import com.example.demo.categoria.CategoriaRepository;
import com.example.demo.categoria.CategoriaResumen;
import com.example.demo.producto.Producto;
import com.example.demo.producto.ProductoRepository;
import com.lowagie.text.Document;            // OpenPDF: el documento PDF
import com.lowagie.text.Element;             // Alineaciones (CENTER, RIGHT...)
import com.lowagie.text.Font;                // Tipo, tamano y color de letra
import com.lowagie.text.PageSize;            // Tamano de hoja (A4, LETTER...)
import com.lowagie.text.Paragraph;           // Un parrafo de texto
import com.lowagie.text.Phrase;              // Texto dentro de una celda
import com.lowagie.text.pdf.PdfPCell;        // Celda de una tabla PDF
import com.lowagie.text.pdf.PdfPTable;       // Tabla PDF
import com.lowagie.text.pdf.PdfWriter;       // Escribe el documento en bytes
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;        // Apache POI: Workbook, Sheet, Row, Cell, CellStyle...
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Libro de Excel moderno (.xlsx)
import org.springframework.stereotype.Service;     // @Service: clase con LOGICA de negocio

import java.awt.Color;                       // Colores para el PDF
import java.io.ByteArrayOutputStream;        // "Archivo en memoria": los bytes que se envian al navegador
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// =====================================================================
//  ReporteService: arma los archivos Excel y PDF en MEMORIA (byte[])
// =====================================================================
//  El controller (ReporteController) solo pide los bytes y los entrega con
//  las cabeceras correctas. Separar asi la logica es la idea de la capa SERVICE.
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ProductoRepository productoRepo;
    private final CategoriaRepository categoriaRepo;

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =====================================================================
    //                        EXCEL con Apache POI
    // =====================================================================
    public byte[] productosExcel() throws IOException {
        // try-with-resources: cierra el libro y el flujo aunque ocurra un error
        try (Workbook libro = new XSSFWorkbook();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            CellStyle encabezado = estiloEncabezado(libro);   // Negrita, fondo azul, letra blanca
            CellStyle moneda = libro.createCellStyle();       // Formato numerico con 2 decimales
            moneda.setDataFormat(libro.createDataFormat().getFormat("#,##0.00"));

            // ---------- Hoja 1: Productos ----------
            Sheet hoja = libro.createSheet("Productos");      // Pestana inferior del Excel
            crearEncabezado(hoja, encabezado, "ID", "Producto", "Precio", "Categoría");
            int fila = 1;                                     // La fila 0 es el encabezado
            for (Producto p : productoRepo.findAll()) {       // SELECT * FROM productos (+ categoria)
                Row r = hoja.createRow(fila++);
                r.createCell(0).setCellValue(p.getId());
                r.createCell(1).setCellValue(p.getNombre());
                Cell precio = r.createCell(2);
                precio.setCellValue(p.getPrecio() == null ? 0 : p.getPrecio());
                precio.setCellStyle(moneda);
                r.createCell(3).setCellValue(p.getCategoria() == null ? "Sin categoría" : p.getCategoria().getNombre());
            }
            anchos(hoja, 8, 30, 14, 20);                      // Ancho de columnas (en caracteres)

            // ---------- Hoja 2: Resumen por categoria (LEFT JOIN + GROUP BY) ----------
            Sheet resumen = libro.createSheet("Resumen");
            crearEncabezado(resumen, encabezado, "Categoría", "Total productos", "Precio promedio");
            fila = 1;
            for (CategoriaResumen c : categoriaRepo.resumen()) {
                Row r = resumen.createRow(fila++);
                r.createCell(0).setCellValue(c.categoria());
                r.createCell(1).setCellValue(c.totalProductos());
                Cell promedio = r.createCell(2);
                if (c.precioPromedio() != null) {             // Ropa no tiene productos: promedio NULL
                    promedio.setCellValue(c.precioPromedio());
                    promedio.setCellStyle(moneda);
                } else {
                    promedio.setCellValue("-");
                }
            }
            anchos(resumen, 20, 16, 16);

            libro.write(salida);                              // Escribe el .xlsx en memoria
            return salida.toByteArray();                      // Los bytes del archivo
        }
    }

    private CellStyle estiloEncabezado(Workbook libro) {
        org.apache.poi.ss.usermodel.Font fuente = libro.createFont(); // Nombre completo: tambien existe com.lowagie.text.Font
        fuente.setBold(true);
        fuente.setColor(IndexedColors.WHITE.getIndex());
        CellStyle estilo = libro.createCellStyle();
        estilo.setFont(fuente);
        estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return estilo;
    }

    private void crearEncabezado(Sheet hoja, CellStyle estilo, String... titulos) {
        Row r = hoja.createRow(0);
        for (int i = 0; i < titulos.length; i++) {
            Cell c = r.createCell(i);
            c.setCellValue(titulos[i]);
            c.setCellStyle(estilo);
        }
    }

    private void anchos(Sheet hoja, int... caracteres) {
        for (int i = 0; i < caracteres.length; i++) {
            hoja.setColumnWidth(i, caracteres[i] * 256);      // POI mide el ancho en 1/256 de caracter
        }
    }

    // =====================================================================
    //                          PDF con OpenPDF
    // =====================================================================
    public byte[] productosPdf() {
        Color azul = new Color(19, 37, 74);                   // Mismo azul oscuro del curso
        Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD, azul);
        Font subtitulo = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
        Font seccion = new Font(Font.HELVETICA, 13, Font.BOLD, azul);
        Font cabecera = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 50, 40); // Margenes: izq, der, arriba, abajo
        PdfWriter.getInstance(doc, salida);                   // El PDF se escribe en "salida"
        doc.open();

        doc.add(new Paragraph("Reporte de productos", titulo));
        doc.add(new Paragraph("Generado el " + LocalDateTime.now().format(FECHA) + " · CRUD Spring Boot", subtitulo));
        doc.add(new Paragraph(" "));                          // Linea en blanco

        // ---------- Tabla de productos ----------
        List<Producto> productos = productoRepo.findAll();
        PdfPTable tabla = new PdfPTable(new float[]{1, 4, 2, 3}); // 4 columnas con anchos relativos
        tabla.setWidthPercentage(100);
        tabla.getDefaultCell().setPadding(5);                 // Espacio interno de las celdas
        celdasCabecera(tabla, cabecera, azul, "ID", "Producto", "Precio", "Categoría");
        for (Producto p : productos) {
            tabla.addCell(new Phrase(String.valueOf(p.getId()), normal));
            tabla.addCell(new Phrase(p.getNombre(), normal));
            PdfPCell precio = new PdfPCell(new Phrase(String.format("%,.2f", p.getPrecio() == null ? 0 : p.getPrecio()), normal));
            precio.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tabla.addCell(precio);
            tabla.addCell(new Phrase(p.getCategoria() == null ? "Sin categoría" : p.getCategoria().getNombre(), normal));
        }
        doc.add(tabla);
        doc.add(new Paragraph("Total de productos: " + productos.size(), subtitulo));
        doc.add(new Paragraph(" "));

        // ---------- Resumen por categoria ----------
        doc.add(new Paragraph("Resumen por categoría", seccion));
        doc.add(new Paragraph(" "));
        PdfPTable resumen = new PdfPTable(new float[]{4, 2, 2});
        resumen.setWidthPercentage(100);
        resumen.getDefaultCell().setPadding(5);
        celdasCabecera(resumen, cabecera, azul, "Categoría", "Total", "Precio promedio");
        for (CategoriaResumen c : categoriaRepo.resumen()) {
            resumen.addCell(new Phrase(c.categoria(), normal));
            resumen.addCell(new Phrase(String.valueOf(c.totalProductos()), normal));
            resumen.addCell(new Phrase(c.precioPromedio() == null ? "-" : String.format("%,.2f", c.precioPromedio()), normal));
        }
        doc.add(resumen);

        doc.close();                                          // Cierra el documento: ya esta completo
        return salida.toByteArray();
    }

    private void celdasCabecera(PdfPTable tabla, Font fuente, Color fondo, String... titulos) {
        for (String t : titulos) {
            PdfPCell celda = new PdfPCell(new Phrase(t, fuente));
            celda.setBackgroundColor(fondo);
            celda.setPadding(6);
            tabla.addCell(celda);
        }
        tabla.setHeaderRows(1);                               // Si la tabla pasa de pagina, repite el encabezado
    }
}
