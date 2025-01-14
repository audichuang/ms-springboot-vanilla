# Observability Deployment


## Architecture

Service A >> Open Telemetric Collector >> Grafana Loki, Prometheus, Tempo
Service B >> Open Telemetric Collector >> Grafana Loki, Prometheus, Tempo
Service C >> Open Telemetric Collector >> Grafana Loki, Prometheus, Tempo

## Open Telemetry - Setup

https://opentelemetry.io/docs/collector/configuration/

Start - OpenTelemetry Docker Container: Listens on Port 4317, 4318, and 55679
```
startOtel otel-config.yaml
```


## Grafana

Mac OS
```
arch -arm64 brew install grafana
```

Start - Grafana: Listens on Port 3000
```
brew services restart grafana
```


### Prometheus - Metrics: Setup

Mac OS
```
arch -arm64 brew install prometheus
```

Start - Premetheus: Listens on Port 9090
```
brew services restart prometheus
```

### Loki - Logs: Setup

Mac OS
```
arch -arm64 brew install loki
```

Start - Loki: Listens on port 3100
```
brew services start loki
```


### Tempo - Traces: Setup

Mac OS
Download from GitHub: https://github.com/grafana/tempo/releases/tag/v2.6.1

Start - Tempo - Listens on Port 3200
```
tempo -config.file tempo-config.yaml
```


