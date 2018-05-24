package basketbandit.core.commands;


import basketbandit.core.Database;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
public class CommandSetup extends Command {

    CommandSetup() {
        super("setup", "basketbandit.core.modules.ModuleCore", Permission.ADMINISTRATOR);
    }

    public CommandSetup(MessageReceivedEvent e) {
        super("setup", "basketbandit.core.modules.ModuleCore", Permission.ADMINISTRATOR);
        executeCommand(e);
    }

    /**
     * Executes command using MessageReceivedEvent e.
     * @param e; MessageReceivedEvent.
     * @return boolean; if the command executed correctly.
     */
    protected boolean executeCommand(MessageReceivedEvent e) {
        String serverLong = e.getGuild().getIdLong()+"";

        if(new Database().addNewServer(serverLong)) {
            e.getTextChannel().sendMessage("Server setup successful. (You cannot do this again!)").queue();
            return true;
        } else {
            e.getTextChannel().sendMessage("Server setup was unsuccessful. (Are you sure the setup command has not been run before?)").queue();
            return false;
        }
    }

}