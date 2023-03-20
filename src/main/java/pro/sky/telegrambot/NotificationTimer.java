package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.util.concurrent.TimeUnit;

@Component
public class NotificationTimer {

    private final NotificationTaskService notificationTaskService;

    private final TelegramBot telegramBot;

    public NotificationTimer(NotificationTaskService notificationTaskService, TelegramBot telegramBot) {
        this.notificationTaskService = notificationTaskService;
        this.telegramBot = telegramBot;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void checkNotificatiosNyTime(){
        notificationTaskService.findNotificationsforSend().forEach(notificationTask -> {
            telegramBot.execute(new SendMessage(notificationTask.getUserId(), "НАПОМИНАНИЕ:\n\n" + notificationTask.getMessage()));
            notificationTaskService.deleteTask(notificationTask );
        });
    }
}
