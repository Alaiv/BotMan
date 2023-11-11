package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyBot extends TelegramLongPollingBot {
    Logger log = Logger.getLogger(MyBot.class.getName());
    Notifications notifications = new Notifications(this);

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(chatId);
        startDialog(sendMessage, message);
    }

    public synchronized void executeMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    private synchronized void startDialog(SendMessage sendMessage, String message) {
        if (!message.startsWith("/")) return;
        switch (message) {
            case "/start":
                sendMessage.setText("""
                        *Список доступных комманд*:
                        1) /notificate - отправка уведомлений о процессе работ по созданию дорог на основе.
                        """);
                break;
            case "/notificate":
                startRoadNotifications(sendMessage);
                break;
            case "/stopNotificate":
                stopRoadNotifications(sendMessage);
                break;
            default:
                sendMessage.setText("Неизвестная команда. Введите /start для просмотра доступных команд.");

        }
        executeMessage(sendMessage);
    }

    private void startRoadNotifications(SendMessage sendMessage) {
        if (notifications.state.equals(JobStates.PROGRESS)) {
            sendMessage.setText("Уведомления о работах по созданию дорог на основе уже запущены.\n" +
                    "Для остановки текущей работы, введите /stopNotificate");
            return;
        }

        notifications.state = JobStates.PROGRESS;
        Thread myThread = new Thread(() -> {
            try {
                notifications.startSliceNotifications(sendMessage);
            } catch (InterruptedException e) {
                sendMessage.setText("Что-то пошло не так. Перезапустите работу.");
            }
        });
        myThread.start();
    }

    private void stopRoadNotifications(SendMessage sendMessage) {
        if (notifications.state.equals(JobStates.STOPPED)
                || notifications.state.equals(JobStates.STALE)) {
            sendMessage.setText("Уведомления уже остановлены или не были запущены.");
        } else {
            sendMessage.setText("Отправка уведомлений о создании дорог на основе остановлена.");
            notifications.state = JobStates.STOPPED;
        }
    }

    @Override
    public String getBotToken() {
        return "{UR_BOT_TOKEN}";
    }

    @Override
    public String getBotUsername() {
        return "{UR_BOT_NAME}";
    }


}
