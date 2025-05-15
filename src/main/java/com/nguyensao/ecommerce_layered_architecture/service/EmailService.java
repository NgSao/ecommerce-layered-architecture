package com.nguyensao.ecommerce_layered_architecture.service;

import java.time.LocalDate;
import java.util.Random;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nguyensao.ecommerce_layered_architecture.constant.EmailConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;
import com.nguyensao.ecommerce_layered_architecture.dto.OrderItemDto;
import com.nguyensao.ecommerce_layered_architecture.enums.OrderStatus;
import com.nguyensao.ecommerce_layered_architecture.utils.CurrencyUtils;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    @Async
    public void sendVerificationEmail(String fullName, String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Mã xác thực");

            long otpTokenExpirationNew = EmailConstant.EXPIRATION_OTP / 60;

            String htmlContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f9f9f9; line-height: 1.6; margin: 0; padding: 20px; }"
                    +
                    ".header { background-color: #d70018; padding: 10px; text-align: center; }" +
                    ".container { max-width: 800px; margin: auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 20px; }"
                    +
                    ".fotterne { background-color: #d70018; padding: 10px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div style='max-width:800px;margin: auto;'>" +
                    "<div class='header'>" +
                    "<h1 style='color: #FFF'>MinhTuan <span style='color:#000'>Mobile</span></h1>" +
                    "</div>" +
                    "<div class='container'>" +
                    "<p>Kính chào " + fullName
                    + ",<br>MinhTuan Mobile gửi đến quý khách mã xác thực tài khoản.</p>" +
                    "<div style='margin-bottom: 30px;'>" +
                    "<div style='border-bottom: 3px solid #d70018;'>" +
                    "<h3 style='color: #d70018;'>MÃ XÁC THỰC</h3>" +
                    "</div>" +
                    "<p style='font-size: 16px;'>Mã xác thực của bạn là: <strong style='font-size: 20px; color: #d70018;'>"
                    + code + "</strong></p>" +
                    "<p style='font-size: 14px;'>Mã này sẽ hết hạn sau " + otpTokenExpirationNew
                    + " phút. Vui lòng nhập mã trong thời gian quy định.</p>" +
                    "<p style='font-size: 12px; color: #888;'>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>"
                    +
                    "</div>" +
                    "<div style='text-align: center; margin-top: 40px'>" +
                    "<p>Chúc bạn luôn có những trải nghiệm tuyệt vời khi sử dụng dịch vụ tại MinhTuan Mobile.</p>" +
                    "<p>Tổng đài hỗ trợ miễn phí: <span style='color:#d70018;'>0392445255</span></p>" +
                    "<p>MinhTuan Mobile cảm ơn quý khách.</p>" +
                    "</div>" +
                    "</div>" +
                    "<div class='fotterne'></div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gửi email xác thực: " + e.getMessage());
        }
    }

    @Async
    public void sendVerificationPassword(String fullName, String toEmail, String newPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Cập nhật mật khẩu mới");
            String htmlContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f9f9f9; line-height: 1.6; margin: 0; padding: 20px; }"
                    +
                    ".header { background-color: #d70018; padding: 10px; text-align: center; }" +
                    ".container { max-width: 800px; margin: auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 20px; }"
                    +
                    ".fotterne { background-color: #d70018; padding: 10px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div style='max-width:800px;margin: auto;'>" +
                    "<div class='header'>" +
                    "<h1 style='color: #FFF'>MinhTuan <span style='color:#000'>Mobile</span></h1>" +
                    "</div>" +
                    "<div class='container'>" +
                    "<p>Kính chào " + fullName
                    + ",<br>MinhTuan Mobile gửi đến quý kháchmật khẩu mới để đăng nhập tài khoản.</p>" +
                    "<div style='margin-bottom: 30px;'>" +
                    "<div style='border-bottom: 3px solid #d70018;'>" +
                    "<h3 style='color: #d70018;'>MẬT KHẨU MỚI</h3>" +
                    "</div>" +
                    "<p style='font-size: 16px;'>Mật khẩu mới của bạn là: <strong style='font-size: 20px; color: #d70018;'>"
                    + newPassword + "</strong></p>" +
                    "<p style='font-size: 14px;'>Vui lòng đổi mật khẩu mới để đảm bảo an toàn hơn.</p>" +
                    "<p style='font-size: 12px; color: #888;'>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>"
                    +
                    "</div>" +
                    "<div style='text-align: center; margin-top: 40px'>" +
                    "<p>Chúc bạn luôn có những trải nghiệm tuyệt vời khi sử dụng dịch vụ tại MinhTuan Mobile.</p>" +
                    "<p>Tổng đài hỗ trợ miễn phí: <span style='color:#d70018;'>0392445255</span></p>" +
                    "<p>MinhTuan Mobile cảm ơn quý khách.</p>" +
                    "</div>" +
                    "</div>" +
                    "<div class='fotterne'></div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gửi email mật khẩu mới: " + e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetConfirmation(String fullName, String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Xác nhận đặt lại mật khẩu thành công");

            String htmlContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f9f9f9; line-height: 1.6; margin: 0; padding: 20px; }"
                    +
                    ".header { background-color: #d70018; padding: 10px; text-align: center; }" +
                    ".container { max-width: 800px; margin: auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 20px; }"
                    +
                    ".fotterne { background-color: #d70018; padding: 10px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div style='max-width:800px;margin: auto;'>" +
                    "<div class='header'>" +
                    "<h1 style='color: #FFF'>MinhTuan <span style='color:#000'>Mobile</span></h1>" +
                    "</div>" +
                    "<div class='container'>" +
                    "<p>Kính chào " + fullName
                    + ",<br>MinhTuan Mobile thông báo quý khách đã đặt lại mật khẩu thành công.</p>" +
                    "<div style='margin-bottom: 30px;'>" +
                    "<div style='border-bottom: 3px solid #d70018;'>" +
                    "<h3 style='color: #d70018;'>XÁC NHẬN ĐẶT LẠI MẬT KHẨU</h3>" +
                    "</div>" +
                    "<p style='font-size: 16px;'>Bạn đã đặt lại mật khẩu thành công!</p>" +
                    "<p style='font-size: 14px;'>Vui lòng đăng nhập bằng mật khẩu mới và thay đổi mật khẩu nếu cần.</p>"
                    +
                    "<p style='font-size: 14px;'>Nếu bạn không thực hiện thao tác này, hãy liên hệ ngay với bộ phận hỗ trợ qua số <span style='color:#d70018;'>0392445255</span>.</p>"
                    +
                    "<p style='font-size: 12px; color: #888;'>Email này được gửi tự động, vui lòng không trả lời.</p>" +
                    "</div>" +
                    "<div style='text-align: center; margin-top: 40px'>" +
                    "<p>Chúc bạn luôn có những trải nghiệm tuyệt vời khi sử dụng dịch vụ tại MinhTuan Mobile.</p>" +
                    "<p>Tổng đài hỗ trợ miễn phí: <span style='color:#d70018;'>0392445255</span></p>" +
                    "<p>MinhTuan Mobile cảm ơn quý khách.</p>" +
                    "</div>" +
                    "</div>" +
                    "<div class='fotterne'></div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gửi email xác nhận đặt lại mật khẩu: " + e.getMessage());
        }
    }

    @Async
    public void sendOrderConfirmation(String email, OrderStatus orderStatus, OrderDto orderRequest, Boolean flag) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setFrom("ecommershopapp@gmail.com");
            String subject;
            switch (orderStatus) {
                case PENDING:
                    subject = "Cảm ơn bạn đã đặt đơn hàng #" + orderRequest.getOrderCode();
                    break;
                case CONFIRMED:
                    subject = "Chúng tôi đã xác nhận đơn hàng của bạn. Cảm ơn bạn đã tin tưởng Minh Tuấn Mobile!";
                    break;
                case CANCELLED:
                    if (flag) {
                        subject = "Đơn hàng #" + orderRequest.getOrderCode() + " đã bị hủy bởi cửa hàng.";
                    } else {
                        subject = "Đơn hàng #" + orderRequest.getOrderCode() + " đã bị hủy bởi bạn.";
                    }
                    break;
                default:
                    subject = "Cập nhật đơn hàng #" + orderRequest.getOrderCode();
                    break;
            }
            helper.setSubject(subject);
            StringBuilder orderDetailsHtml = new StringBuilder();
            orderDetailsHtml.append("<h3 style='color: #d70018;'>THÔNG TIN SẢN PHẨM</h3>");
            int totalQuantity = 0;
            for (OrderItemDto detail : orderRequest.getItems()) {
                int quantity = detail.getQuantity();
                totalQuantity += quantity;
                orderDetailsHtml.append(
                        "<div class='order-item' style='display: flex; border-bottom: 1px solid #ccc; padding: 10px 0;'>")
                        .append("<img src='")
                        .append(detail.getImageUrl())
                        .append("' alt='Sản phẩm' style='width: 100px; height: 100px; margin-right: 15px;'/>")
                        .append("<div class='item-details' style='flex: 1;'>")
                        .append("<p style='font-weight: bold;'>")
                        .append(detail.getName())
                        .append("</p>");

                if (detail.getColor() != null && !detail.getColor().isEmpty()) {
                    orderDetailsHtml.append("<p>Màu sắc: <span>")
                            .append(detail.getColor())
                            .append("</span></p>");
                }

                if (detail.getStorage() != null && !detail.getStorage().isEmpty()) {
                    orderDetailsHtml.append("<p>Dung lượng: <span>")
                            .append(detail.getStorage())
                            .append("</span></p>");
                }

                // Số lượng và giá
                orderDetailsHtml.append("<p>Số lượng: <span>")
                        .append(detail.getQuantity())
                        .append("</span></p>")
                        .append("<p>Giá: <span>")
                        .append(CurrencyUtils.formatAmount(detail.getPrice()))
                        .append("</span></p>")
                        .append("</div></div>");
            }
            LocalDate estimatedDeliveryDate = LocalDate.now().plusDays(3);
            double shippingFee = orderRequest.getShipping().getFee();
            String emailContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f9f9f9; line-height: 1.6; margin: 0; padding: 20px; }"
                    +
                    ".header { background-color: #d70018; padding: 10px; text-align: center; }" +
                    ".container { max-width: 800px; margin: auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 20px; }"
                    +
                    ".order-info { margin-bottom: 20px; }" +
                    ".order-title { display: flex; justify-content: space-between; align-items: center; }" +
                    ".order-status { color: #198754; margin-left: 40px; }" +
                    ".order-date { margin: 10px 0; color: #555; }" +
                    ".order-details { margin-top: 20px; }" +
                    ".order-item { display: flex; border-bottom: 1px solid #ccc; padding: 10px 0; }" +
                    ".product-image { width: 100px; height: 100px; margin-right: 15px; }" +
                    ".item-details { flex: 1; }" +
                    ".item-name { font-weight: bold; }" +
                    ".order-footer { margin-top: 20px; }" +
                    ".footer-item { display: flex; justify-content: space-between; padding: 5px 0; }" +
                    ".total-order { color: #d70018; font-weight: bold; }" +
                    ".fotterne { background-color: #d70018;  padding: 10px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div style='max-width:800px;margin: auto;'>" +
                    "<div class='header'>" +
                    "<h1 style='color: #FFF'>SN <span style='color:#000'>Mobile</span></h1>" +
                    "</div>" +
                    "<div class='container'>" +
                    "<p>Kính chào quý khách,<br>SN Mobile gửi đến quý khách hóa đơn điện tử cho đơn hàng " +
                    orderRequest.getOrderCode() +
                    ". Quý khách vui lòng kiểm tra hóa đơn VAT bằng cách xem và tải file theo thông tin chi tiết dưới đây.</p>";

            emailContent += "<div style='margin-bottom: 30px;'>" +
                    "<div style='border-bottom: 3px solid #d70018;'>" +
                    "<h3 style='color: #d70018;'>THÔNG TIN KHÁCH HÀNG</h3>" +
                    "</div>" +
                    "<p>Người nhận: <span>" + orderRequest.getShipping().getFullName() + "</span></p>" +
                    "<p>Số điện thoại: <span>" + orderRequest.getShipping().getPhone() + "</span></p>" +
                    "<p>Email: <span>" + email + "</span></p>";
            emailContent += "<p>Địa chỉ nhận hàng: <span>" + orderRequest.getShipping().getAddressDetail()
                    + "</span></p>";

            emailContent += "</div>";

            emailContent += "<div class='order-info'>" +
                    "<div style='border-bottom: 3px solid #d70018;'>" +
                    "<h3 style='color: #d70018;'>THÔNG TIN ĐƠN HÀNG " + orderRequest.getOrderCode() + "</h3>" +
                    "</div>" +
                    "<div>" +
                    "<div class='order-title'>" +
                    "<p>Ngày đặt hàng: <span>" + LocalDate.now() + "</span></p>" +
                    "<p class='order-status'>" + getVietnameseOrderStatus(orderStatus) + "</p>" +
                    "</div>" +
                    "<p>Phương thức thanh toán: <span>"
                    + getVietnamesePaymentMethod(orderRequest.getPayment().getMethod()) + "</span></p>" +
                    "<p>Trạng thái thanh toán: <span>" + orderRequest.getPayment().getStatus() + "</span></p>" +
                    "<p>Phương thức vận chuyển: <span>" + orderRequest.getShipping().getMethod() + "</span></p>" +

                    "<p class='estimated-delivery'>Dự kiến giao: <span>" + estimatedDeliveryDate + "</span></p>" +
                    "</div>" +
                    "<div class='order-details'>" +
                    orderDetailsHtml.toString() +
                    "</div>" +
                    "<div class='order-footer'>" +
                    "<div class='footer-item'>" +
                    "<p>Tổng đơn hàng:</p>" +
                    "<p>" + CurrencyUtils.formatAmount(orderRequest.getTotal()) + "</p>" +
                    "</div>" +
                    "<div class='footer-item'>" +
                    "<p>Số lượng:</p>" +
                    "<p>" + totalQuantity + "</p>" +
                    "</div>" +
                    "<div class='footer-item'>" +
                    "<p>Phí vận chuyển:</p>" +
                    "<p >" + CurrencyUtils.formatAmount((shippingFee)) + "</p>" +
                    "</div>" +
                    (orderRequest.getDiscount() > 0 ? "<div class='footer-item'>" +
                            "<p>Giảm giá" +
                            (orderRequest.getPromoCode() != null ? " (" + orderRequest.getPromoCode() + ")" : "") +
                            ":</p>" +
                            "<p>-" + CurrencyUtils.formatAmount(orderRequest.getDiscount()) + "</p>" +
                            "</div>"
                            : "")
                    +
                    "<div class='footer-item'>" +
                    "<p>Tổng tiền đơn hàng:</p>" +
                    "<p class='total-order'>" + CurrencyUtils.formatAmount(orderRequest.getTotal())
                    + "</p>" +
                    "</div>" +
                    "</div>" +
                    "</div>" +
                    "<div style='text-align: center; margin-top: 40px'>" +
                    "<p>Chúc bạn luôn có những trải nghiệm tuyệt vời khi mua sắm tại SN Mobile.</p>" +
                    "<p>Tổng đài hỗ trợ miễn phí: <span style='color:#d70018;'>0392445255</span></p>" +
                    "<p>SN Mobile cảm ơn quý khách.</p>" +
                    "</div>" +
                    "</div>" +
                    " <div class='fotterne'></div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(emailContent, true);
            mailSender.send(message);
        } catch (Exception e) {
        }
    }

    @Async
    public void sendShippingUpdate(String email, OrderStatus orderStatus, OrderDto orderRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setFrom("ecommershopapp@gmail.com");
            String subject;
            switch (orderStatus) {
                case SHIPPED:
                    subject = "Đơn hàng #" + orderRequest.getOrderCode() + " đang được giao.";
                    break;
                case DELIVERED:
                    subject = "Đơn hàng #" + orderRequest.getOrderCode() + " đã giao thành công.";
                    break;
                default:
                    subject = "Cập nhật đơn hàng #" + orderRequest.getOrderCode();
                    break;
            }
            helper.setSubject(subject);
            int totalQuantity = 0;
            for (OrderItemDto detail : orderRequest.getItems()) {
                totalQuantity += detail.getQuantity();
            }

            String trackingNumber = generateRandomTrackingNumber();
            LocalDate estimatedDeliveryDate = LocalDate.now().plusDays(3);
            String emailContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f9f9f9; line-height: 1.6; margin: 0; padding: 20px; }"
                    +
                    ".header { background-color: #d70018; padding: 10px; text-align: center; }" +
                    ".container { max-width: 800px; margin: auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 20px; }"
                    +
                    ".order-info { margin-bottom: 20px; }" +
                    ".order-title { display: flex; justify-content: space-between; align-items: center; }" +
                    ".order-status { color: #198754; margin-left: 40px; }" +
                    ".shipping-details { margin-top: 20px; }" +
                    ".order-footer { margin-top: 20px; }" +
                    ".footer-item { display: flex; justify-content: space-between; padding: 5px 0; }" +
                    ".total-order { color: #d70018; font-weight: bold; }" +
                    ".fotterne { background-color: #d70018; padding: 10px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div style='max-width:800px;margin: auto;'>" +
                    "<div class='header'>" +
                    "<h1 style='color: #FFF'>MT <span style='color:#000'>Mobile</span></h1>" +
                    "</div>" +
                    "<div class='container'>" +
                    "<p>Kính chào quý khách,<br>MT Mobile thông báo cập nhật trạng thái vận chuyển cho đơn hàng " +
                    orderRequest.getOrderCode() + ".</p>" +
                    "<div style='margin-bottom: 30px;'>" +
                    "<div style='border-bottom: 3px solid #d70018;'>" +
                    "<h3 style='color: #d70018;'>THÔNG TIN VẬN CHUYỂN</h3>" +
                    "</div>" +
                    "<p>Trạng thái: <span>" + getVietnameseOrderStatus(orderStatus) + "</span></p>" +
                    "<p>Đơn vị vận chuyển: <span>" + orderRequest.getShipping().getMethod() + "</span></p>" +
                    "<p>Mã vận đơn: <span>" + trackingNumber + "</span></p>" +
                    "<p>Dự kiến giao hàng: <span>" + estimatedDeliveryDate + "</span></p>" +
                    "<p>Địa chỉ nhận hàng: <span>" + orderRequest.getShipping().getAddressDetail() + "</span></p>" +
                    "</div>" +
                    "<div style='margin-bottom: 30px;'>" +
                    "<div style='border-bottom: 3px solid #d70018;'>" +
                    "<h3 style='color: #d70018;'>THÔNG TIN KHÁCH HÀNG</h3>" +
                    "</div>" +
                    "<p>Người nhận: <span>" + orderRequest.getShipping().getFullName() + "</span></p>" +
                    "<p>Số điện thoại: <span>" + orderRequest.getShipping().getPhone() + "</span></p>" +
                    "<p>Email: <span>" + email + "</span></p>" +
                    "</div>" +
                    "<div class='order-footer'>" +
                    "<div class='footer-item'>" +
                    "<p>Số lượng:</p>" +
                    "<p>" + totalQuantity + "</p>" +
                    "</div>" +
                    "<div class='footer-item'>" +
                    "<p>Tổng tiền đơn hàng:</p>" +
                    "<p class='total-order'>" + CurrencyUtils.formatAmount(orderRequest.getTotal()) + "</p>" +
                    "</div>" +
                    "</div>" +
                    "<div style='text-align: center; margin-top: 40px'>" +
                    "<p>Để theo dõi trạng thái vận chuyển, quý khách vui lòng sử dụng mã vận đơn trên hệ thống của " +
                    orderRequest.getShipping().getMethod() + ".</p>" +
                    "<p>Chúc bạn luôn có những trải nghiệm tuyệt vời khi mua sắm tại MT Mobile.</p>" +
                    "<p>Tổng đài hỗ trợ miễn phí: <span style='color:#d70018;'>0392445255</span></p>" +
                    "<p>MT Mobile cảm ơn quý khách.</p>" +
                    "</div>" +
                    "</div>" +
                    "<div class='fotterne'></div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            helper.setText(emailContent, true);
            mailSender.send(message);
        } catch (Exception e) {
        }
    }

    private String getVietnamesePaymentMethod(String method) {
        if (method == null)
            return "Không xác định";
        switch (method) {
            case "cod":
                return "Thanh toán khi nhận hàng";
            case "bank":
                return "Chuyển khoản ngân hàng";
            case "vnpay":
                return "Thanh toán qua VNPAY";

            default:
                return "Khác";
        }
    }

    private String getVietnameseOrderStatus(OrderStatus status) {
        if (status == null) {
            return "Không xác định";
        }
        switch (status) {
            case PENDING:
                return "Đang chờ xử lý";
            case CONFIRMED:
                return "Đã xác nhận";
            case SHIPPED:
                return "Đang giao hàng";
            case DELIVERED:
                return "Đã giao hàng";
            case CANCELLED:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }

    private String generateRandomTrackingNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000);
        return "SN-" + String.format("%06d", randomNumber);
    }

}