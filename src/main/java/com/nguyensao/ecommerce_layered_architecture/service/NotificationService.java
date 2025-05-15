
package com.nguyensao.ecommerce_layered_architecture.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.nguyensao.ecommerce_layered_architecture.constant.UserConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.NotificationDto;
import com.nguyensao.ecommerce_layered_architecture.enums.NotificationEnum;
import com.nguyensao.ecommerce_layered_architecture.enums.RoleAuthorities;
import com.nguyensao.ecommerce_layered_architecture.event.domain.NotificationEvent;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.model.Notification;
import com.nguyensao.ecommerce_layered_architecture.repository.NotificationRepository;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(RedisTemplate<String, String> redisTemplate,
            NotificationRepository notificationRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Notification createNotification(NotificationDto notificationDTO) {
        Notification notification = new Notification();
        notification.setUserId(notificationDTO.getUserId());
        notification.setType(notificationDTO.getType() != null ? notificationDTO.getType() : NotificationEnum.USER);
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setDate(notificationDTO.getDate() != null ? notificationDTO.getDate() : Instant.now());
        notification.setRead(notificationDTO.isRead());
        notification.setData(notificationDTO.getData());

        Notification savedNotification = notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/notifications", savedNotification);

        return savedNotification;
    }

    public void createOrderNotification(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setFlagId(event.getFlagId());
        notification.setType(NotificationEnum.ORDER);
        notification.setDate(Instant.now());
        notification.setRead(false);
        notification.setData("{\"orderId\": \"" + event.getData() + "\"}");
        switch (event.getFlag()) {
            case "PENDING":
                notification.setTitle("Bạn có đơn hàng mới");
                notification.setMessage(
                        "Bạn vừa nhận được đơn hàng mới #" + event.getData() + ". Vui lòng xử lý đơn hàng.");
                notification.setRole(RoleAuthorities.STAFF);
                break;
            case "CONFIRMED":
                notification.setTitle("Đơn hàng #" + event.getData() + " đã được xác nhận");
                notification.setMessage(
                        "Chúng tôi đã xác nhận đơn hàng của bạn. Cảm ơn bạn đã tin tưởng Minh Tuấn Mobile!");
                notification.setRole(RoleAuthorities.CUSTOMER);
                break;
            case "SHIPPED":
                notification.setTitle("Đơn hàng #" + event.getData() + " đang được giao");
                notification.setMessage("Đơn hàng của bạn đang được giao. Vui lòng chú ý điện thoại để nhận hàng.");
                notification.setRole(RoleAuthorities.CUSTOMER);
                break;
            case "DELIVERED":
                notification.setTitle("Đơn hàng #" + event.getData() + " đã giao thành công");
                notification.setMessage(
                        "Đơn hàng của bạn đã được giao thành công. Cảm ơn bạn đã mua sắm tại Minh Tuấn Mobile!");
                notification.setRole(RoleAuthorities.CUSTOMER);
                break;
            case "CANCELLED":
                if ("CUSTOMER".equals(event.getFlagData())) {
                    notification.setTitle("Người dùng đã hủy đơn hàng #" + event.getData());
                    notification.setMessage("Khách hàng đã chủ động hủy đơn hàng #" + event.getData() + ".");
                    notification.setRole(RoleAuthorities.STAFF);
                } else {
                    notification.setTitle("Đơn hàng #" + event.getData() + " đã bị hủy");
                    notification.setMessage(
                            "Đơn hàng của bạn đã bị hủy. Vui lòng liên hệ với chúng tôi nếu cần hỗ trợ thêm.");
                    notification.setRole(RoleAuthorities.CUSTOMER);
                }
                break;
            default:
                return;
        }
        Notification savedNotification = notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/notifications", savedNotification);
        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getUserId(), savedNotification);
    }

    public void createCustomerNotification(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setType(NotificationEnum.USER);
        notification.setDate(Instant.now());
        notification.setRead(false);
        notification.setData("{}");
        notification.setRole(RoleAuthorities.CUSTOMER);

        switch (event.getFlag()) {
            case "REGISTER":
                notification.setTitle("Chào mừng bạn đến với Minh Tuấn Mobile!");
                notification.setMessage("Tài khoản của bạn đã được tạo thành công. Hãy khám phá ngay!");
                break;
            case "LOGIN":
                notification.setTitle("Đăng nhập thành công");
                notification.setMessage("Bạn đã đăng nhập vào tài khoản của mình.");
                break;
            case "FORGOT_PASSWORD":
                notification.setTitle("Yêu cầu đặt lại mật khẩu");
                notification.setMessage("Chúng tôi đã gửi hướng dẫn đặt lại mật khẩu đến email của bạn.");
                break;
            case "RESET_PASSWORD":
                notification.setTitle("Đặt lại mật khẩu thành công");
                notification.setMessage("Mật khẩu của bạn đã được đặt lại thành công. Hãy bảo mật tài khoản của bạn.");
                break;
            case "CHANGE_PASSWORD":
                notification.setTitle("Thay đổi mật khẩu thành công");
                notification.setMessage("Bạn đã thay đổi mật khẩu thành công.");
                break;
            default:
                return;
        }
        Notification savedNotification = notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications", savedNotification);
        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getUserId(), savedNotification);
    }

    public List<Notification> getNotificationsByToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<NotificationEnum> publicTypes = List.of(NotificationEnum.PROMOTION, NotificationEnum.NEWS);

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return notificationRepository.findByTypeInOrderByDateDesc(publicTypes);
        }

        Jwt jwt = (Jwt) auth.getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);

        return notificationRepository.findByUserIdOrPublicTypesOrderByDateDesc(uuid, publicTypes);
    }

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getUnreadNotificationsByUserId(String userId) {
        return notificationRepository.findByUserIdAndRead(userId, false);
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException("Notification not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);

        if (!notification.getUserId().equals(uuid)) {
            throw new AppException("You can only mark your own notifications as read");
        }
        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return updatedNotification;
    }

    public void markAllAsReadForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);

        List<Notification> notifications = notificationRepository.findByUserIdAndRead(uuid, false);

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }

    public List<Notification> getLatestNotifications(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return notificationRepository.findAllByOrderByIdDesc(pageable).getContent();
    }

    public void deleteNotification(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException("Notification not found"));

        if (!notification.getUserId().equals(uuid)) {
            throw new AppException("Bạn không có quyền xóa thông báo này");
        }

        notificationRepository.delete(notification);
    }

}