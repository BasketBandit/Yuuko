package com.yuuko.core.scheduler.tasks;

import com.yuuko.core.commands.utility.commands.EventCommand;
import com.yuuko.core.scheduler.Task;

public class PruneAbandonedEventsTask implements Task {

    @Override
    public void run() {
        EventCommand.pruneEvents();
    }
}
