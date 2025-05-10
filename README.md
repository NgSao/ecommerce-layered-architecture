# 🛒 Ecommerce Layered Architecture

![Java](https://img.shields.io/badge/Java-17+-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen) ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue) ![License](https://img.shields.io/badge/License-MIT-yellow)

**Ecommerce Layered Architecture** là một dự án demo thương mại điện tử được xây dựng theo kiến trúc phân lớp (Layered Architecture) sử dụng **Spring Boot**. Dự án tích hợp các công nghệ hiện đại như **Redis**, **Kafka**, **RabbitMQ**, **OAuth2**, **WebSocket**, và **Spring Security**, hướng đến việc xây dựng một hệ thống mạnh mẽ, bảo mật, và dễ bảo trì.

## 🚀 Tính năng chính

- Xác thực người dùng với **OAuth2** (Google, Facebook).
- Tích hợp **Kafka** và **RabbitMQ** cho hệ thống nhắn tin bất đồng bộ.
- Gửi email thông qua **SMTP**.
- Lưu trữ cache với **Redis**.
- Hỗ trợ tính năng thời gian thực qua **WebSocket**.
- RESTful API được tài liệu hóa với **Swagger UI**.
- Cấu trúc dự án rõ ràng theo mô hình phân lớp: **Controller**, **Service**, **Repository**, **Domain**.
- Hỗ trợ phân trang dữ liệu và validation đầu vào.
- Sử dụng **MySQL** làm hệ quản trị cơ sở dữ liệu chính.

## ⚙️ Công nghệ sử dụng

- **Backend**: Java 17, Spring Boot 3.4.5, Spring Security, Spring Data JPA
- **Messaging**: Spring Kafka, Spring AMQP (RabbitMQ)
- **Caching**: Redis
- **Database**: MySQL
- **Real-time**: WebSocket
- **API Docs**: Swagger (Springdoc OpenAPI)
- **DevOps**: Maven
- **Authentication**: OAuth2 Client (Google, Facebook)
- **Utilities**: Lombok

## 📦 Cấu trúc project

```
src/main/java/com/example/ecommerce/
├── controller/          # Định nghĩa các endpoint RESTful
├── service/             # Chứa logic xử lý chính của ứng dụng
├── repository/          # Giao tiếp với cơ sở dữ liệu
├── domain/              # Định nghĩa các entity
├── event/               # Xử lý các sự kiện Kafka và RabbitMQ
├── config/              # Cấu hình bảo mật, OAuth2, Kafka, RabbitMQ,...
```

## 🛠 Hướng dẫn cài đặt và chạy (Dev)

### 1. Yêu cầu môi trường
- **Java**: 17+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Dịch vụ bên ngoài**: Kafka, RabbitMQ, Redis (có thể chạy bằng Docker)
- **Docker** (tùy chọn, nếu dùng Docker để chạy dịch vụ phụ thuộc)

### 2. Cài đặt và chạy

#### Bước 1: Clone repository
```bash
git clone https://github.com/NgSao/ecommerce-layered-architecture.git
cd ecommerce-layered-architecture
```

#### Bước 2: Cấu hình môi trường
- Cập nhật file `application.yml` hoặc `application.properties` với thông tin:
  - Kết nối **MySQL** (URL, username, password).
  - Cấu hình **Kafka**, **RabbitMQ**, và **Redis**.
  - Thông tin **OAuth2** (client ID, client secret cho Google/Facebook).

#### Bước 3: Khởi chạy dịch vụ phụ thuộc (nếu dùng Docker)
```bash
docker-compose up -d
```

#### Bước 4: Build dự án
```bash
mvn clean install
```

#### Bước 5: Chạy ứng dụng
```bash
mvn spring-boot:run
```

### 3. Truy cập Swagger UI
API được tài liệu hóa tại:
```
http://localhost:8080/swagger-ui.html
```

## 📡 Kiến trúc hệ thống

```plaintext
[Client]
   |
[REST API / WebSocket] <- [Spring Security, OAuth2]
   |
[Controller Layer]
   |
[Service Layer]
   |
[Repository Layer] <- [MySQL]
   |
[Event Layer] <- [Kafka, RabbitMQ]
   |
[Cache] <- [Redis]
```

## 📚 Tài liệu bổ sung
- **Kafka/RabbitMQ**: Xem thư mục `event/` để biết cách xử lý sự kiện.
- **Cấu hình**: Xem thư mục `config/` để biết chi tiết về bảo mật, OAuth2, và các dịch vụ.
- **GitHub Repository**: [NgSao/ecommerce-layered-architecture](https://github.com/NgSao/ecommerce-layered-architecture) (thay bằng link thật nếu có).

## 📫 Thông tin liên hệ
- **Email**: nguyensaovn2019@gmail.com
- **SĐT**: 039 244 5255
- **Địa chỉ**: Thủ Đức, TP. Hồ Chí Minh
- **GitHub**: [NgSao](https://github.com/NgSao)

## 🤝 Đóng góp
Chúng tôi hoan nghênh mọi đóng góp! Vui lòng làm theo các bước sau:
1. Fork repository.
2. Tạo branch mới: `git checkout -b feature/your-feature`.
3. Commit thay đổi: `git commit -m "Add your feature"`.
4. Push lên branch: `git push origin feature/your-feature`.
5. Tạo Pull Request.
