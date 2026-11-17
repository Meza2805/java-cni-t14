// Paquete de los REPORTES (Excel y PDF)
package com.example.demo.reporte;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;   // Cabecera que le dice al navegador "descarga este archivo"
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;            // Tipo de contenido (application/pdf, ...)
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

// =====================================================================
//  Un endpoint no siempre devuelve JSON o HTML: tambien puede devolver ARCHIVOS.
//  La clave son dos cabeceras de la respuesta:
//    Content-Type        -> que tipo de archivo es (Excel, PDF...)
//    Content-Disposition -> "attachment; filename=..." = descargar con ese nombre
// =====================================================================
@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    // Tipo MIME oficial de los archivos .xlsx
    private static final MediaType EXCEL =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    // ===================== Pagina con los botones de descarga =====================
    @GetMapping                                     // GET /reportes
    public String pagina() {
        return "reportes";                          // templates/reportes.html
    }

    // ===================== Excel =====================
    @GetMapping("/productos.xlsx")                  // GET /reportes/productos.xlsx
    public ResponseEntity<byte[]> excel() throws IOException {
        return archivo(reporteService.productosExcel(), "productos.xlsx", EXCEL);
    }

    // ===================== PDF =====================
    @GetMapping("/productos.pdf")                   // GET /reportes/productos.pdf
    public ResponseEntity<byte[]> pdf() {
        return archivo(reporteService.productosPdf(), "productos.pdf", MediaType.APPLICATION_PDF);
    }

    // Arma la respuesta: 200 OK + cabeceras + los bytes del archivo
    private ResponseEntity<byte[]> archivo(byte[] bytes, String nombre, MediaType tipo) {
        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nombre).build().toString())
                .body(bytes);
    }
}
