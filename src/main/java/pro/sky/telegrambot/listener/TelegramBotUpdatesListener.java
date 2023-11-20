package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private String startCommand = "/start";
    private final Pattern PATTERN = Pattern.compile(
            "(\\d{2}/\\d{2}/\\d{4} " + // дата
                    "\\d{2}:\\d{2})" +       // время
                    "(.*)");                 // текст напоминалки


    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            Long chatId = update.message().chat().id();
            String messageText = update.message().text();
            String nickName = update.message().chat().firstName();

            if (messageText.equals(startCommand)) {
                startMessage(chatId, nickName);
            }
//            Matcher matcher = PATTERN.matcher(messageText);
//            if (matcher.matches()) {
//                String date = matcher.group(1);
//                String reminder = matcher.group(2);
//            }


        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /* метод, который принимает сообщение из бота
     */
    public void startMessage(Long chatId, String nickName) {
        logger.info("Called method startMessage");
        String messageForUser = "Добро пожаловать в бот-напоминалку!\n"
                + nickName + ", введи запрос в формате:\n"
                + "дд/мм/гггг ЧЧ:ММ напомни о встрече с мамой";
        sendMessage(chatId, messageForUser);
    }

    /* метод, который отправляет сообщение в бот
     */
    public void sendMessage(Long chatId, String message) {
        logger.info("Called method sendMessage");
        SendMessage sendMessage = new SendMessage(chatId, message);
        telegramBot.execute(sendMessage);
    }

}
