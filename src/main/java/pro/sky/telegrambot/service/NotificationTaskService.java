package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.repository.TasksRepository;

import java.time.LocalDateTime;

@Service
public class NotificationTaskService {

    private final TasksRepository tasksRepository;

    public NotificationTaskService(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    public void addNotificationTask(LocalDateTime localDateTime, String message, Long userId){

    }
}
