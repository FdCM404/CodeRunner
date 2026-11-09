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

## Pré-requisitos

JDK 21, Gradle, Docker e Redis. O container de execução usa
`python:3.12-slim`, `--network none`, 256 MiB e um limite de 10 segundos.

## Executar localmente

```powershell

docker run --rm -d --name coderunner-redis -p 6379:6379 redis:7-alpine

.\gradlew.bat :manager-server:shadowJar :run-server:shadowJar :client:shadowJar

java -jar manager-server/build/libs/manager-server-1.0-all.jar 5000

java -jar run-server/build/libs/run-server-1.0-all.jar 127.0.0.1 5001 127.0.0.1 5000 127.0.0.1 6379
java -jar run-server/build/libs/run-server-1.0-all.jar 127.0.0.1 5002 127.0.0.1 5000 127.0.0.1 6379
java -jar run-server/build/libs/run-server-1.0-all.jar 127.0.0.1 5003 127.0.0.1 5000 127.0.0.1 6379

java -jar client/build/libs/client-1.0-all.jar scripts/programa.py input.txt 127.0.0.1 5000
```

## Executar no GCP após a criação e configuração das VMs (vm-run-1, vm-run-2, vm-manager) e da importação através do "Bitvise SSH Cli" dos ficheiros .jar:

```powershell

# Na VM do ManagerServer
export REDIS_HOST=127.0.0.1
export REDIS_ADVERTISE_HOST=<IP_INTERNO_VM_MNG>
export REDIS_PORT=6379

java -jar manager-server-1.0-all.jar 5000

#RNSV1
java -jar run-server-1.0-all.jar <IP_EXT_RUNSV1> 5001 <IP_INTERNO_VM_MNG> 5000 <IP_INTERNO_VM_MNG> 6379


#CLI

java -jar client-1.0-all.jar scripts/programa.py input.txt <IP_EXT_VM_MNG> 5000

ou a correr o cliente localmente:

java -jar client\build\libs\client-1.0-all.jar scripts/programa.py input.txt <IP_EXT_VM_MNG> 5000
```



Os três processos devem ser iniciados em terminais separados, pela ordem
Redis, ManagerServer, RunServer e Client. O `RunServer` exige pelo menos
`host` e `port`; o `Client` exige o caminho do programa Python. O Docker
Desktop tem de estar iniciado antes de executar o Redis ou os pedidos.

O comando `Client` imprime o identificador, acompanha o estado e escreve stdout
e stderr nos canais correspondentes. Para várias instâncias, iniciar cada
RunServer numa porta diferente e registar todas no mesmo ManagerServer.

O `RunServer` lança e monitoriza os containers através da biblioteca
`docker-java`, seguindo o exemplo em `Trabs/TP1/3-docker-java-examples`.
Em Linux, o container é executado com o UID/GID atual quando disponível. Em
instalações com Redis noutra VM, configure no ManagerServer `REDIS_HOST` para
a ligação local e `REDIS_ADVERTISE_HOST` para o hostname/IP que os RunServers
conseguem alcançar.

Todos os componentes escrevem logs de diagnóstico para a consola. Os logs
incluem timestamp, IP/porto, método gRPC, identificador do pedido, estado,
tamanho dos dados, operações Redis e ciclo de vida do container, sem imprimir
o conteúdo dos programas ou dos ficheiros de input.

Com Redis em Docker, estes comandos mostram o estado dos pedidos:

```powershell
docker exec coderunner-redis redis-cli KEYS "coderunner:request:*"
docker exec coderunner-redis redis-cli HGETALL coderunner:request:<id>
```
E para acompanhar todas as operações Redis em tempo real:

```powershell
docker exec coderunner-redis redis-cli MONITOR
```


Outros comandos uteis para observar as portas em utilização no Windows:

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

## Docker Compose

O `docker-compose.yml` inicia Redis, ManagerServer e um RunServer. O RunServer
recebe acesso ao socket Docker para criar containers.

```powershell
docker compose up --build -d
```