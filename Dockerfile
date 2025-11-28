
# ----------------------------
# 1️⃣ BUILD STAGE (Maven)
# ----------------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copia apenas os arquivos de configuração primeiro (melhor cache)
COPY pom.xml .
COPY src ./src

# Compila o projeto e gera o jar
RUN mvn clean package -DskipTests

# ----------------------------
# 2️⃣ RUNTIME STAGE (Somente JAR)
# ----------------------------
FROM eclipse-temurin:21-jdk-alpine

# Cria um usuário não-root (melhor prática de segurança)
RUN addgroup --system spring && adduser --system spring --ingroup spring

WORKDIR /app

# Copia o jar da imagem anterior
COPY --from=build /app/target/*.jar app.jar

# Define permissões para o novo usuário
RUN chown -R spring:spring /app

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

