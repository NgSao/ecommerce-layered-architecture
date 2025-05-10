# Ecommerce Layered Architecture

## 🛒 Giới thiệu

Đây là một dự án demo thương mại điện tử được xây dựng theo kiến trúc phân lớp sử dụng Spring Boot. Dự án tích hợp nhiều công nghệ hiện đại như Redis, Kafka, RabbitMQ, OAuth2, WebSocket, và Spring Security.

## 🚀 Tính năng chính

- Xác thực người dùng với OAuth2 (Google, Facebook)
- Tích hợp Kafka và RabbitMQ cho hệ thống nhắn tin bất đồng bộ
- Gửi email với SMTP
- Lưu trữ cache bằng Redis
- WebSocket cho tính năng thời gian thực (real-time)
- RESTful API với Swagger UI
- Cấu trúc dự án rõ ràng theo mô hình phân lớp: controller, service, repository, domain
- Hỗ trợ phân trang dữ liệu và validation đầu vào
- Tích hợp MySQL làm hệ quản trị cơ sở dữ liệu chính

## ⚙️ Công nghệ sử dụng

- Java 17
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- Spring Kafka
- Spring AMQP (RabbitMQ)
- Redis
- WebSocket
- OAuth2 Client (Google, Facebook)
- Swagger (Springdoc OpenAPI)
- Lombok
- MySQL
- Maven

## 📦 Cấu trúc project

- `controller`: định nghĩa các endpoint RESTful
- `service`: chứa logic xử lý chính của ứng dụng
- `repository`: giao tiếp với CSDL
- `domain`: định nghĩa các entity
- `event`: xử lý các sự kiện Kafka và RabbitMQ
- `config`: chứa các file cấu hình bảo mật, OAuth2, Kafka, RabbitMQ,...

## 📫 Thông tin liên hệ

- 📧 Email: nguyensaovn2019@gmail.com  
- 📱 SĐT: 039 244 5255  
- 🏢 Địa chỉ: Thủ Đức, TP. Hồ Chí Minh  

---

