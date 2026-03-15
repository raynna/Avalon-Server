package com.rs.java.game.player.queue;

import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.kotlin.game.player.queue.QueueType;

import java.util.ArrayDeque;
import java.util.Queue;

public final class PlayerActionQueue {

    private final Queue<QueuedAction> weakQueue = new ArrayDeque<>();
    private final Queue<QueuedAction> strongQueue = new ArrayDeque<>();

    private boolean running;

    public void enqueue(Runnable action) {
        weakQueue.add(new QueuedAction(action, 0));
        tryRunNext();
    }

    public void enqueue(int delay, Runnable action) {
        weakQueue.add(new QueuedAction(action, delay));
        tryRunNext();
    }

    public void enqueueStrong(Runnable action) {
        strongQueue.add(new QueuedAction(action, 0));
        tryRunNext();
    }

    public void enqueueDelay(int ticks) {
        weakQueue.add(new QueuedAction(null, ticks));
        tryRunNext();
    }

    private void tryRunNext() {
        if (running)
            return;

        QueuedAction next = !strongQueue.isEmpty()
                ? strongQueue.poll()
                : weakQueue.poll();

        if (next == null)
            return;

        running = true;

        if (next.delay > 0) {
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    runAction(next);
                    stop();
                }
            }, next.delay);
        } else {
            runAction(next);
        }
    }

    private void runAction(QueuedAction action) {
        try {
            if (action.action != null)
                action.action.run();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        running = false;
        tryRunNext();
    }

    public void clearWeak() {
        weakQueue.clear();
    }

    public void clearAll() {
        weakQueue.clear();
        strongQueue.clear();
    }

    private record QueuedAction(Runnable action, int delay) {}
}