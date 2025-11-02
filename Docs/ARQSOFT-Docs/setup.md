### Setup para correr o projeto
#### Redis
```bash
# Para instalar o redis (via docker)
docker run --name redis -p 6379:6379 -d redis

# Verificar se está a funcionar corretamente
docker exec -it redis redis-cli ping
```