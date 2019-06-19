FROM openjdk:8u212-b04-jre-slim
VOLUME /tmp
ADD target/*.jar swagger-kubernetes.jar
RUN sh -c 'touch /swagger-kubernetes.jar'
#ENV KUBERNETES_CONNECT_URL=""
#ENV KUBERNETES_CONNECT_TOKEN=""
#ENV DISCOVERY-NAMESPACE=""
#ENV DISCOVERY-INITIAL-INTERVAL=""
ENV JAVA_OPTS="-Duser.timezone=Asia/Shanghai"
ENV APP_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /swagger-kubernetes.jar $APP_OPTS" ]