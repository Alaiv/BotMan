package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class Notifications {
    public JobStates state = JobStates.STALE;
    private final MyBot bot;

    public Notifications(MyBot bot) {
        this.bot = bot;
    }

    public void startSliceNotifications(SendMessage sendMessage) throws InterruptedException {
        sendMessage.setText("Отправка уведомлений запущена. Для остановки работы, введите: /stopNotificate");
        bot.executeMessage(sendMessage);
        DataBase db = new DataBase();
        long startTime = System.currentTimeMillis();

        for (; ; ) {
            //get current time
            long currentTime = System.currentTimeMillis();
            //get jobs count with state = 3 (error)
            int errCount = db.getBadJobStateCount();

            String message = "*Возникла ошибка создания среза!*";
            boolean isTime = false;
            boolean isFinished = false;

            //send notifications every 10 min
            if (currentTime - startTime >= 30000) {
                //get jobs count with state = 0 (waiting)
                int waitingCount = db.getWaitJobStateCount();
                //get jobs count with state = 1 (process)
                int progressCount = db.getInProgressJobStateCount();
                //check if no jobs active
//                isFinished = errCount == 0 && waitingCount == 0 && progressCount == 0;

                String err = errCount > 0 ? message : "Ошибок нет";
                message = isFinished ? "Все работы завершены." : String.format("""
                        - Количество дорог в ожидании: %s
                        - Количество дорог в процессе: %s
                        - %s""", waitingCount, progressCount, err);
                startTime = currentTime;
                isTime = true;
            }

            sendMessage.setText(message);
            if (state.equals(JobStates.STOPPED)) break;
            if (errCount > 0 || isTime) bot.executeMessage(sendMessage);
            //wait for 2 min
            Thread.sleep(30000);
        }
    }

}
