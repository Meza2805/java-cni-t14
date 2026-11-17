// =====================================================================
//  productos.js: la pagina /productos consume la API REST /api/productos
// =====================================================================

// ---------- 1) La tabla se llena con el JSON de GET /api/productos/listar ----------
const tabla = new DataTable('#tablaProductos', {
    ajax: { url: '/api/productos/listar', dataSrc: '' },   // dataSrc '' = el JSON ya es un arreglo [...]
    language: DT_ES,
    order: [[0, 'asc']],
    columns: [
        { data: 'id' },
        { data: 'nombre', render: DataTable.render.text() },              // render.text() escapa el HTML
        { data: 'precio', className: 'text-end', render: (precio) => dinero(precio) },
        {
            data: 'categoria',                                            // Objeto { id, nombre, ... } o null
            render: (cat) => cat
                ? `<span class="badge text-bg-primary">${escapar(cat.nombre)}</span>`
                : '<span class="badge text-bg-secondary">Sin categoría</span>'
        },
        {
            data: null, orderable: false, className: 'text-end text-nowrap',
            render: () => `
                <button class="btn btn-sm btn-outline-primary btn-editar" title="Editar"><i class="bi bi-pencil"></i></button>
                <button class="btn btn-sm btn-outline-danger btn-eliminar" title="Eliminar"><i class="bi bi-trash"></i></button>`
        }
    ]
});

// ---------- 2) Ventana modal y campos del formulario ----------
const modal = new bootstrap.Modal('#modalProducto');
const form = document.getElementById('formProducto');
const campo = {
    id: document.getElementById('productoId'),
    nombre: document.getElementById('nombre'),
    precio: document.getElementById('precio'),
    categoria: document.getElementById('categoriaId')
};

function abrirModal(producto) {
    form.classList.remove('was-validated');
    document.getElementById('tituloModal').textContent = producto ? 'Editar producto' : 'Nuevo producto';
    campo.id.value = producto?.id ?? '';
    campo.nombre.value = producto?.nombre ?? '';
    campo.precio.value = producto?.precio ?? '';
    campo.categoria.value = producto?.categoria?.id ?? '';
    modal.show();
}

document.getElementById('btnNuevo').addEventListener('click', () => abrirModal(null));

// Boton editar de cada fila: DataTables nos da el objeto JSON completo de esa fila
$('#tablaProductos tbody').on('click', '.btn-editar', function () {
    abrirModal(tabla.row($(this).closest('tr')).data());
});

// ---------- 3) Guardar: POST (nuevo) o PUT (editar) ----------
form.addEventListener('submit', async (evento) => {
    evento.preventDefault();                            // No recargar la pagina
    if (!form.checkValidity()) {                        // Validacion del navegador (required, min)
        form.classList.add('was-validated');
        return;
    }

    const id = campo.id.value;
    const categoriaId = campo.categoria.value;
    const datos = { nombre: campo.nombre.value.trim(), precio: Number(campo.precio.value) };

    try {
        if (!id) {
            // POST /api/productos/crear  ->  { "nombre": "...", "precio": 10, "categoria": { "id": 1 } }
            if (categoriaId) datos.categoria = { id: Number(categoriaId) };
            const creado = await api('/api/productos/crear', 'POST', datos);
            notificar(`Producto "${creado.nombre}" creado`);
        } else {
            // PUT /api/productos/actualizar/{id}  (nombre y precio)
            await api(`/api/productos/actualizar/${id}`, 'PUT', datos);
            // La categoria se cambia con los endpoints de la RELACION
            if (categoriaId) {
                await api(`/api/productos/${id}/categoria/${categoriaId}`, 'PUT');
            } else {
                await api(`/api/productos/${id}/categoria`, 'DELETE');
            }
            notificar('Producto actualizado');
        }
        modal.hide();
        tabla.ajax.reload(null, false);                 // Vuelve a pedir GET /api/productos/listar
    } catch (error) {
        mostrarError('No se pudo guardar', error);
    }
});

// ---------- 4) Eliminar: SweetAlert2 + DELETE /api/productos/eliminar/{id} ----------
$('#tablaProductos tbody').on('click', '.btn-eliminar', async function () {
    const producto = tabla.row($(this).closest('tr')).data();
    if (!await confirmar(`¿Eliminar "${producto.nombre}"?`)) return;
    try {
        await api(`/api/productos/eliminar/${producto.id}`, 'DELETE'); // 204 No Content
        notificar('Producto eliminado', 'info');
        tabla.ajax.reload(null, false);
    } catch (error) {
        mostrarError('No se pudo eliminar', error);
    }
});
