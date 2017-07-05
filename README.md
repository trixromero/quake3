Setup:

- java 8
- Maven 3.0 ou superior
- MongoDb server rodando localmente na porta 27017
- porta 8080 livre

configurações referente ao Mongo e a porta que a aplicacao iniciará podem ser alteradas no arquivo "src/main/resourcer/application.properties" se necessário.

Clonar o repositorio
navegar ate a pasta raiz

executar:
mvn clean install (testes unitarios e de integração executados neste passo)
mvn spring-boot:run

A aplicação ficará disponivel na porta 8080.

Rotas disponiveis em : http://localhost:8080/swagger-ui.html#/
Logs: path_aplicacao/src/main/resources/logs/app.log

Foi feita uma api que recebe por meio de um post um arquivo de log de Quake3 e o envia para ser processado a uma fila , enquanto retorna mensagem de sucesso de recebimento do arquivo para o usuario.
O Arquivo é processado e seus resultados são salvos em um banco de dados (mongo).
A mesma api que recebeu o arquivo, por meio de outras rotas, disponibiliza meios (agora consultando o banco de dados) do usuario consultar sobre as estatisticas do arquivo enviado.


