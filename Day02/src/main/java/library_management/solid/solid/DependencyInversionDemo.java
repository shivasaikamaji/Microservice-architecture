package library_management.solid.solid;
public class DependencyInversionDemo {

    interface MessageSender {
        void send(String message);
    }

    static class EmailSender implements MessageSender {
        public void send(String message) {
            System.out.println("Sending email: " + message);
        }
    }

    static class SmsSender implements MessageSender {
        public void send(String message) {
            System.out.println("Sending SMS: " + message);
        }
    }

    static class NotificationService {
        private final MessageSender sender;

        NotificationService(MessageSender sender) {
            this.sender = sender;
        }

        void notifyUser(String message) {
            sender.send(message);
        }
    }

    public static void run() {
        MessageSender email = new EmailSender();
        NotificationService emailService =
                new NotificationService(email);

        emailService.notifyUser("Your order is confirmed.");

        MessageSender sms = new SmsSender();
        NotificationService smsService =
                new NotificationService(sms);

        smsService.notifyUser("Your order has been shipped.");
    }
}