// =====================================================================
//  app.js: funciones que usan TODAS las paginas
//  (se carga desde el fragmento "scripts" de templates/fragments/layout.html)
// =====================================================================

// ---------- DataTables en espanol ----------
const DT_ES = {
    search: 'Buscar:',
    lengthMenu: 'Mostrar _MENU_ registros',
    info: 'Mostrando _START_ a _END_ de _TOTAL_ registros',
    infoEmpty: 'Mostrando 0 a 0 de 0 registros',
    infoFiltered: '(filtrado de _MAX_ registros)',
    zeroRecords: 'No se encontraron resultados',
    emptyTable: 'No hay datos',
    loadingRecords: 'Cargando...',
    paginate: { first: 'Primero', last: 'Último', next: 'Siguiente', previous: 'Anterior' }
};

// ---------- Notify.js: notificacion pequena en la esquina ----------
// tipo: 'success' | 'info' | 'warn' | 'error'
function notificar(mensaje, tipo = 'success') {
    $.notify(mensaje, { className: tipo, globalPosition: 'top right', autoHideDelay: 3500 });
}

// ---------- SweetAlert2: ventana de confirmacion ----------
// Devuelve una promesa con true si el usuario confirmo.
async function confirmar(titulo, texto = 'Esta acción no se puede deshacer') {
    const r = await Swal.fire({
        title: titulo,
        text: texto,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, continuar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#B02A37',
        cancelButtonColor: '#6c757d'
    });
    return r.isConfirmed;
}

// ---------- SweetAlert2: ventana de error ----------
function mostrarError(titulo, error) {
    Swal.fire({ icon: 'error', title: titulo, text: error.message || String(error) });
}

// ---------- fetch(): llamar a un endpoint REST y leer la respuesta ----------
// metodo: 'GET', 'POST', 'PUT', 'DELETE'.  cuerpo: objeto que se envia como JSON.
// Si el servidor responde con error (400, 404, 409...) lanza un Error con un mensaje legible.
async function api(url, metodo = 'GET', cuerpo) {
    const opciones = { method: metodo, headers: {} };
    if (cuerpo !== undefined) {
        opciones.headers['Content-Type'] = 'application/json'; // Le decimos al servidor que enviamos JSON
        opciones.body = JSON.stringify(cuerpo);                 // Objeto JavaScript -> texto JSON
    }
    const respuesta = await fetch(url, opciones);
    const texto = await respuesta.text();                       // 204 No Content trae el cuerpo vacio

    if (respuesta.ok) {                                         // 200-299
        return texto ? JSON.parse(texto) : null;
    }

    // Error: armamos un mensaje entendible
    let mensaje = texto || `Error ${respuesta.status}`;         // 409: el controller manda un texto
    try {
        const json = JSON.parse(texto);
        if (json.status && json.error) {                        // Error generico de Spring (404, 500...)
            mensaje = `${json.status} ${json.error}`;
        } else if (typeof json === 'object') {                  // 400 de validacion: { campo: mensaje }
            mensaje = Object.values(json).join('\n');
        }
    } catch (e) { /* no era JSON: usamos el texto tal cual */ }
    throw new Error(mensaje);
}

// ---------- Escapar texto antes de meterlo en HTML (evita inyectar etiquetas) ----------
function escapar(texto) {
    const div = document.createElement('div');
    div.textContent = texto ?? '';
    return div.innerHTML;
}

// ---------- Formato de dinero con 2 decimales ----------
function dinero(valor) {
    return Number(valor ?? 0).toLocaleString('es-NI', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// ---------- Recargar la pagina y mostrar un aviso despues ----------
// sessionStorage guarda el mensaje mientras la pagina se recarga.
function recargarConAviso(mensaje) {
    sessionStorage.setItem('aviso', mensaje);
    location.reload();
}
document.addEventListener('DOMContentLoaded', () => {
    const aviso = sessionStorage.getItem('aviso');
    if (aviso) {
        sessionStorage.removeItem('aviso');
        notificar(aviso, 'success');
    }
});

// ---------- Botones con data-confirmar (por ejemplo, eliminar categoria) ----------
// Antes de enviar el formulario, pregunta con SweetAlert2.
document.addEventListener('click', async (evento) => {
    const boton = evento.target.closest('[data-confirmar]');
    if (!boton) return;
    evento.preventDefault();                               // Detiene el envio normal del formulario
    if (await confirmar(boton.dataset.confirmar)) {
        boton.closest('form').submit();                    // Si confirma, ahora si se envia
    }
});
