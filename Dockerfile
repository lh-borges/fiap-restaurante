# ----------------------------
# 1) BUILD STAGE (Maven)
# ----------------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

# Diretório de trabalho dentro da imagem
WORKDIR /app

# Copia o POM primeiro para maximizar cache de dependências
COPY pom.xml .

# Baixa dependências antes de copiar o código (melhoria no cache)
#RUN mvn -q -e -DskipTests dependency:go-offline

# Copia o código-fonte
COPY src ./src

# Compila o projeto e gera o jar
RUN mvn clean package -DskipTests


# ----------------------------
# 2) RUNTIME STAGE (Somente JAR)
# ----------------------------
FROM eclipse-temurin:21-jre-alpine

# Cria usuário e grupo não-root
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Diretório de trabalho do runtime
WORKDIR /app

# Copia o jar gerado no stage de build
COPY --from=build /app/target/*.jar /app/app.jar

# Ajusta permissões para o usuário criado
RUN chown -R spring:spring /app

# Executa como usuário não-root
USER spring

# Porta padrão do Spring dentro do container
EXPOSE 8080

# JVM flags simples e seguras para container
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
