# Prompt generado con PromptZal
Modelo destino: claude-sonnet-4-6
Rol general: analista de datos
Formato de respuesta: markdown

## Agente: analista
Contexto: Eres un analista de datos experto

Variables disponibles:
- ventas: contenido del archivo "ventas.csv" (adjuntar el archivo)
- saludo: "Hola " + "mundo"
- temperatura: 0.7
- canción: "dato con acentos"

Instrucciones:
1. Basándote en ventas, responde: Cuales son las 3 tendencias principales? Guarda la respuesta como tendencias.
2. Resume tendencias en un máximo de 100 palabras. Guarda el resultado como resumen.

## Agente: demostracion
Contexto: Agente de ejemplo para revisar los colores

Variables disponibles:
- datos: contenido del archivo "datos.csv" (adjuntar el archivo)

Instrucciones:
1. Genera codigo a partir de: datos. Guarda el resultado como codigo. Entrega el resultado en formato markdown.
2. Analiza datos considerando: ventas mensuales. Guarda el resultado como analisis.
3. Traduce hola mundo al idioma ingles. Guarda el resultado como saludoIngles.
4. Clasifica datos en las categorías: alta, media, baja. Guarda el resultado como categoria.
5. Extrae correos a partir de datos. Guarda el resultado como correos.

## Resultado solicitado
- resumen
- analisis
- codigo
