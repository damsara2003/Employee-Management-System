package model;

public class Notification {
    private int notificationId;
    private Integer employeeId;
    private String title;
    private String message;
    private String notificationType;
    private boolean read;
    private String createdAt;

    public Notification(int notificationId, Integer employeeId, String title,
                        String message, String notificationType, boolean read,
                        String createdAt) {
        this.notificationId = notificationId;
        this.employeeId = employeeId;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.read = read;
        this.createdAt = createdAt;
    }

    public int getNotificationId() { return notificationId; }
    public Integer getEmployeeId() { return employeeId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getNotificationType() { return notificationType; }
    public boolean isRead() { return read; }
    public String getCreatedAt() { return createdAt; }
}
