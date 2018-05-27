package basketbandit.core.modules.core.commands;

import basketbandit.core.Configuration;
import basketbandit.core.Database;
import basketbandit.core.modules.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

public class CommandModules extends Command {

    public CommandModules() {
        super("module", "basketbandit.core.modules.core.ModuleCore", null);
    }

    public CommandModules(MessageReceivedEvent e) {
        super("module", "basketbandit.core.modules.core.ModuleCore", null);
        executeCommand(e);
    }

    /**
     * Executes command using MessageReceivedEvent e.
     * @param e; MessageReceivedEvent.
     * @return boolean; if the command executed correctly.
     */
    protected void executeCommand(MessageReceivedEvent e) {
        String serverId = e.getGuild().getId();
        ArrayList<String> enabled = new ArrayList<>();
        ArrayList<String> disabled = new ArrayList<>();
        ResultSet rs;

        try {
            rs = new Database().getModuleSettings(serverId);
            rs.next();

            for(int i = 4; i < 12; i++) {
                ResultSetMetaData meta = rs.getMetaData();
                if(rs.getBoolean(i)) {
                    enabled.add(meta.getColumnName(i));
                } else {
                    disabled.add(meta.getColumnName(i));
                }
            }

            EmbedBuilder commandModules = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setAuthor("Hey " + e.getAuthor().getName() + ",",null,e.getAuthor().getAvatarUrl())
                    .setTitle("Below are the list of bot module!")
                    .setDescription("Each module can be toggled on or off by using the " + Configuration.PREFIX + "module <name> command.")
                    .addField("Enabled Modules", enabled.toString().replace(",","\n").replaceAll("[\\[\\] ]", "").toLowerCase(), false)
                    .addField("Disabled Modules", disabled.toString().replace(",","\n").replaceAll("[\\[\\] ]", "").toLowerCase(), false)
                    .setFooter("Version: " + Configuration.VERSION, e.getGuild().getMemberById(Configuration.BOT_ID).getUser().getAvatarUrl());

            e.getTextChannel().sendMessage(commandModules.build()).queue();
        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }

}
