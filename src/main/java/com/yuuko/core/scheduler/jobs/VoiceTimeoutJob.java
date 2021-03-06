package com.yuuko.core.scheduler.jobs;

import com.yuuko.core.scheduler.Job;
import com.yuuko.core.scheduler.tasks.VoiceTimeoutTask;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.TimeUnit;

public class VoiceTimeoutJob extends Job {
    private final VoiceTimeoutTask voiceTimeoutTask;

    public VoiceTimeoutJob(Guild guild) {
        super(5, 0, TimeUnit.MINUTES);
        voiceTimeoutTask = new VoiceTimeoutTask(guild);
    }

    @Override
    public void run() {
        handleTask(voiceTimeoutTask);
    }
}
