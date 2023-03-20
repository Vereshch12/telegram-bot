package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.TasksRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationTaskService {

    private final TasksRepository tasksRepository;

    public NotificationTaskService(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    @Transactional
    public void addNotificationTask(LocalDateTime localDateTime, String message, Long userId){
        NotificationTask notificationTask = new NotificationTask();
        notificationTask.setDateTime(localDateTime);
        notificationTask.setMessage(message);
        notificationTask.setUserId(userId);
        tasksRepository.save(notificationTask);
    }

    public List<NotificationTask> findNotificationsforSend(){
        return tasksRepository.findNotificationTaskByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    @Transactional
    public void deleteTask(NotificationTask notificationTask){
        tasksRepository.delete(notificationTask);
    }


}
