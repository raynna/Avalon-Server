package com.rs.java.game.player.queue;

import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;

import java.util.ArrayDeque;
import java.util.Queue;

public final class PlayerActionQueue {

    private final Queue<QueuedAction> queue = new ArrayDeque<>();
    private boolean running = false;

    public void enqueue(Runnable action) {
        queue.add(new QueuedAction(action, 0));
        tryRunNext();
    }

    public void enqueue(int delay, Runnable action) {
        queue.add(new QueuedAction(action, delay));
        tryRunNext();
    }


    public void enqueueDelay(int ticks) {
        queue.add(new QueuedAction(null, ticks));
        tryRunNext();
    }

    private void tryRunNext() {
        if (running)
            return;

        QueuedAction next = queue.poll();
        if (next == null)
            return;

        running = true;

        if (next.delay > 0) {
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    if (next.action != null) {
                        try {
                            next.action.run();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                    running = false;
                    tryRunNext();
                    stop();
                }
            }, next.delay);
            return;
        }

        try {
            if (next.action != null)
                next.action.run();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        running = false;
        tryRunNext();
    }


    public void clear() {
        queue.clear();
        running = false;
    }

    private record QueuedAction(Runnable action, int delay) {
    }
}
