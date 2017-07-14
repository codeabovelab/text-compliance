package com.codeabovelab.tpc.util

import com.google.common.util.concurrent.ThreadFactoryBuilder

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Common utilities for {@link java.util.concurrent.Executor }
 */
object ExecutorUtils {
     val DIRECT: Executor = Executor { command -> command.run() }

    /**
     * Executor deffer tasks into internal storage and run its only at {@link #flush()} .
     */
    class DeferredExecutor : Executor {
        private val lock = Any()
        @Volatile
        private var queue = CopyOnWriteArrayList<Runnable>()

        @Override
        override fun execute(command: Runnable) {
            synchronized(lock) {
                queue.add(command)
            }
        }

        /**
         * Execute all scheduled tasks.
         */
        fun flush() {
            val old  = synchronized (lock) {
                val tmp = this.queue
                this.queue = CopyOnWriteArrayList<Runnable>()
                tmp
            }
            for(runnable in old) {
                runnable.run()
            }
        }
    }

    /**
     * @see DeferredExecutor
     * @return new instance of {@link DeferredExecutor }
     */
    fun deferred(): DeferredExecutor {
        return DeferredExecutor()
    }

    private class ThreadFactoryImpl(
            name: String,
            private val daemon: Boolean,
            private val uncaughtExceptionHandler: Thread.UncaughtExceptionHandler?
    ): ThreadFactory {
        private val group: ThreadGroup
        private val count = AtomicInteger(1)
        private val prefix: String

        init {
            val sm = System.getSecurityManager()
            group = if(sm != null)  sm.threadGroup else Thread.currentThread().threadGroup
            this.prefix = if(name.endsWith("-")) name else (name + "-")
        }

        override fun newThread(r: Runnable): Thread {
            val thread = Thread(group, r, prefix + count.getAndIncrement())
            thread.isDaemon = daemon
            thread.priority = Thread.NORM_PRIORITY
            if(this.uncaughtExceptionHandler != null) {
                thread.uncaughtExceptionHandler = this.uncaughtExceptionHandler
            }
            return thread
        }
    }

    class ExecutorBuilder {
        var name = "executor"
        var daemon = true
        var exceptionHandler: Thread.UncaughtExceptionHandler = Throwables.uncaughtHandler()
        var rejectedHandler: RejectedExecutionHandler = ThreadPoolExecutor.AbortPolicy()
        var maxSize = 5
        var coreSize = 2
        var keepAlive = 30L
        var queueSize = 10

        /**
         * Name of thread, without thread number pattern.
         * @param name name
         * @return this
         */
        fun name(name: String) = apply {
            this.name = name
        }

        /**
         * Daemon flag.
         * @param daemon default true
         * @return this
         */
        fun daemon(daemon: Boolean) = apply {
            this.daemon = daemon
        }

        /**
         * Uncaught exception handler.
         * @param exceptionHandler handler, default {@link Throwables#uncaughtHandler()}
         * @return this
         */
        fun exceptionHandler(exceptionHandler: Thread.UncaughtExceptionHandler) = apply {
            this.exceptionHandler = exceptionHandler
        }

        /**
         * Rejected execution handler.
         * @param rejectedHandler rejected execution handler, default {@link ThreadPoolExecutor.AbortPolicy()}
         * @return this
         */
        fun rejectedHandler(rejectedHandler: RejectedExecutionHandler) = apply {
            this.rejectedHandler = rejectedHandler
        }

        fun coreSize(coreSize: Int) = apply {
            this.coreSize = coreSize
        }

        fun maxSize(maxSize: Int) = apply {
            this.maxSize = maxSize
        }

        fun keepAlive(keepAlive: Long) = apply {
            this.keepAlive = keepAlive
        }

        fun queueSize(queueSize: Int) = apply {
            this.queueSize = queueSize
        }

        fun build() : ExecutorService  {
            val tf = ThreadFactoryImpl(name, daemon, exceptionHandler)
            return ThreadPoolExecutor(coreSize,
                    maxSize,
                    keepAlive,
                    TimeUnit.SECONDS,
                    ArrayBlockingQueue<Runnable>(queueSize),
                    tf,
                    rejectedHandler)
        }
    }

    fun executorBuilder(): ExecutorBuilder {
        return ExecutorBuilder()
    }

    /**
     * Make single daemon thread scheduled executor service.
     * @param clazz class, it user for calculation name of thread
     * @return executor service
     */
    fun singleThreadScheduledExecutor(clazz: Class<*>): ScheduledExecutorService {
        return Executors.newSingleThreadScheduledExecutor(ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat(clazz.simpleName + "-%d")
          .build())
    }
}
