// =====================================================================
//  relaciones.js: agregar y quitar favoritos con los endpoints REST
//    POST   /api/clientes/{id}/favoritos/{productoId}
//    DELETE /api/clientes/{id}/favoritos/{productoId}
// =====================================================================

// ---------- Agregar un favorito ----------
document.getElementById('formFavorito').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const clienteId = document.getElementById('favCliente').value;
    const productoId = document.getElementById('favProducto').value;
    try {
        await api(`/api/clientes/${clienteId}/favoritos/${productoId}`, 'POST');
        recargarConAviso('Favorito agregado');           // Thymeleaf vuelve a dibujar la tabla
    } catch (error) {
        mostrarError('No se pudo agregar', error);        // 409 si ya era favorito
    }
});

// ---------- Quitar un favorito (la "x" de cada pildora) ----------
document.addEventListener('click', async (evento) => {
    const boton = evento.target.closest('.btn-quitar');
    if (!boton) return;
    const { cliente, producto, nombre } = boton.dataset;  // data-cliente, data-producto, data-nombre
    if (!await confirmar(`¿Quitar "${nombre}" de los favoritos?`, 'Se borra la fila de la tabla cliente_favoritos')) return;
    try {
        await api(`/api/clientes/${cliente}/favoritos/${producto}`, 'DELETE');
        recargarConAviso('Favorito quitado');
    } catch (error) {
        mostrarError('No se pudo quitar', error);
    }
});
