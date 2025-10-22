# Flora Streams

Aplicación Android para leer JSONs y mostrar su contenido de forma estructurada con conexión a reproductor AceStream.

## Características
- Gestión de listas JSON
- Integración directa con AceStream
- Soporte Android TV
- Interfaz por categorías

## Formato JSON

```json
[
  {
    "name": "Televisión",
    "subcategories": [
      {
        "name": "La 1",
        "icon": "https://ejemplo.com/icono.png",
        "urls": [
          {
            "name": "Full HD",
            "url": "acestream://hash-del-stream"
          }
        ]
      }
    ]
  }
]
```

## Uso
1. Abre la app
2. Toca el botón "+" para añadir una lista JSON
3. Introduce la URL del JSON
4. Navega por las categorías y toca cualquier stream para reproducirlo

## Requisitos
- Android 7.0+
- AceStream instalado