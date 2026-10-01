# Adicionar 4000 Pontos Clusterizados no Mapa (MapLibre)

Vamos implementar um sistema de clusterização para lidar com os 4000 pontos em Quixadá. A biblioteca MapLibre já possui suporte nativo excelente para clusterização através de fontes de dados GeoJSON (`GeoJsonSource`).

## User Review Required

> [!NOTE]
> Vou criar o arquivo GeoJSON com 4000 pontos aleatórios localizados na região de Quixadá. Este arquivo será embutido no app e lido de forma eficiente. O MapLibre fará o agrupamento matemático por baixo dos panos (clustering) para que o mapa rode fluido.

## Proposed Changes

### Assets

#### [NEW] app/src/main/assets/points.geojson
- Criar o arquivo `points.geojson` que conterá a `FeatureCollection` com 4000 pontos simulados. (Este arquivo já foi gerado de antemão pelo meu script localmente).

### UI (Presentation)

#### [MODIFY] app/src/main/java/com/example/cacaplaca/ui/presentation/map/Map.kt
1. Ler o arquivo `points.geojson` nativamente utilizando a URI `asset://points.geojson`.
2. Adicionar uma `GeoJsonSource` ao mapa do MapLibre ativando a configuração `cluster(true)`.
3. Configurar três camadas (Layers) baseadas nesta fonte:
   - Uma **CircleLayer** para renderizar os círculos dos clusters agrupados.
   - Uma **SymbolLayer** para mostrar a quantidade de pontos dentro daquele cluster agrupado (um número dentro do círculo).
   - Uma **CircleLayer** (ou SymbolLayer) separada que não é cluster (os pontos individuais quando o zoom chega bem perto).

## Verification Plan

- Compilar a aplicação
- Abrir o mapa e verificar se em zoom mais afastado vemos círculos grandes escritos "400", "500", etc.
- Dar zoom in na região de Quixadá e confirmar que os agrupamentos vão se dividindo até que se tornam pontos unitários e individuais de forma performática.
