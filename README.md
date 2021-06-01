# MercaUJA
Mercado de subastas, como Ebay, donde los vendedores publican anuncios del producto que quieren vender y que podrán adquirir los compradores.

## Análisis
A continuación, detallaré como he dedicidido que se comportan cada una de las clases.

### Producto
El producto se compone de:
- *tipo* : tipo de producto, es un enum que puede ser `ALIMENTACION`, `ELECTRONICA`, `ROPA` y `OTROS`;
- *precioActual* : indica el precio del producto, ira variando con las pujas. El precio inicial es un valor aleatorio dependiendo del tipo de producto, siendo el precio minimo 1
- *comprador* : es una referencia al último comprador que realizo la puja mas alta, si no se ha pujado por el producto vale `null`.
- *vendido* : indica si el producto ya esta vendido.
- *puestoEnVenta* : tiempo en milisegundos en el que se inscribió el objeto en el catálogo.
- *tiempo* : tiempo que esta el producto a la venta, valor aleatorio entre 1000 y 3000 ms.

Una vez explicadas las variables que componen un producto pasemos a explicar como funciona un producto. Tras inicializarse se puede establecer el tiempo de puesto en venta, pujar por él (indicando el comprador que realiza la puja) y dar el producto como vendido. Cuando se puja por un producto se realiza un depósito para asegurar de que el comprador puede pagar por él, cuando otro comprador supera la oferta el depósito es devuelto.

Para obtener el precio actual, pujar, consultar si el producto ha sido vendido y establecer el producto como vendido se utiliza un *ReentrantLock* para garantizar la excusión mutua en esas operaciones.

### Vendedor
El vendedor se compone de:
- *name* : nombre del vendedor.
- *listaProductos* : lista de productos a la venta, se genera de manera aleatoria cuando se crea el vendedor.
- *esperaVentas* : elemento de sincronización (CountDownLatch) con los productos, cada uno de ello tiene una referencia a este elemento.
- *mercado* : acceso al catálogo y a las operaciones del mercado.

Una vez inicializado, el vendedor añade sus productos al catálogo. Tras esto espera hasta que todos los productos son vendidos (mediante el CountDownLatch) o hasta que es interrumpido. Después presenta el informe sobre de los productos vendidos.

### Comprador
El comprador se compone de:
- *name* : nombre del comprador.
- *productosDeseados* : lista con los tipos productos que se desea comprar, se genera de manera aleatoria cuando se crea el comprador.
- *productosComprados* : lista con los productos comprados, se van incluyendo los productos cuando termina su tiempo de puja y se adquieren.
- *mercado* : acceso al catálogo y a las operaciones del mercado.

Una vez inicializado, el comprador intenta pujar. Si hay productos en el catálogo, se busca el producto mas barato de alguno de los tipos por los que está interesado el comprador. Tras intentar pujar espera un breve instante para que se realicen nuevas pujas, tras lo cual vuelve a intentar pujar. Por último, cuando compra todos los productos que deseaba o lo cancelan, presenta su informe.

### Mercado
El mercado se compone de:
- *registro* : lista de entradas con las operaciones realizadas (venta de los productos).
- *catalogo* : lista de productos en venta en el mercado.
- *ejecutor* : ExecutorService donde se ejecutan las tareas para dar servicios a los vendedores y los compradores.

Cuando el mercado se inicia crea la tarea que le avise del cierre del mercado, después de eso espera a que la tarea avise del cierre del mismo. Los vendedores y compradores van solicitando servicios al mercado, y este va creando tareas que satisfagan estos servicios. Tras el cierre del mercado cancela los procesos restantes (vendedores y compradores), obtiene sus resultados y presenta el ranking de vendedores y compradores.
