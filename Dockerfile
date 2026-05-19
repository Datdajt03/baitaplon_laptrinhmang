# Su dung base image OpenJDK 8
FROM eclipse-temurin:8-jdk

# Cai dat ant de bien dich du an
RUN apt-get update && apt-get install -y ant && rm -rf /var/lib/apt/lists/*

# Thiet lap thu muc lam viec
WORKDIR /app

# Copy toan bo ma nguon vao container (bao gom thu muc lib chua MongoDB JARs)
COPY . .

# Cap quyen va chay ant de bien dich
RUN ant compile

# Moi truong IP cho RMI
ENV RMI_HOSTNAME="127.0.0.1"

# Chuoi ket noi MongoDB
# host.docker.internal = IP may that cua ban (duoc Docker tu dong map)
# Vi MongoDB cua ban dang chay o port 27020 tren may that, khong cung Docker network
ENV MONGODB_URI="mongodb://emr:123456@host.docker.internal:27020/?authSource=admin"

# Mo cac cong can thiet
EXPOSE 8888 1099 9999/udp

# Chay server voi java, truyen ca RMI Hostname va MongoDB URI
CMD ["sh", "-c", "java -Djava.rmi.server.hostname=$RMI_HOSTNAME -cp build/classes:lib/mongodb-driver-sync-4.11.1.jar:lib/mongodb-driver-core-4.11.1.jar:lib/bson-4.11.1.jar may_chu.chay_may_chu"]

