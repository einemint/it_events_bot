package MessageHandlers;

import Bot.Bot;
import Commands.CommandHandler;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j2
public class MessageReceiver implements Runnable {
    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;

    private Bot bot;
    private CommandHandler commandHandler;

    public MessageReceiver(Bot bot) {
        this.bot = bot;
        this.commandHandler = new CommandHandler(bot, bot.getBotName());
    }

    @Override
    public void run() {
        while (true) {
            for (Object object = bot.receiveQueue.poll(); object != null; object = bot.receiveQueue.poll()) {
                handle(object);
            }
            try {
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (Exception exception) {
                log.warn(exception.getStackTrace());
                return;
            }
        }
    }

    private void handle(Object object) {
        if (object instanceof Update) {
            Update update = (Update) object;
            Long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            commandHandler.handleMessage(text, chatId, bot);
        } else {
            log.warn("Can't handle object: type of object isn't Update");
        }
    }
}
