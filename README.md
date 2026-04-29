# HabitFlow API
O **HabitFlow** é uma API RESTful para gestão e monitorização de hábitos, desenvolvida em Java 17. O projeto foi construído utilizando uma arquitetura limpa, sem o uso de frameworks pesados, focando-se na manipulação direta de HTTP através do HttpServer nativo e persistência com JDBC puro.
## 🚀 Funcionalidades Principais
 * **Gestão de Hábitos (CRUD):** Criação, listagem, atualização e remoção de hábitos.
 * **Cálculo de Streaks:** Lógica integrada para calcular sequências de dias consecutivos de cumprimento de um hábito.
 * **Validação de Domínio:** Garante que hábitos não podem ser criados sem nome ou frequência definida.
 * **Registo de Execuções:** Suporte para marcar hábitos como "Concluídos" (COMPLETE) ou "Saltados" (SKIP).
 * **Consultas Especializadas:** Filtros por frequência (ex: diário, semanal) e identificação de hábitos realizados no dia corrente.
## 🛠️ Tecnologias Utilizadas
 * **Linguagem:** Java 17.
 * **Base de Dados:** PostgreSQL 42.7.3.
 * **Serialização JSON:** Jackson Databind com suporte a Java Time.
 * **Build Tool:** Maven.
## 🏗️ Arquitetura do Projeto
A aplicação segue o padrão de camadas para garantir o desacoplamento:
 1. **Controller:** Gere os pedidos HTTP e a comunicação JSON.
 2. **Service:** Contém a orquestração e as regras de negócio.
 3. **Repository:** Interface e implementação JDBC para persistência de dados.
 4. **Model:** Entidades de domínio com lógica de validação própria.
## ⚙️ Configuração e Execução
### 1. Base de Dados
Certifique-se de que o PostgreSQL está em execução. Utilize o script SQL fornecido para criar a estrutura necessária:
```sql
-- Executar o conteúdo de schema.sql
CREATE DATABASE habitflow;
-- Criar tabelas habits e executions conforme definido no ficheiro

```
### 2. Configuração de Credenciais
As credenciais padrão estão definidas em DatabaseConfig.java:
 * **URL:** jdbc:postgresql://localhost:5432/habitflow
 * **User:** postgres
 * **Password:** admin123
### 3. Compilação e Execução
```bash
mvn clean package
java -jar target/habitflow-api-1.0.0-SNAPSHOT.jar

```
A API ficará disponível em: http://localhost:8080/habits.
## 📑 Endpoints da API
| Método | Endpoint | Descrição |
|---|---|---|
| **GET** | /habits | Lista todos os hábitos (ordenados por data de criação). |
| **POST** | /habits | Cria um novo hábito. |
| **PUT** | /habits/{id} | Atualiza um hábito existente. |
| **DELETE** | /habits/{id} | Remove um hábito pelo ID. |
*Este projeto foi desenvolvido como um scaffolding robusto para sistemas de controlo de hábitos.*
