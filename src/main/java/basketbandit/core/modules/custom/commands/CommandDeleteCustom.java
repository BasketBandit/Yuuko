package basketbandit.core.modules.custom.commands;

import basketbandit.core.Configuration;
import basketbandit.core.Database;
import basketbandit.core.modules.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.NoSuchElementException;

public class CommandDeleteCustom extends Command {

    public CommandDeleteCustom() {
        super(Configuration.PREFIX + "delcc", "basketbandit.core.modules.custom.ModuleCustom", null);
    }

    public CommandDeleteCustom(MessageReceivedEvent e) {
        super(Configuration.PREFIX + "delcc", "basketbandit.core.modules.custom.ModuleCustom", null);
        executeCommand(e);
    }

    /**
     * Executes command using MessageReceivedEvent e.
     * @param e; MessageReceivedEvent.
     * @return boolean; if the command executed correctly.
     * @throws NoSuchElementException ;
     */
    protected void executeCommand(MessageReceivedEvent e) throws NoSuchElementException {
        String[] commandArray = e.getMessage().getContentRaw().toLowerCase().split("",2);
        String commandName = commandArray[1];
        String serverLong = e.getGuild().getIdLong()+"";
        Database database = new Database();

        if(database.removeCustomCommand(commandName, serverLong)) {
            e.getTextChannel().sendMessage(commandName + " successfully removed!").queue();
        }
        throw new NoSuchElementException();

    }

}
