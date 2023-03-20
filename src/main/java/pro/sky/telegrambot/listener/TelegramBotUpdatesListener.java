package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final Pattern NOTIFICATION_TASK_PATTERN = Pattern.compile("([\\d\\\\.:\\s]{16})(\\s)([А-яA-z\\s\\d,.!?:]+)");

    private final TelegramBot telegramBot;

    private final NotificationTaskService notificationTaskService;


    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    private LocalDateTime parse (String dateTime){
        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } catch (DateTimeParseException e){
            return null;
        }
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                logger.info("Processing update: {}", update);
                String text = update.message().text();
                if("/start".equals(text)){
                    SendMessage sendMessage = new SendMessage(update.message().chat().id(), "Привет, это бот-напоминалка!\nНапиши дату и время задачи и бот пришлет тебе сообщение в указанное время.\n\nP.S. Формат сообщения: 20.03.2023 19:30 Сделать домашнюю работу!");
                    sendMessage.parseMode(ParseMode.MarkdownV2);
                    telegramBot.execute(sendMessage);
                } else if (text != null){
                    Matcher matcher = NOTIFICATION_TASK_PATTERN.matcher(text);
                    if (matcher.find()){
                        LocalDateTime dateTime = parse(matcher.group(1));
                        if (Objects.isNull(dateTime)){
                            telegramBot.execute(new SendMessage(update.message().chat().id(), "Неправильный формат даты или времени!\n\nP.S. Формат сообщения: 20.03.2023 19:30 Сделать домашнюю работу!"));
                        } else{
                            String message = matcher.group(3);
                            notificationTaskService.addNotificationTask(dateTime, message, update.message().chat().id());
                            telegramBot.execute(new SendMessage(update.message().chat().id(), "Вы добавили новую задачу. Бот пришлет сообщение в нужное время!"));
                        }
                    }else{
                        telegramBot.execute(new SendMessage(update.message().chat().id(), "Неправильный формат задачи!\n\nP.S. Формат сообщения: 20.03.2023 19:30 Сделать домашнюю работу!"));
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
