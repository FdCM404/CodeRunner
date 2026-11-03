# CodeRunner (esboço)

Implementação modular de referência da arquitetura proposta:

- **contract**: contrato Protobuf/gRPC partilhado pelos outros módulos.
- **manager-server** (`:5000`): regista RunServers, devolve servidores em round-robin e localiza o dono de um pedido.
- **run-server**: recebe o programa por streaming, guarda apenas metadados no Redis e executa cada pedido num container Python efémero.
- **Redis**: guarda `state` (`PENDING`, `RUNNING`, `FINISHED` ou `FAILED`) e `owner` (`host:port`); stdout/stderr ficam no RunServer.
- **client**: resolve o RunServer através do ManagerServer, submete e consulta o resultado mesmo após mudar de RunServer.

## Estrutura Gradle

```text
CodeRunner/
├── contract/
├── manager-server/
├── run-server/
└── client/
```

O módulo `contract` é uma biblioteca; `manager-server`, `run-server` e `client`
são aplicações independentes que dependem dele.

## Pré-requisitos

JDK 21, Gradle, Docker e Redis. O container de execução usa
`python:3.12-slim`, `--network none`, 256 MiB e um limite de 10 segundos.

## Executar localmente

```powershell
docker run --rm -d --name coderunner-redis -p 6379:6379 redis:7-alpine
gradle :manager-server:shadowJar :run-server:shadowJar :client:shadowJar
java -jar manager-server/build/libs/manager-server-1.0-all.jar 5000
java -jar run-server/build/libs/run-server-1.0-all.jar 127.0.0.1 5001
java -jar client/build/libs/client-1.0-all.jar programa.py input.txt
```

O comando `Client` imprime o identificador, acompanha o estado e escreve stdout
e stderr nos canais correspondentes. Para várias instâncias, iniciar cada
RunServer numa porta diferente e registar todas no mesmo ManagerServer.

O `RunServer` executa os containers sem privilégios de root. Em Linux, configure
`RUNNER_UID` e `RUNNER_GID` com o UID/GID do utilizador que deve ser dono dos
ficheiros montados (por omissão são usados `1000:1000`). Em instalações com
Redis noutra VM, configure no ManagerServer `REDIS_HOST` para a ligação local
e `REDIS_ADVERTISE_HOST` para o hostname/IP que os RunServers conseguem
alcançar.

Todos os componentes escrevem logs de diagnóstico para a consola. Os logs
incluem timestamp, IP/porto, método gRPC, identificador do pedido, estado,
tamanho dos dados, operações Redis e ciclo de vida do container, sem imprimir
o conteúdo dos programas ou dos ficheiros de input.

Para observar as portas em utilização no Windows:

```powershell
Get-NetTCPConnection -State Listen |
    Sort-Object LocalPort |
    Format-Table LocalAddress,LocalPort,OwningProcess
```

Para observar os processos associados às portas do CodeRunner:

```powershell
Get-NetTCPConnection -LocalPort 5001,5002,6379 -ErrorAction SilentlyContinue |
    Select-Object LocalAddress,LocalPort,OwningProcess
```

Com Redis em Docker, estes comandos mostram o estado dos pedidos:

```powershell
docker exec coderunner-redis redis-cli KEYS "coderunner:request:*"
docker exec coderunner-redis redis-cli HGETALL coderunner:request:<id>
```

Para acompanhar todas as operações Redis em tempo real:

```powershell
docker exec coderunner-redis redis-cli MONITOR
```

`MONITOR` é muito detalhado e deve ser usado apenas durante o diagnóstico.

## Docker Compose

O `docker-compose.yml` inicia Redis, ManagerServer e um RunServer. O RunServer
recebe acesso ao socket Docker para criar containers efémeros; em produção
deve ser usado um executor dedicado com políticas equivalentes e isolamento
adicional.

Este é um esboço académico: autenticação, TLS, quotas por utilizador,
limpeza periódica e persistência dos ficheiros após reinício ainda devem ser
acrescentados antes de uma utilização real.
