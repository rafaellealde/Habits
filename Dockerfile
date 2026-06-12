# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copia o pom.xml primeiro e resolve dependências em camada separada.
# Isso garante que o download das dependências seja cacheado enquanto o pom.xml não mudar.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e gera o fat JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia apenas o fat JAR gerado pelo maven-assembly-plugin
COPY --from=builder /app/target/habitflow-api-1.0.0-SNAPSHOT-jar-with-dependencies.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
