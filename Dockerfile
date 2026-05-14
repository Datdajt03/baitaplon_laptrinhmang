# Sử dụng base image OpenJDK 8
FROM eclipse-temurin:8-jdk

# Cài đặt ant để biên dịch dự án
RUN apt-get update && apt-get install -y ant && rm -rf /var/lib/apt/lists/*

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy toàn bộ mã nguồn vào container
COPY . .

# Cấp quyền và chạy ant để biên dịch
RUN ant compile

# Môi trường IP cho RMI, mặc định là 127.0.0.1 (có thể truyền vào lúc chạy docker run -e RMI_HOSTNAME=IP_CUA_MAY_CHU)
ENV RMI_HOSTNAME="127.0.0.1"

# Mở các cổng cần thiết:
# 8888 cho TCP
# 1099 cho RMI
# 9999 cho UDP
EXPOSE 8888 1099 9999/udp

# Chạy server với java, truyền cấu hình RMI Hostname để các client bên ngoài có thể kết nối RMI
CMD ["sh", "-c", "java -Djava.rmi.server.hostname=$RMI_HOSTNAME -cp build/classes may_chu.chay_may_chu"]
